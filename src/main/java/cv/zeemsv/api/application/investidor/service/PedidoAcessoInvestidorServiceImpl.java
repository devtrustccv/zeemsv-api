package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.generic.service.EmailService;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.domain.documento.dto.UploadDTO;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.TNotificacaoEntity;
import cv.zeemsv.api.infrastructure.entity.TNotificacaoRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTEmailsEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPedidoAcessoInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTParamReportEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTRepresInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTUserEntity;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTConfigTemplateNotifRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTEmailsRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTParamReportRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPedidoAcessoInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTRepresInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Log4j2
public class PedidoAcessoInvestidorServiceImpl implements PedidoAcessoInvestidorService {
    private static final String TIPO_RELACAO_PEDIDO_ACESSO = "PEDIDO_ACESSO";
    private static final String NOME_FICHEIRO_COMPRAVATIVO = "ficheiro_compravativo";
    private static final String ESTADO_PENDENTE = "PENDENTE";
    private static final String ESTADO_ATIVO = "A";
    private static final String TIPO_PEDIDO_REPRES_INVESTIDOR = "REPRES_INVESTIDOR";
    private static final String TIPO_PEDIDO_REGISTO = "REGISTO";
    private static final String TEMPLATE_PEDIDO_ACESSO = "PEDIDO_ACESSO";
    private static final String TIPO_NOTIFICACAO_EMAIL = "EMAIL";
    private static final String RELACAO_UTILIZADOR = "UTILIZADOR";
    private static final String TIPO_SOCIO = "SOCIO";

    private final ZeeTPedidoAcessoInvestidorRepository repository;
    private final ZeeTUserRepository userRepository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final ZeeTParamReportRepository paramReportRepository;
    private final ZeeTEmailsRepository emailsRepository;
    private final ZeeTSocioRepresRepository socioRepresRepository;
    private final ZeeTRepresInvestidorRepository represInvestidorRepository;
    private final ZeeTConfigTemplateNotifRepository templateNotifRepository;
    private final TNotificacaoRepository notificacaoRepository;
    private final TNotificacaoRelacaoRepository notificacaoRelacaoRepository;
    private final DocumentoBus documentoBus;
    private final EmailService emailService;
    private final DomainDescriptionHelper domainHelper;
    private final SocioRepresentanteService socioRepresentanteService;

