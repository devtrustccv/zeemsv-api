package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.generic.service.EmailService;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorDetailResponseDTO;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.domain.documento.business.DocumentViewerUrlService;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.domain.documento.dto.UploadDTO;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.TNotificacaoEntity;
import cv.zeemsv.api.infrastructure.entity.TNotificacaoRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTEmailsEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTOrdemEntity;
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
import cv.zeemsv.api.infrastructure.repository.ZeeTOrdemRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTParamReportRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPedidoAcessoInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTRepresInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
    private final ZeeTOrdemRepository ordemRepository;
    private final ZeeTParamReportRepository paramReportRepository;
    private final ZeeTEmailsRepository emailsRepository;
    private final ZeeTSocioRepresRepository socioRepresRepository;
    private final ZeeTRepresInvestidorRepository represInvestidorRepository;
    private final ZeeTConfigTemplateNotifRepository templateNotifRepository;
    private final TNotificacaoRepository notificacaoRepository;
    private final TNotificacaoRelacaoRepository notificacaoRelacaoRepository;
    private final DocumentoBus documentoBus;
    private final DocumentViewerUrlService documentViewerUrlService;
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

        resolveInvestidorByNifEntidade(dto);
        String tipoPedido = resolveTipoPedido(dto);
        ZeeTInvestidorEntity investidor = null;
        if (dto.getIdInvestidor() != null) {
            investidor = investidorRepository.findById(dto.getIdInvestidor())
                .orElseThrow(() -> new BusinessException("Investidor nao encontrado."));
        }

        Integer idSocioRepres = dto.getIdSocioRepres();
        Integer idOrdem = dto.getIdOrdem();
        boolean shouldValidatePedidoRepresentanteDuplicado = idSocioRepres != null || idOrdem != null;
        if (TIPO_PEDIDO_REPRES_INVESTIDOR.equals(tipoPedido)) {
            if (dto.getIdInvestidor() == null) {
                throw new BusinessException("O campo id_investidor e obrigatorio para pedido REPRES_INVESTIDOR.");
            }
            if (idOrdem != null && !ordemRepository.existsById(idOrdem)) {
                throw new BusinessException("Ordem nao encontrada.");
            }
            if (idOrdem == null) {
                idSocioRepres = resolveSocioRepres(dto);
            }
            if (shouldValidatePedidoRepresentanteDuplicado
                && existsPedidoRepresentanteNaoRejeitado(idSocioRepres, idOrdem, dto.getIdInvestidor())) {
                throw new BusinessException("Ja existe pedido de acesso para este socio/representante ou ordem e investidor.");
            }
            if (idSocioRepres != null || idOrdem != null) {
                validateRepresentanteNotAssociated(dto.getIdInvestidor(), idSocioRepres, idOrdem);
            }
            createRepresInvestidorPendente(dto, idSocioRepres, idOrdem);
        } else if (dto.getIdInvestidor() != null && repository.existsNaoRejeitadoByIdUtilizadorAndIdInvestidor(dto.getIdUser(), dto.getIdInvestidor())) {
            throw new BusinessException("Ja existe pedido de acesso para este utilizador e investidor.");
        }

        ZeeTPedidoAcessoInvestidorEntity entity = new ZeeTPedidoAcessoInvestidorEntity();
        entity.setIdUtilizador(dto.getIdUser());
        entity.setIdInvestidor(dto.getIdInvestidor());
        entity.setDmTipoPedido(tipoPedido);
        entity.setIdSocioRepres(idSocioRepres);
        entity.setIdOrdem(idOrdem);
        entity.setNifEntidade(trim(dto.getNifEntidade()));
        entity.setDenominacaoEntidade(trim(dto.getDenominacaoEntidade()));
        entity.setEmailContactoEntidade(trim(dto.getEmailContactoEntidade()));
        entity.setTelemovelContactoEntidade(dto.getTelemovelContactoEntidade());
        entity.setDmTpRepresentante(trim(dto.getDmTpRepresentante()));
        entity.setDmEstado(ESTADO_PENDENTE);
        entity.setObs(trim(dto.getObs()));
        entity.setDataRegisto(LocalDate.now());

        try {
            ZeeTPedidoAcessoInvestidorEntity saved = repository.save(entity);
            UploadDTO upload = buildUpload(saved, ficheiroCompravativo);
            documentoBus.saveOrUpdate(upload, String.valueOf(dto.getIdUser()));
            saved.setFicheiroCompravativo(upload.getZeeTDocRelacao().getPath());
            ZeeTPedidoAcessoInvestidorEntity savedWithFile = repository.save(saved);
            notifyTecnicos(savedWithFile, investidor);
            notifyUtilizador(savedWithFile, user, dto.getEmail(), dto.getEmailContactoEntidade());
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

    @Override
    @Transactional(readOnly = true)
    public PedidoAcessoInvestidorDetailResponseDTO findDetailById(Integer id) {
        ZeeTPedidoAcessoInvestidorEntity pedido = repository.findById(id)
            .orElseThrow(() -> new BusinessException("Pedido de acesso nao encontrado."));

        PedidoAcessoInvestidorDetailResponseDTO dto = new PedidoAcessoInvestidorDetailResponseDTO();
        dto.setPedido(toResponseWithFileContent(pedido));
        dto.setUtilizador(userRepository.findById(pedido.getIdUtilizador()).map(this::toUtilizador).orElse(null));
        dto.setInvestidor(pedido.getIdInvestidor() != null
            ? investidorRepository.findById(pedido.getIdInvestidor()).map(this::toInvestidor).orElse(null)
            : null);
        dto.setSocioRepresentante(pedido.getIdSocioRepres() != null
            ? socioRepresRepository.findById(pedido.getIdSocioRepres()).map(this::toSocioRepresentante).orElse(null)
            : null);
        dto.setOrdem(pedido.getIdOrdem() != null
            ? ordemRepository.findById(pedido.getIdOrdem()).map(this::toOrdem).orElse(null)
            : null);
        return dto;
    }

    private void resolveInvestidorByNifEntidade(PedidoAcessoInvestidorRequestDTO dto) {
        if (dto.getIdInvestidor() != null || !StringUtils.hasText(dto.getNifEntidade())) {
            return;
        }

        List<ZeeTInvestidorEntity> investidores = investidorRepository.findByNif(trim(dto.getNifEntidade()));
        if (investidores.isEmpty()) {
            return;
        }
        if (investidores.size() > 1) {
            throw new BusinessException("Existe mais de um investidor local com o NIF informado.");
        }
        dto.setIdInvestidor(investidores.get(0).getId());
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    private String resolveTipoPedido(PedidoAcessoInvestidorRequestDTO dto) {
        if ((dto.getIdSocioRepres() != null || dto.getIdOrdem() != null) && dto.getIdInvestidor() != null) {
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
        request.setFotoUrl(dto.getFotoUrl());
        request.setIndicativoPais(dto.getIndicativoPais());
        request.setEndereco(dto.getEndereco());
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

    private boolean existsPedidoRepresentanteNaoRejeitado(Integer idSocioRepres, Integer idOrdem, Integer idInvestidor) {
        boolean socioDuplicado = idSocioRepres != null
            && repository.existsNaoRejeitadoByIdSocioRepresAndIdInvestidor(idSocioRepres, idInvestidor);
        boolean ordemDuplicada = idOrdem != null
            && repository.existsNaoRejeitadoByIdOrdemAndIdInvestidor(idOrdem, idInvestidor);
        return socioDuplicado || ordemDuplicada;
    }

    private void validateRepresentanteNotAssociated(Integer idInvestidor, Integer idSocioRepres, Integer idOrdem) {
        boolean socioAssociated = idSocioRepres != null
            && represInvestidorRepository.findAssociation(idInvestidor, idSocioRepres, null).isPresent();
        boolean ordemAssociated = idOrdem != null
            && represInvestidorRepository.findAssociation(idInvestidor, null, idOrdem).isPresent();
        if (socioAssociated || ordemAssociated) {
            throw new BusinessException("Socio/representante ou ordem ja associado ao investidor.");
        }
    }

    private ZeeTSocioRepresEntity findSocioRepres(PedidoAcessoInvestidorRequestDTO dto) {
        if (dto.getIdSocioRepres() != null) {
            return socioRepresRepository.findById(dto.getIdSocioRepres())
                .orElseThrow(() -> new BusinessException("Socio/representante nao encontrado."));
        }
        List<ZeeTSocioRepresEntity> matches = new ArrayList<>();
        matches.addAll(findSocioRepresByNif(dto.getNif()));
        matches.addAll(findSocioRepresByNrDoc(dto.getNrDoc()));
        matches.addAll(findSocioRepresByEmail(dto.getEmail()));

        if (matches.isEmpty()) {
            return null;
        }
        if (matches.stream().map(ZeeTSocioRepresEntity::getId).filter(Objects::nonNull).distinct().count() > 1) {
            throw new BusinessException("Os dados informados pertencem a socios/representantes diferentes.");
        }
        return matches.iterator().next();
    }

    private List<ZeeTSocioRepresEntity> findSocioRepresByNif(String nif) {
        if (!StringUtils.hasText(nif)) {
            return List.of();
        }
        return socioRepresRepository.findByNif(trim(nif));
    }

    private List<ZeeTSocioRepresEntity> findSocioRepresByNrDoc(String nrDoc) {
        if (!StringUtils.hasText(nrDoc)) {
            return List.of();
        }
        return socioRepresRepository.findByNrDoc(trim(nrDoc));
    }

    private List<ZeeTSocioRepresEntity> findSocioRepresByEmail(String email) {
        if (!StringUtils.hasText(email)) {
            return List.of();
        }
        return socioRepresRepository.findByEmailIgnoreCase(trim(email));
    }

    private void createRepresInvestidorPendente(PedidoAcessoInvestidorRequestDTO dto, Integer idSocioRepres, Integer idOrdem) {
        ZeeTRepresInvestidorEntity represInvestidor = new ZeeTRepresInvestidorEntity();
        represInvestidor.setIdInvestidor(dto.getIdInvestidor());
        represInvestidor.setIdSocioRepres(idSocioRepres);
        represInvestidor.setIdOrdem(idOrdem);
        represInvestidor.setDmTpRepresentante(firstText(dto.getDmTpRepresentante(), TIPO_SOCIO));
        represInvestidor.setFlagRepresentante(true);
        represInvestidor.setFlagSocio(TIPO_SOCIO.equalsIgnoreCase(firstText(dto.getDmTpRepresentante(), TIPO_SOCIO)));
        represInvestidor.setDmEstado(ESTADO_PENDENTE);
        represInvestidor.setDataRegisto(LocalDate.now());
        represInvestidor.setUserRegisto(BigDecimal.valueOf(dto.getIdUser()));
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
            pedido.getNifEntidade(),
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

    private void notifyUtilizador(ZeeTPedidoAcessoInvestidorEntity pedido, ZeeTUserEntity user, String... payloadEmails) {
        if (user == null) {
            log.info("Utilizador nao informado para notificacao de pedido de acesso {}.", pedido.getId());
            return;
        }

        String tipoPedido = firstText(
            domainHelper.describe(DomainDescriptionHelper.TIPO_PEDIDO_ACESSO, pedido.getDmTipoPedido()),
            pedido.getDmTipoPedido()
        );
        TemplateContent content = resolvePedidoAcessoTemplate(tipoPedido, pedido.getId());
        Set<String> recipients = resolveUtilizadorEmails(user.getEmail(), payloadEmails);
        if (recipients.isEmpty()) {
            log.info("Nenhum email de utilizador/contacto para notificacao de pedido de acesso {}.", pedido.getId());
            return;
        }
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
            notificacao.setEmail(recipients.iterator().next());
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

    private Set<String> resolveUtilizadorEmails(String userEmail, String... payloadEmails) {
        Set<String> emails = new LinkedHashSet<>();
        if (StringUtils.hasText(userEmail)) {
            emails.add(userEmail.trim());
        }
        if (payloadEmails != null) {
            for (String payloadEmail : payloadEmails) {
                if (StringUtils.hasText(payloadEmail) && emails.stream().noneMatch(email -> email.equalsIgnoreCase(payloadEmail.trim()))) {
                    emails.add(payloadEmail.trim());
                }
            }
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
        dto.setTipoPedidoDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_PEDIDO_ACESSO, entity.getDmTipoPedido()));
        dto.setIdSocioRepres(entity.getIdSocioRepres());
        dto.setIdOrdem(entity.getIdOrdem());
        dto.setNifEntidade(entity.getNifEntidade());
        dto.setDenominacaoEntidade(entity.getDenominacaoEntidade());
        dto.setEmailContactoEntidade(entity.getEmailContactoEntidade());
        dto.setTelemovelContactoEntidade(entity.getTelemovelContactoEntidade());
        dto.setDmTpRepresentante(entity.getDmTpRepresentante());
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, entity.getDmTpRepresentante()));
        dto.setFicheiroCompravativo(documentViewerUrlService.toViewerUrl(entity.getFicheiroCompravativo()));
        dto.setDmEstado(entity.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getDmEstado()));
        dto.setObs(entity.getObs());
        dto.setDataRegisto(entity.getDataRegisto());
        dto.setDataResposta(entity.getDataResposta());
        dto.setUserResposta(entity.getUserResposta());
        dto.setUserTask(entity.getUserTask());
        return dto;
    }

    private PedidoAcessoInvestidorResponseDTO toResponseWithFileContent(ZeeTPedidoAcessoInvestidorEntity entity) {
        PedidoAcessoInvestidorResponseDTO dto = toResponse(entity);
        return dto;
    }

    private PedidoAcessoInvestidorDetailResponseDTO.UtilizadorDTO toUtilizador(ZeeTUserEntity entity) {
        PedidoAcessoInvestidorDetailResponseDTO.UtilizadorDTO dto = new PedidoAcessoInvestidorDetailResponseDTO.UtilizadorDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setDmEstado(entity.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getDmEstado()));
        dto.setOrigem(entity.getOrigem());
        dto.setOrigemDesc(domainHelper.describe(DomainDescriptionHelper.ORIGEM, entity.getOrigem()));
        dto.setOnboardingRealizado(entity.getOnboardingRealizado());
        dto.setDataOnboarding(entity.getDataOnboarding());
        dto.setDataRegisto(entity.getDataRegisto());
        dto.setPessoaId(entity.getPessoaId());
        return dto;
    }

    private PedidoAcessoInvestidorDetailResponseDTO.InvestidorDTO toInvestidor(ZeeTInvestidorEntity entity) {
        PedidoAcessoInvestidorDetailResponseDTO.InvestidorDTO dto = new PedidoAcessoInvestidorDetailResponseDTO.InvestidorDTO();
        dto.setId(entity.getId());
        dto.setDenominacao(entity.getDenominacao());
        dto.setMatricula(entity.getMatricula());
        dto.setDmNaturezaJuridica(entity.getDmNaturezaJuridica());
        dto.setDmNaturezaJuridicaDesc(domainHelper.describe(DomainDescriptionHelper.NATUREZA_JURIDICA, entity.getDmNaturezaJuridica()));
        dto.setSetor(entity.getSetor());
        dto.setSede(entity.getSede());
        dto.setDmClassificacao(entity.getDmClassificacao());
        dto.setDmClassificacaoDesc(domainHelper.describe(DomainDescriptionHelper.CLASSIFICACAO, entity.getDmClassificacao()));
        dto.setDataConstituicao(entity.getDataConstituicao());
        dto.setPhone(entity.getPhone());
        dto.setIndicativoPais(entity.getIndicativoPais());
        dto.setTelemovel(entity.getTelemovel());
        dto.setEmail(entity.getEmail());
        dto.setSite(entity.getSite());
        dto.setFlagRec(entity.getFlagRec());
        dto.setDmEstado(entity.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getDmEstado()));
        dto.setLinkRegComercial(documentViewerUrlService.toViewerUrl(entity.getLinkRegComercial()));
        dto.setDateCreate(entity.getDateCreate());
        dto.setUserCreate(entity.getUserCreate());
        dto.setDateUpdate(entity.getDateUpdate());
        dto.setUserUpdate(entity.getUserUpdate());
        dto.setFormaObrigar(entity.getFormaObrigar());
        dto.setCapitalSocial(entity.getCapitalSocial());
        dto.setPaisOrigem(entity.getPaisOrigem());
        dto.setEndereco(entity.getEndereco());
        dto.setNif(entity.getNif());
        dto.setFlagServico(entity.getFlagServico());
        dto.setDmIdioma(entity.getDmIdoma());
        dto.setDmIdiomaDesc(domainHelper.describe(DomainDescriptionHelper.IDIOMA, entity.getDmIdoma()));
        dto.setDmTipoInvestidor(entity.getDmTipoInvestidor());
        dto.setDmTipoInvestidorDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_INVESTIDOR, entity.getDmTipoInvestidor()));
        dto.setDmGenero(entity.getDmGenero());
        dto.setDmGeneroDesc(domainHelper.describe(DomainDescriptionHelper.GENERO, entity.getDmGenero()));
        dto.setDataNascimento(entity.getDataNascimento());
        dto.setDmEstadoCivil(entity.getDmEstadoCivil());
        dto.setDmEstadoCivilDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_CIVIL, entity.getDmEstadoCivil()));
        dto.setProfissao(entity.getProfissao());
        dto.setNrDocumento(entity.getNrDocumento());
        dto.setMoeda(entity.getMoeda());
        dto.setMoedaDesc(domainHelper.describe(DomainDescriptionHelper.MOEDA, entity.getMoeda()));
        return dto;
    }

    private PedidoAcessoInvestidorDetailResponseDTO.SocioRepresentanteDTO toSocioRepresentante(ZeeTSocioRepresEntity entity) {
        PedidoAcessoInvestidorDetailResponseDTO.SocioRepresentanteDTO dto = new PedidoAcessoInvestidorDetailResponseDTO.SocioRepresentanteDTO();
        dto.setId(entity.getId());
        dto.setIdInvestidor(entity.getIdInvestidor());
        dto.setNome(entity.getNome());
        dto.setNacionalidade(entity.getNacionalidade());
        dto.setNif(entity.getNif());
        dto.setTipoDoc(entity.getTipoDoc());
        dto.setTipoDocDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_DOCUMENTO, entity.getTipoDoc()));
        dto.setNrDoc(entity.getNrDoc());
        dto.setDmTpRepresentante(entity.getDmTpRepresentante());
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, entity.getDmTpRepresentante()));
        dto.setTelefone(entity.getTelefone());
        dto.setTelemovel(entity.getTelemovel());
        dto.setEmail(entity.getEmail());
        dto.setFotoUrl(firstText(entity.getFotoUrl(), documentViewerUrlService.toViewerUrl(entity.getFotoPath())));
        dto.setFlagSocio(entity.getFlagSocio());
        dto.setFlagRepresentante(entity.getFlagRepresentante());
        dto.setDmPrincipal(entity.getDmPrincipal());
        dto.setDmPrincipalDesc(domainHelper.describe(DomainDescriptionHelper.SIM_NAO, entity.getDmPrincipal()));
        dto.setEstado(entity.getEstado());
        dto.setEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getEstado()));
        dto.setDateCreate(entity.getDateCreate());
        dto.setUserCreate(entity.getUserCreate());
        dto.setIndicativoPais(entity.getIndicativoPais());
        dto.setEndereco(entity.getEndereco());
        dto.setIdUser(entity.getIdUser());
        return dto;
    }

    private PedidoAcessoInvestidorDetailResponseDTO.OrdemDTO toOrdem(ZeeTOrdemEntity entity) {
        PedidoAcessoInvestidorDetailResponseDTO.OrdemDTO dto = new PedidoAcessoInvestidorDetailResponseDTO.OrdemDTO();
        dto.setId(entity.getId());
        dto.setTipoOrdem(entity.getTipoOrdem());
        dto.setTipoOrdemDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_ORDEM, entity.getTipoOrdem()));
        dto.setNome(entity.getNome());
        dto.setCedula(entity.getCedula());
        dto.setConcelho(entity.getConcelho());
        dto.setEndereco(entity.getEndereco());
        dto.setEmail(entity.getEmail());
        dto.setIndicativoPais(entity.getIndicativoPais());
        dto.setTelemovel(entity.getTelemovel());
        dto.setNif(entity.getNif());
        dto.setNrDocumento(entity.getNrDocumento());
        dto.setNacionalidade(entity.getNacionalidade());
        dto.setNumeroInscricao(entity.getNumeroInscricao());
        dto.setEspecialidade(entity.getEspecialidade());
        dto.setDmEstado(entity.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getDmEstado()));
        dto.setDataRegisto(entity.getDataRegisto());
        dto.setUserRegisto(entity.getUserRegisto());
        dto.setDmTpDoc(entity.getDmTpDoc());
        dto.setDmTpDocDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_DOCUMENTO, entity.getDmTpDoc()));
        return dto;
    }
}