    @Override
    @Transactional
    public PedidoAcessoInvestidorResponseDTO create(PedidoAcessoInvestidorRequestDTO dto, MultipartFile ficheiroCompravativo) {
        if (ficheiroCompravativo == null || ficheiroCompravativo.isEmpty()) {
            throw new BusinessException("O campo ficheiro_compravativo e obrigatorio.");
        }

        ZeeTUserEntity user = userRepository.findById(dto.getIdUser())
            .orElseThrow(() -> new BusinessException("Utilizador nao encontrado."));

        String tipoPedido = resolveTipoPedido(dto);
        ZeeTInvestidorEntity investidor = null;
        if (dto.getIdInvestidor() != null) {
            investidor = investidorRepository.findById(dto.getIdInvestidor())
                .orElseThrow(() -> new BusinessException("Investidor nao encontrado."));
        }

        Integer idSocioRepres = dto.getIdSocioRepres();
        if (TIPO_PEDIDO_REPRES_INVESTIDOR.equals(tipoPedido)) {
            if (dto.getIdInvestidor() == null) {
                throw new BusinessException("O campo id_investidor e obrigatorio para pedido REPRES_INVESTIDOR.");
            }
            idSocioRepres = resolveSocioRepres(dto);
            if (repository.existsNaoRejeitadoByIdSocioRepresAndIdInvestidor(idSocioRepres, dto.getIdInvestidor())) {
                throw new BusinessException("Ja existe pedido de acesso para este socio/representante e investidor.");
            }
            validateSocioRepresNotAssociated(dto.getIdInvestidor(), idSocioRepres);
            createRepresInvestidorPendente(dto, idSocioRepres);
        } else if (dto.getIdInvestidor() != null && repository.existsNaoRejeitadoByIdUtilizadorAndIdInvestidor(dto.getIdUser(), dto.getIdInvestidor())) {
            throw new BusinessException("Ja existe pedido de acesso para este utilizador e investidor.");
        }

        ZeeTPedidoAcessoInvestidorEntity entity = new ZeeTPedidoAcessoInvestidorEntity();
        entity.setIdUtilizador(dto.getIdUser());
        entity.setIdInvestidor(dto.getIdInvestidor());
        entity.setDmTipoPedido(tipoPedido);
        entity.setIdSocioRepres(idSocioRepres);
        entity.setNifInvestidor(trim(dto.getNifInvestidor()));
        entity.setNifEntidade(trim(dto.getNifEntidade()));
        entity.setDenominacaoEntidade(trim(dto.getDenominacaoEntidade()));
        entity.setDmTpRepresentante(trim(dto.getDmTpRepresentante()));
        entity.setDmEstado(ESTADO_PENDENTE);
        entity.setObs(trim(dto.getObs()));
        entity.setDataRegisto(LocalDate.now());

        try {
            ZeeTPedidoAcessoInvestidorEntity saved = repository.save(entity);
            UploadDTO upload = buildUpload(saved, ficheiroCompravativo);
            documentoBus.saveOrUpdate(upload, String.valueOf(dto.getIdUser()));
            saved.setFicheiroCompravativo(upload.getFullPath());
            ZeeTPedidoAcessoInvestidorEntity savedWithFile = repository.save(saved);
            notifyTecnicos(savedWithFile, investidor);
            notifyUtilizador(savedWithFile, user, dto.getEmail());
            return toResponse(savedWithFile);
        } catch (DataIntegrityViolationException ex) {
            throw new BusinessException("Ja existe pedido de acesso para este utilizador e investidor.", ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PedidoAcessoInvestidorResponseDTO> findByUserId(Integer idUser) {
        if (!userRepository.existsById(idUser)) {
            throw new BusinessException("Utilizador nao encontrado.");
        }

        return repository.findByIdUtilizadorOrderByDataRegistoDescIdDesc(idUser)
            .stream()
            .map(this::toResponseWithFileContent)
            .toList();
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    private String resolveTipoPedido(PedidoAcessoInvestidorRequestDTO dto) {
        if (dto.getIdSocioRepres() != null && dto.getIdInvestidor() != null) {
            return TIPO_PEDIDO_REPRES_INVESTIDOR;
        }
        if (dto.getIdSocioRepres() != null && dto.getIdInvestidor() == null
            && (StringUtils.hasText(dto.getNifEntidade()) || StringUtils.hasText(dto.getDenominacaoEntidade()))) {
            return TIPO_PEDIDO_REGISTO;
        }
        if (StringUtils.hasText(dto.getTipoPedido())) {
            return trim(dto.getTipoPedido());
        }
        throw new BusinessException("Nao foi possivel determinar o tipo de pedido de acesso.");
    }

    private void validateSocioRepresentante(PedidoAcessoInvestidorRequestDTO dto) {
        if (!StringUtils.hasText(dto.getNome())) {
            throw new BusinessException("O campo nome e obrigatorio para pedido REPRES_INVESTIDOR.");
        }
        if (!StringUtils.hasText(dto.getEmail())) {
            throw new BusinessException("O campo email e obrigatorio para pedido REPRES_INVESTIDOR.");
        }
    }

    private SocioRepresentanteRequestDTO toSocioRepresentanteRequest(PedidoAcessoInvestidorRequestDTO dto) {
        SocioRepresentanteRequestDTO request = new SocioRepresentanteRequestDTO();
        request.setNome(dto.getNome());
        request.setNacionalidade(dto.getNacionalidade());
        request.setNif(dto.getNif());
        request.setTipoDoc(dto.getTipoDoc());
        request.setNrDoc(dto.getNrDoc());
        request.setTelefone(dto.getTelefone());
        request.setTelemovel(dto.getTelemovel());
        request.setEmail(dto.getEmail());
        request.setIndicativoPais(dto.getIndicativoPais());
        return request;
    }

    private Integer resolveSocioRepres(PedidoAcessoInvestidorRequestDTO dto) {
        ZeeTSocioRepresEntity existing = findSocioRepres(dto);
        if (existing != null) {
            return existing.getId();
        }

        validateSocioRepresentante(dto);
        SocioRepresentanteResponseDTO socioRepresentante = socioRepresentanteService.createPendente(toSocioRepresentanteRequest(dto));
        return socioRepresentante.getId();
    }

    private void validateSocioRepresNotAssociated(Integer idInvestidor, Integer idSocioRepres) {
        if (represInvestidorRepository.findAssociation(idInvestidor, idSocioRepres, null).isPresent()) {
            throw new BusinessException("Socio/representante ja associado ao investidor.");
        }
    }

    private ZeeTSocioRepresEntity findSocioRepres(PedidoAcessoInvestidorRequestDTO dto) {
        if (dto.getIdSocioRepres() != null) {
            return socioRepresRepository.findById(dto.getIdSocioRepres())
                .orElseThrow(() -> new BusinessException("Socio/representante nao encontrado."));
        }
        return findSocioRepresByNif(dto.getNif());
    }

    private ZeeTSocioRepresEntity findSocioRepresByNif(String nif) {
        if (!StringUtils.hasText(nif)) {
            return null;
        }
        return socioRepresRepository.findByNif(trim(nif)).stream()
            .findFirst()
            .orElse(null);
    }

    private void createRepresInvestidorPendente(PedidoAcessoInvestidorRequestDTO dto, Integer idSocioRepres) {
        ZeeTSocioRepresEntity socioRepres = socioRepresRepository.findById(idSocioRepres)
            .orElseThrow(() -> new BusinessException("Socio/representante nao encontrado."));

        ZeeTRepresInvestidorEntity represInvestidor = new ZeeTRepresInvestidorEntity();
        represInvestidor.setIdInvestidor(dto.getIdInvestidor());
        represInvestidor.setIdSocioRepres(idSocioRepres);
        represInvestidor.setDmTpRepresentante(firstText(dto.getDmTpRepresentante(), TIPO_SOCIO));
        represInvestidor.setFlagRepresentante(true);
        represInvestidor.setFlagSocio(TIPO_SOCIO.equalsIgnoreCase(firstText(dto.getDmTpRepresentante(), TIPO_SOCIO)));
        represInvestidor.setDmEstado(ESTADO_PENDENTE);
        represInvestidor.setDataRegisto(LocalDate.now());
        represInvestidor.setUserRegisto(BigDecimal.valueOf(dto.getIdUser()));
        represInvestidor.setIdUser(socioRepres.getIdUser());
        represInvestidorRepository.save(represInvestidor);
    }

    private void notifyTecnicos(ZeeTPedidoAcessoInvestidorEntity pedido, ZeeTInvestidorEntity investidor) {
        Set<String> recipients = resolveTecnicoEmails();
        if (recipients.isEmpty()) {
            log.info("Nenhum email tecnico configurado para notificacao de pedido de acesso {}.", pedido.getId());
            return;
        }

        String tipoPedido = firstText(
            domainHelper.describe(DomainDescriptionHelper.TIPO_PEDIDO_ACESSO, pedido.getDmTipoPedido()),
            pedido.getDmTipoPedido()
        );
        String estadoPedido = firstText(
            domainHelper.describe(DomainDescriptionHelper.ESTADO_PEDIDO, pedido.getDmEstado()),
            pedido.getDmEstado()
        );
        String entidade = firstText(
            pedido.getDenominacaoEntidade(),
            investidor != null ? investidor.getDenominacao() : null,
            pedido.getNifInvestidor(),
            "entidade nao identificada"
        );
        String subject = "Nova submissao de pedido de acesso - Pedido no " + pedido.getId();
        String body = "Caro Tecnico da ZEEMSV, informamos que foi submetido um pedido de acesso"
            + " do tipo " + tipoPedido
            + " para " + entidade
            + ". Estado atual: " + estadoPedido
            + ". Por favor consulte a lista de pedidos de Acesso/ Manifestacao de Interesse para mais informacoes.";

        for (String recipient : recipients) {
            try {
                emailService.sendText(recipient, subject, body);
            } catch (RuntimeException ex) {
                log.error("Erro ao enviar notificacao de pedido de acesso {} para {}.", pedido.getId(), recipient, ex);
            }
        }
    }

    private void notifyUtilizador(ZeeTPedidoAcessoInvestidorEntity pedido, ZeeTUserEntity user, String payloadEmail) {
        if (user == null || !StringUtils.hasText(user.getEmail())) {
            log.info("Utilizador sem email para notificacao de pedido de acesso {}.", pedido.getId());
            return;
        }

        String tipoPedido = firstText(
            domainHelper.describe(DomainDescriptionHelper.TIPO_PEDIDO_ACESSO, pedido.getDmTipoPedido()),
            pedido.getDmTipoPedido()
        );
        TemplateContent content = resolvePedidoAcessoTemplate(tipoPedido, pedido.getId());
        Set<String> recipients = resolveUtilizadorEmails(user.getEmail(), payloadEmail);
        boolean sent = true;
        for (String recipient : recipients) {
            sent = sendEmailSafely(recipient, content.subject(), content.body(), pedido.getId()) && sent;
        }
        String emailsEnviados = String.join(",", recipients);

        try {
            TNotificacaoEntity notificacao = new TNotificacaoEntity();
            notificacao.setIdAplicacao(BigDecimal.ZERO);
            notificacao.setIdOrganica(BigDecimal.ZERO);
            notificacao.setUserRegisto(BigDecimal.valueOf(user.getId()));
            notificacao.setDataRegisto(LocalDate.now());
            notificacao.setAssunto(content.subject());
            notificacao.setDataEnvio(LocalDateTime.now());
            notificacao.setMensagem(content.body());
            notificacao.setEmail(user.getEmail());
            notificacao.setEstado(ESTADO_PENDENTE);
            notificacao.setFlagAutomatico("S");
            notificacao.setFlagSucesso(sent ? "S" : "N");
            notificacao.setFlagLeitura("N");
            notificacao.setNumeroReenvios(BigDecimal.ZERO);
            notificacao.setTipo(TIPO_NOTIFICACAO_EMAIL);
            notificacao.setIdRelacao(user.getId());
            notificacao.setEmailsEnviados(emailsEnviados);
            notificacao.setConfirmRecebimento(false);

            TNotificacaoEntity saved = notificacaoRepository.save(notificacao);
            saveNotificacaoRelacao(saved.getId(), RELACAO_UTILIZADOR, user.getId());
        } catch (RuntimeException ex) {
            log.error("Erro ao gravar notificacao do pedido de acesso {} para utilizador {}.", pedido.getId(), user.getId(), ex);
        }
    }

    private Set<String> resolveUtilizadorEmails(String userEmail, String payloadEmail) {
        Set<String> emails = new LinkedHashSet<>();
        if (StringUtils.hasText(userEmail)) {
            emails.add(userEmail.trim());
        }
        if (StringUtils.hasText(payloadEmail) && emails.stream().noneMatch(email -> email.equalsIgnoreCase(payloadEmail.trim()))) {
            emails.add(payloadEmail.trim());
        }
        return emails;
    }

    private TemplateContent resolvePedidoAcessoTemplate(String tipoPedido, Integer nrPedido) {
        return templateNotifRepository.findFirstByCodigoAndDmEstadoOrderByIdDesc(TEMPLATE_PEDIDO_ACESSO, ESTADO_ATIVO)
            .map(template -> new TemplateContent(
                replaceTemplate(template.getAssunto(), tipoPedido, nrPedido),
                replaceTemplate(template.getTemplateMsg(), tipoPedido, nrPedido)
            ))
            .orElseGet(() -> new TemplateContent(
                "Pedido de acesso submetido",
                "O seu pedido de acesso do tipo " + tipoPedido + " foi submetido com o numero " + nrPedido + "."
            ));
    }

    private String replaceTemplate(String template, String tipoPedido, Integer nrPedido) {
        String value = StringUtils.hasText(template) ? template : "";
        return value
            .replace("${tipoPedido}", tipoPedido)
            .replace("{tipoPedido}", tipoPedido)
            .replace("#tipoPedido#", tipoPedido)
            .replace(":tipoPedido", tipoPedido)
            .replace("${nrPedido}", String.valueOf(nrPedido))
            .replace("{nrPedido}", String.valueOf(nrPedido))
            .replace("#nrPedido#", String.valueOf(nrPedido))
            .replace(":nrPedido", String.valueOf(nrPedido));
    }

    private boolean sendEmailSafely(String recipient, String subject, String body, Integer pedidoId) {
        try {
            emailService.sendText(recipient, subject, body);
            return true;
        } catch (RuntimeException ex) {
            log.error("Erro ao enviar notificacao de pedido de acesso {} para utilizador {}.", pedidoId, recipient, ex);
            return false;
        }
    }

    private void saveNotificacaoRelacao(Integer idNotificacao, String tpRelacao, Integer idRelacao) {
        TNotificacaoRelacaoEntity relacao = new TNotificacaoRelacaoEntity();
        relacao.setIdNotificacao(idNotificacao);
        relacao.setTpRelacao(tpRelacao);
        relacao.setIdRelacao(BigDecimal.valueOf(idRelacao));
        notificacaoRelacaoRepository.save(relacao);
    }

    private Set<String> resolveTecnicoEmails() {
        Set<String> emails = new LinkedHashSet<>();
        paramReportRepository.findAll().stream()
            .map(ZeeTParamReportEntity::getEmail)
            .filter(StringUtils::hasText)
            .map(String::trim)
            .forEach(emails::add);
        emailsRepository.findAll().stream()
            .map(ZeeTEmailsEntity::getEmail)
            .filter(StringUtils::hasText)
            .map(String::trim)
            .forEach(emails::add);
        return emails;
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private record TemplateContent(String subject, String body) {
    }

    private UploadDTO buildUpload(ZeeTPedidoAcessoInvestidorEntity pedidoAcesso, MultipartFile ficheiroCompravativo) {
        ZeeTDocRelacaoEntity docRelacao = new ZeeTDocRelacaoEntity();
        docRelacao.setTipoRelacao(TIPO_RELACAO_PEDIDO_ACESSO);
        docRelacao.setIdRelacao(BigDecimal.valueOf(pedidoAcesso.getId()));

        String basePath = DocumentoBus.getBasePathForModuloOrObject(
            TIPO_RELACAO_PEDIDO_ACESSO,
            pedidoAcesso.getId().toString()
        );
        return new UploadDTO(ficheiroCompravativo, NOME_FICHEIRO_COMPRAVATIVO, basePath, docRelacao);
    }

    private PedidoAcessoInvestidorResponseDTO toResponse(ZeeTPedidoAcessoInvestidorEntity entity) {
        PedidoAcessoInvestidorResponseDTO dto = new PedidoAcessoInvestidorResponseDTO();
        dto.setId(entity.getId());
        dto.setIdUser(entity.getIdUtilizador());
        dto.setIdInvestidor(entity.getIdInvestidor());
        dto.setTipoPedido(entity.getDmTipoPedido());
        dto.setIdSocioRepres(entity.getIdSocioRepres());
        dto.setNifInvestidor(entity.getNifInvestidor());
        dto.setNifEntidade(entity.getNifEntidade());
        dto.setDenominacaoEntidade(entity.getDenominacaoEntidade());
        dto.setDmTpRepresentante(entity.getDmTpRepresentante());
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, entity.getDmTpRepresentante()));
        dto.setFicheiroCompravativo(entity.getFicheiroCompravativo());
        dto.setDmEstado(entity.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getDmEstado()));
        dto.setObs(entity.getObs());
        dto.setDataRegisto(entity.getDataRegisto());
        dto.setDataResposta(entity.getDataResposta());
        dto.setUserResposta(entity.getUserResposta());
        return dto;
    }

    private PedidoAcessoInvestidorResponseDTO toResponseWithFileContent(ZeeTPedidoAcessoInvestidorEntity entity) {
        PedidoAcessoInvestidorResponseDTO dto = toResponse(entity);
        dto.setFicheiroCompravativoBytes(documentoBus.getDocContentByPath(entity.getFicheiroCompravativo()));
        return dto;
    }
}
