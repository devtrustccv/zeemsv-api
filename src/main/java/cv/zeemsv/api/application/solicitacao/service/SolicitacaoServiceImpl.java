package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.generic.service.EmailService;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocumentosRequisitosResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocumentoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequisitoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequisitoResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoTaxaResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SubmeterSolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.mapper.SolicitacaoDtoMapper;
import cv.zeemsv.api.application.startprocessigrp.service.ProcessStartService;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.domain.documento.business.DocumentViewerUrlService;
import cv.zeemsv.api.domain.documento.dto.UploadDTO;
import cv.zeemsv.api.domain.external.model.StartProcessResponse;
import cv.zeemsv.api.domain.solicitacao.business.SolicitacaoBus;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.TNotificacaoEntity;
import cv.zeemsv.api.infrastructure.entity.TNotificacaoRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.TPedidoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTEmailsEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTLeadPromotorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTParamReportEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoDocEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTpDocEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.TPedidoRepository;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTConfigTemplateNotifRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTEmailsRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTLeadPromotorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTParamReportRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoDocRepository;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTaxaEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicTpDocRepository;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoDocProjection;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoDocumentoConfiguradoProjection;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoInvestidorProjection;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoRequisitoProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class SolicitacaoServiceImpl implements SolicitacaoService {
    private static final String PROCESS_KEY_SOLICITACAO_INVESTIDOR = "proc_solicitacao_investidor";
    private static final String ESTADO_PENDENTE = "PENDENTE";
    private static final String ESTADO_ATIVO = "A";
    private static final String TIPO_RELACAO_SOLICITACAO = "SOLICITACAO";
    private static final String TEMPLATE_SUBMISSAO_SOLICITACAO = "PROC_SOLIC_TASK_SOLIC";
    private static final String TIPO_NOTIFICACAO_EMAIL = "EMAIL";
    private static final String ORIGEM_PORTAL = "PORTAL";

    private final SolicitacaoBus bus;
    private final SolicitacaoDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTSolicitacaoRepository solicitacaoRepository;
    private final ZeeTSolicitacaoDocRepository solicitacaoDocRepository;
    private final ZeeTTpSolicitacaoRepository tpSolicitacaoRepository;
    private final ZeeTTpSolicTaxaRepository tpSolicTaxaRepository;
    private final ZeeTTpSolicTpDocRepository tpSolicTpDocRepository;
    private final TPedidoRepository pedidoRepository;
    private final ZeeTConfigTemplateNotifRepository templateNotifRepository;
    private final TNotificacaoRepository notificacaoRepository;
    private final TNotificacaoRelacaoRepository notificacaoRelacaoRepository;
    private final ZeeTParamReportRepository paramReportRepository;
    private final ZeeTEmailsRepository emailsRepository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final ZeeTLeadPromotorRepository leadPromotorRepository;
    private final DocumentViewerUrlService documentViewerUrlService;
    private final DocumentoBus documentoBus;
    private final ProcessStartService processStartService;
    private final EmailService emailService;

    @Value("${application.reports.recibo-url-template:}")
    private String reciboUrlTemplate;

    @Override @Transactional
    public SolicitacaoResponseDTO create(SolicitacaoRequestDTO dto) { return enrich(mapper.toResponse(bus.create(mapper.toModel(dto)))); }

    @Override
    @Transactional
    public SolicitacaoResponseDTO submeter(SubmeterSolicitacaoRequestDTO dto, String authorization) {
        ZeeTTpSolicitacaoEntity tpSolicitacao = tpSolicitacaoRepository.findById(dto.getIdTpSolicitacao())
            .orElseThrow(() -> new BusinessException("Tipo de solicitacao nao encontrado."));
        if (tpSolicitacao.getIdEntExterna() == null) {
            throw new BusinessException("Tipo de solicitacao sem entidade externa configurada.");
        }
        validateSubmissao(dto);

        StartProcessResponse processo = processStartService.start(PROCESS_KEY_SOLICITACAO_INVESTIDOR, authorization, "{}");
        BigDecimal idProcesso = toBigDecimal(processo.getProcessInstanceId(), "id do processo");
        String idEtapa = firstText(processo.getId(), processo.getTaskDefinitionKey(), processo.getFormKey());
        BigDecimal idEtapaDoc = toBigDecimalOrZero(idEtapa);

        TPedidoEntity pedido = buildPedido(dto, processo, idProcesso, idEtapa);
        pedido = pedidoRepository.save(pedido);

        ZeeTSolicitacaoEntity solicitacao = buildSolicitacao(dto, tpSolicitacao, idProcesso, pedido.getId());
        solicitacao = solicitacaoRepository.save(solicitacao);

        pedido.setIdRelacao(BigDecimal.valueOf(solicitacao.getId()));
        pedidoRepository.save(pedido);

        saveDocumentos(dto.getDocumentos(), solicitacao, idProcesso, idEtapaDoc);
        saveRequisitos(dto.getRequisitos(), solicitacao, idProcesso, idEtapaDoc);
        notifyRequerente(dto, solicitacao, pedido, processo);
        notifyTecnicos(solicitacao, pedido, tpSolicitacao);

        return enrich(mapper.toResponse(bus.findById(solicitacao.getId())));
    }

    @Override @Transactional
    public SolicitacaoResponseDTO update(Integer id, SolicitacaoRequestDTO dto) { return enrich(mapper.toResponse(bus.update(id, mapper.toModel(dto)))); }

    @Override @Transactional(readOnly = true)
    public SolicitacaoResponseDTO findById(Integer id) { return enrich(mapper.toResponse(bus.findById(id))); }

    @Override @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).map(this::enrich).toList(); }

    @Override @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> findByInvestidorId(Integer idInvestidor) {
        return solicitacaoRepository.findDetalheByInvestidorId(idInvestidor).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override @Transactional(readOnly = true)
    public SolicitacaoDocumentosRequisitosResponseDTO findDocumentosByTipoSolicitacaoId(Integer idTpSolicitacao) {
        if (!tpSolicitacaoRepository.existsById(idTpSolicitacao)) {
            throw new BusinessException("Tipo de solicitacao nao encontrado.");
        }
        SolicitacaoDocumentosRequisitosResponseDTO response = new SolicitacaoDocumentosRequisitosResponseDTO();
        response.setDocumentos(tpSolicTpDocRepository.findDocumentosByIdTpSolicitacao(idTpSolicitacao).stream()
            .map(this::toDocumentoResponse)
            .toList());
        response.setRequisitos(tpSolicTpDocRepository.findRequisitosByIdTpSolicitacao(idTpSolicitacao).stream()
            .map(this::toRequisitoResponse)
            .toList());
        response.setTaxas(tpSolicTaxaRepository.findByIdTpSolic(idTpSolicitacao).stream()
            .map(this::toTaxaResponse)
            .toList());
        return response;
    }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private void validateSubmissao(SubmeterSolicitacaoRequestDTO dto) {
        if (dto.getDocumentos() != null) {
            for (SolicitacaoDocumentoRequestDTO documento : dto.getDocumentos()) {
                if (documento.getFicheiro() == null || documento.getFicheiro().isEmpty()) {
                    continue;
                }
                if (documento.getIdTpSolicTpDoc() == null) {
                    throw new BusinessException("Documento sem configuracao informada.");
                }
                ZeeTTpSolicTpDocEntity tpSolicTpDoc = tpSolicTpDocRepository.findById(documento.getIdTpSolicTpDoc())
                    .orElseThrow(() -> new BusinessException("Documento configurado nao encontrado: " + documento.getIdTpSolicTpDoc()));
                if (!Objects.equals(tpSolicTpDoc.getIdTpSolic(), dto.getIdTpSolicitacao())) {
                    throw new BusinessException("Documento nao pertence ao tipo de solicitacao informado.");
                }
            }
        }

        if (dto.getRequisitos() != null) {
            for (SolicitacaoRequisitoRequestDTO requisito : dto.getRequisitos()) {
                if (!Objects.equals(requisito.getCumpre(), requisito.getCumpreCheck())) {
                    continue;
                }
                if (requisito.getIdTpSolicTpDoc() == null) {
                    throw new BusinessException("Requisito sem configuracao informada.");
                }
                ZeeTTpSolicTpDocEntity tpSolicTpDoc = tpSolicTpDocRepository.findById(requisito.getIdTpSolicTpDoc())
                    .orElseThrow(() -> new BusinessException("Requisito configurado nao encontrado: " + requisito.getIdTpSolicTpDoc()));
                if (!Objects.equals(tpSolicTpDoc.getIdTpSolic(), dto.getIdTpSolicitacao())) {
                    throw new BusinessException("Requisito nao pertence ao tipo de solicitacao informado.");
                }
            }
        }
    }

    private TPedidoEntity buildPedido(
        SubmeterSolicitacaoRequestDTO dto,
        StartProcessResponse processo,
        BigDecimal idProcesso,
        String idEtapa
    ) {
        TPedidoEntity pedido = new TPedidoEntity();
        pedido.setDmEstadoPedido(ESTADO_PENDENTE);
        pedido.setDmOrigemReg(resolveOrigem(dto));
        pedido.setDtRegisto(LocalDate.now());
        pedido.setIdTpProcesso(firstText(processo.getProcessDefinitionKey(), PROCESS_KEY_SOLICITACAO_INVESTIDOR));
        pedido.setIdEtapa(idEtapa);
        pedido.setIdUserReg(dto.getIdUser() != null ? BigDecimal.valueOf(dto.getIdUser()) : BigDecimal.ZERO);
        pedido.setIdProcesso(idProcesso);
        pedido.setEmail(dto.getEmail());
        pedido.setIdOrganica(dto.getIdOrganica() != null ? dto.getIdOrganica() : BigDecimal.ZERO);
        pedido.setTipoRelacao(TIPO_RELACAO_SOLICITACAO);
        pedido.setRequerente(firstText(dto.getNomeDenominacao(), dto.getDenominacaoSocial()));
        pedido.setTpProcesso(processo.getProcessName());
        pedido.setEtapaAtual(processo.getTaskDefinitionKey());
        pedido.setCodEtapaAtual(processo.getFormKey());
        return pedido;
    }

    private ZeeTSolicitacaoEntity buildSolicitacao(
        SubmeterSolicitacaoRequestDTO dto,
        ZeeTTpSolicitacaoEntity tpSolicitacao,
        BigDecimal idProcesso,
        Integer idPedido
    ) {
        ZeeTSolicitacaoEntity solicitacao = new ZeeTSolicitacaoEntity();
        solicitacao.setIdTpSolicitacao(tpSolicitacao.getId());
        solicitacao.setIdEntidade(tpSolicitacao.getIdEntExterna());
        solicitacao.setIdOrganica(dto.getIdOrganica() != null ? dto.getIdOrganica() : BigDecimal.ZERO);
        solicitacao.setIdProcesso(idProcesso);
        solicitacao.setIdPromotor(dto.getIdPromotor());
        solicitacao.setIdInvestidor(dto.getIdInvestidor());
        solicitacao.setIdProjeto(dto.getIdProjeto());
        solicitacao.setExposicao(dto.getExposicao());
        solicitacao.setDmOrigem(resolveOrigem(dto));
        solicitacao.setDataSolic(LocalDate.now());
        solicitacao.setUserSolic(firstText(dto.getUserName(), dto.getEmail(), dto.getIdUser() != null ? dto.getIdUser().toString() : null, "system"));
        solicitacao.setDmEstadoProc(ESTADO_PENDENTE);
        solicitacao.setDataPrevResposta(dto.getDataPrevistaResposta());
        solicitacao.setDescSolic(firstText(tpSolicitacao.getDescricao(), tpSolicitacao.getNome()));
        if (dto.getPrazoDias() != null && dto.getPrazoDias() > 0) {
            solicitacao.setPrazoDia(BigDecimal.valueOf(dto.getPrazoDias()));
        } else if (tpSolicitacao.getPrazoDia() != null) {
            solicitacao.setPrazoDia(tpSolicitacao.getPrazoDia());
        }
        solicitacao.setIdPedido(idPedido);
        return solicitacao;
    }

    private void saveDocumentos(
        List<SolicitacaoDocumentoRequestDTO> documentos,
        ZeeTSolicitacaoEntity solicitacao,
        BigDecimal idProcesso,
        BigDecimal idEtapa
    ) {
        if (documentos == null || documentos.isEmpty()) {
            return;
        }

        for (SolicitacaoDocumentoRequestDTO documento : documentos) {
            if (documento.getFicheiro() == null || documento.getFicheiro().isEmpty()) {
                continue;
            }
            if (documento.getIdTpSolicTpDoc() == null) {
                throw new BusinessException("Documento sem configuracao informada.");
            }
            ZeeTTpSolicTpDocEntity tpSolicTpDoc = tpSolicTpDocRepository.findById(documento.getIdTpSolicTpDoc())
                .orElseThrow(() -> new BusinessException("Documento configurado nao encontrado: " + documento.getIdTpSolicTpDoc()));
            if (!Objects.equals(tpSolicTpDoc.getIdTpSolic(), solicitacao.getIdTpSolicitacao())) {
                throw new BusinessException("Documento nao pertence ao tipo de solicitacao informado.");
            }

            ZeeTDocRelacaoEntity docRelacao = new ZeeTDocRelacaoEntity();
            docRelacao.setTipoRelacao(TIPO_RELACAO_SOLICITACAO);
            docRelacao.setIdRelacao(BigDecimal.valueOf(solicitacao.getId()));
            docRelacao.setIdTpDoc(tpSolicTpDoc.getIdTpDoc());
            docRelacao.setDescricao(tpSolicTpDoc.getRequisito());

            String filename = firstText(documento.getTipoDoc(), tpSolicTpDoc.getIdTpDoc() != null ? tpSolicTpDoc.getIdTpDoc().toString() : null, documento.getIdTpSolicTpDoc().toString());
            UploadDTO upload = new UploadDTO(
                documento.getFicheiro(),
                filename,
                DocumentoBus.getBasePathForModuloOrObject(TIPO_RELACAO_SOLICITACAO, solicitacao.getId().toString()),
                docRelacao
            );
            documentoBus.saveOrUpdate(upload, solicitacao.getUserSolic());

            ZeeTSolicitacaoDocEntity solicitacaoDoc = new ZeeTSolicitacaoDocEntity();
            solicitacaoDoc.setIdEtapa(idEtapa);
            solicitacaoDoc.setIdProcesso(idProcesso);
            solicitacaoDoc.setDataRegisto(LocalDate.now());
            solicitacaoDoc.setUserRegisto(solicitacao.getUserSolic());
            solicitacaoDoc.setIdSolicitacao(solicitacao.getId());
            solicitacaoDoc.setIdTpSolicTpDoc(tpSolicTpDoc.getId());
            solicitacaoDoc.setPath(upload.getZeeTDocRelacao().getPath());
            solicitacaoDocRepository.save(solicitacaoDoc);
        }
    }

    private void saveRequisitos(
        List<SolicitacaoRequisitoRequestDTO> requisitos,
        ZeeTSolicitacaoEntity solicitacao,
        BigDecimal idProcesso,
        BigDecimal idEtapa
    ) {
        if (requisitos == null || requisitos.isEmpty()) {
            return;
        }

        for (SolicitacaoRequisitoRequestDTO requisito : requisitos) {
            if (!Objects.equals(requisito.getCumpre(), requisito.getCumpreCheck())) {
                continue;
            }
            if (requisito.getIdTpSolicTpDoc() == null) {
                throw new BusinessException("Requisito sem configuracao informada.");
            }
            ZeeTTpSolicTpDocEntity tpSolicTpDoc = tpSolicTpDocRepository.findById(requisito.getIdTpSolicTpDoc())
                .orElseThrow(() -> new BusinessException("Requisito configurado nao encontrado: " + requisito.getIdTpSolicTpDoc()));
            if (!Objects.equals(tpSolicTpDoc.getIdTpSolic(), solicitacao.getIdTpSolicitacao())) {
                throw new BusinessException("Requisito nao pertence ao tipo de solicitacao informado.");
            }

            ZeeTSolicitacaoDocEntity solicitacaoDoc = new ZeeTSolicitacaoDocEntity();
            solicitacaoDoc.setIdEtapa(idEtapa);
            solicitacaoDoc.setIdProcesso(idProcesso);
            solicitacaoDoc.setDataRegisto(LocalDate.now());
            solicitacaoDoc.setUserRegisto(solicitacao.getUserSolic());
            solicitacaoDoc.setIdSolicitacao(solicitacao.getId());
            solicitacaoDoc.setIdTpSolicTpDoc(tpSolicTpDoc.getId());
            solicitacaoDocRepository.save(solicitacaoDoc);
        }
    }

    private static String firstText(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                return value.trim();
            }
        }
        return null;
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private static String resolveOrigem(SubmeterSolicitacaoRequestDTO dto) {
        return hasText(dto.getOrigem()) ? dto.getOrigem().trim() : ORIGEM_PORTAL;
    }

    private static BigDecimal toBigDecimal(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException("IGRP nao retornou " + label + ".");
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            throw new BusinessException("IGRP retornou " + label + " invalido: " + value, ex);
        }
    }

    private static BigDecimal toBigDecimalOrZero(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException ex) {
            return BigDecimal.ZERO;
        }
    }

    private void notifyRequerente(
        SubmeterSolicitacaoRequestDTO dto,
        ZeeTSolicitacaoEntity solicitacao,
        TPedidoEntity pedido,
        StartProcessResponse processo
    ) {
        if (!hasText(dto.getEmail())) {
            log.info("Nenhum email informado para notificacao da solicitacao {}.", solicitacao.getId());
            return;
        }

        String linkRecibo = buildLinkRecibo(pedido, processo);
        TemplateContent content = resolveSubmissaoTemplate(processo, pedido, solicitacao, linkRecibo);
        boolean sent = sendEmailSafely(dto.getEmail(), content.subject(), content.body(), solicitacao.getId());

        try {
            TNotificacaoEntity notificacao = new TNotificacaoEntity();
            notificacao.setIdAplicacao(BigDecimal.ZERO);
            notificacao.setIdOrganica(pedido.getIdOrganica() != null ? pedido.getIdOrganica() : BigDecimal.ZERO);
            notificacao.setUserRegisto(pedido.getIdUserReg() != null ? pedido.getIdUserReg() : BigDecimal.ZERO);
            notificacao.setDataRegisto(LocalDate.now());
            notificacao.setAssunto(content.subject());
            notificacao.setDataEnvio(LocalDateTime.now());
            notificacao.setMensagem(content.body());
            notificacao.setEmail(dto.getEmail().trim());
            notificacao.setEstado(ESTADO_PENDENTE);
            notificacao.setFlagAutomatico("S");
            notificacao.setFlagSucesso(sent ? "S" : "N");
            notificacao.setFlagLeitura("N");
            notificacao.setNumeroReenvios(BigDecimal.ZERO);
            notificacao.setTipo(TIPO_NOTIFICACAO_EMAIL);
            notificacao.setIdRelacao(solicitacao.getId());
            notificacao.setEmailsEnviados(dto.getEmail().trim());
            notificacao.setConfirmRecebimento(false);

            TNotificacaoEntity saved = notificacaoRepository.save(notificacao);
            saveNotificacaoRelacao(saved.getId(), solicitacao.getId());
        } catch (RuntimeException ex) {
            log.error("Erro ao gravar notificacao da solicitacao {}.", solicitacao.getId(), ex);
        }
    }

    private TemplateContent resolveSubmissaoTemplate(
        StartProcessResponse processo,
        TPedidoEntity pedido,
        ZeeTSolicitacaoEntity solicitacao,
        String linkRecibo
    ) {
        return templateNotifRepository.findFirstByCodigoAndDmEstadoOrderByIdDesc(TEMPLATE_SUBMISSAO_SOLICITACAO, ESTADO_ATIVO)
            .map(template -> new TemplateContent(
                replaceTemplate(template.getAssunto(), processo, pedido, solicitacao, linkRecibo),
                replaceTemplate(template.getTemplateMsg(), processo, pedido, solicitacao, linkRecibo)
            ))
            .orElseGet(() -> new TemplateContent(
                "Solicitacao submetida",
                "A sua solicitacao foi submetida com o numero de processo " + processo.getProcessInstanceId()
                    + (hasText(linkRecibo) ? ". Recibo: " + linkRecibo : ".")
            ));
    }

    private String replaceTemplate(
        String template,
        StartProcessResponse processo,
        TPedidoEntity pedido,
        ZeeTSolicitacaoEntity solicitacao,
        String linkRecibo
    ) {
        String value = hasText(template) ? template : "";
        String processoNome = firstText(processo.getProcessName(), processo.getProcessDefinitionKey(), "");
        String nrProcesso = firstText(processo.getProcessInstanceId(), pedido.getIdProcesso() != null ? pedido.getIdProcesso().toString() : "", "");
        return value
            .replace("${processo}", processoNome)
            .replace("{processo}", processoNome)
            .replace("#processo#", processoNome)
            .replace(":processo", processoNome)
            .replace("${nrProcesso}", nrProcesso)
            .replace("{nrProcesso}", nrProcesso)
            .replace("#nrProcesso#", nrProcesso)
            .replace(":nrProcesso", nrProcesso)
            .replace("${linkRecibo}", firstText(linkRecibo, ""))
            .replace("{linkRecibo}", firstText(linkRecibo, ""))
            .replace("#linkRecibo#", firstText(linkRecibo, ""))
            .replace(":linkRecibo", firstText(linkRecibo, ""))
            .replace("${nrPedido}", String.valueOf(pedido.getId()))
            .replace("{nrPedido}", String.valueOf(pedido.getId()))
            .replace("#nrPedido#", String.valueOf(pedido.getId()))
            .replace(":nrPedido", String.valueOf(pedido.getId()))
            .replace("${idSolicitacao}", String.valueOf(solicitacao.getId()))
            .replace("{idSolicitacao}", String.valueOf(solicitacao.getId()))
            .replace("#idSolicitacao#", String.valueOf(solicitacao.getId()))
            .replace(":idSolicitacao", String.valueOf(solicitacao.getId()));
    }

    private String buildLinkRecibo(TPedidoEntity pedido, StartProcessResponse processo) {
        if (!hasText(reciboUrlTemplate)) {
            return "";
        }
        String link = reciboUrlTemplate
            .replace("${p_id_pedido}", encode(pedido.getId()))
            .replace("{p_id_pedido}", encode(pedido.getId()))
            .replace("${p_id_processo}", encode(pedido.getIdProcesso()))
            .replace("{p_id_processo}", encode(pedido.getIdProcesso()))
            .replace("${processDefinitionKey}", encode(processo.getProcessDefinitionKey()))
            .replace("{processDefinitionKey}", encode(processo.getProcessDefinitionKey()))
            .replace("${processDefinition}", encode(processo.getProcessName()))
            .replace("{processDefinition}", encode(processo.getProcessName()))
            .replace("${taskId}", encode(pedido.getIdEtapa()))
            .replace("{taskId}", encode(pedido.getIdEtapa()))
            .replace("${isPublic}", "1")
            .replace("{isPublic}", "1");
        pedido.setPathRecibo(link);
        return link;
    }

    private boolean sendEmailSafely(String recipient, String subject, String body, Integer solicitacaoId) {
        try {
            emailService.sendText(recipient, subject, body);
            return true;
        } catch (RuntimeException ex) {
            log.error("Erro ao enviar notificacao da solicitacao {} para {}.", solicitacaoId, recipient, ex);
            return false;
        }
    }

    private void saveNotificacaoRelacao(Integer idNotificacao, Integer idSolicitacao) {
        TNotificacaoRelacaoEntity relacao = new TNotificacaoRelacaoEntity();
        relacao.setIdNotificacao(idNotificacao);
        relacao.setTpRelacao(TIPO_RELACAO_SOLICITACAO);
        relacao.setIdRelacao(BigDecimal.valueOf(idSolicitacao));
        notificacaoRelacaoRepository.save(relacao);
    }

    private void notifyTecnicos(
        ZeeTSolicitacaoEntity solicitacao,
        TPedidoEntity pedido,
        ZeeTTpSolicitacaoEntity tpSolicitacao
    ) {
        Set<String> recipients = resolveTecnicoEmails();
        if (recipients.isEmpty()) {
            log.info("Nenhum email tecnico configurado para notificacao da solicitacao {}.", solicitacao.getId());
            return;
        }

        String origem = firstText(solicitacao.getDmOrigem(), "");
        String viaPortal = ORIGEM_PORTAL.equalsIgnoreCase(origem) || "ONLINE".equalsIgnoreCase(origem) ? " Via Portal" : "";
        String nomeTipoSolicitacao = firstText(tpSolicitacao.getNome(), tpSolicitacao.getDescricao(), "solicitacao");
        String subject = "Nova Submissao de Pedido" + viaPortal
            + " - Processo no - " + firstText(pedido.getIdProcesso() != null ? pedido.getIdProcesso().toString() : null, "nao informado")
            + " - " + nomeTipoSolicitacao;
        String requerente = resolveRequerenteTecnico(solicitacao, pedido);
        String pedidoNome = nomeTipoSolicitacao.replaceFirst("(?i)^Pedido de\\s*", "").trim();
        String body = "Caro Tecnico da AZEEMSV, informamos que "
            + requerente
            + " submeteu o pedido de " + firstText(pedidoNome, nomeTipoSolicitacao)
            + " - Processo no " + firstText(pedido.getIdProcesso() != null ? pedido.getIdProcesso().toString() : null, "nao informado")
            + ". Por favor consulte o processo para mais informacoes.";

        for (String recipient : recipients) {
            sendEmailSafely(recipient, subject, body, solicitacao.getId());
        }
    }

    private String resolveRequerenteTecnico(ZeeTSolicitacaoEntity solicitacao, TPedidoEntity pedido) {
        if (solicitacao.getIdInvestidor() != null) {
            return investidorRepository.findById(solicitacao.getIdInvestidor())
                .map(ZeeTInvestidorEntity::getDenominacao)
                .filter(SolicitacaoServiceImpl::hasText)
                .map(denominacao -> "o investidor " + denominacao)
                .orElseGet(() -> firstText(pedido.getRequerente(), "pessoa nao identificada"));
        }
        if (solicitacao.getIdPromotor() != null) {
            return leadPromotorRepository.findById(solicitacao.getIdPromotor())
                .map(ZeeTLeadPromotorEntity::getDenominacao)
                .filter(SolicitacaoServiceImpl::hasText)
                .map(denominacao -> "o promotor " + denominacao)
                .orElseGet(() -> firstText(pedido.getRequerente(), "pessoa nao identificada"));
        }
        return firstText(pedido.getRequerente(), "pessoa nao identificada");
    }

    private Set<String> resolveTecnicoEmails() {
        Set<String> emails = new LinkedHashSet<>();
        paramReportRepository.findAll().stream()
            .map(ZeeTParamReportEntity::getEmail)
            .filter(SolicitacaoServiceImpl::hasText)
            .map(String::trim)
            .forEach(email -> addEmailIgnoreCase(emails, email));
        emailsRepository.findAll().stream()
            .map(ZeeTEmailsEntity::getEmail)
            .filter(SolicitacaoServiceImpl::hasText)
            .map(String::trim)
            .forEach(email -> addEmailIgnoreCase(emails, email));
        return emails;
    }

    private static void addEmailIgnoreCase(Set<String> emails, String email) {
        if (emails.stream().noneMatch(existing -> existing.equalsIgnoreCase(email))) {
            emails.add(email);
        }
    }

    private static String encode(Object value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
    }

    private record TemplateContent(String subject, String body) {
    }

    private SolicitacaoResponseDTO enrich(SolicitacaoResponseDTO dto) {
        dto.setDmEstadoProcDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROC_SOLICIT, dto.getDmEstadoProc()));
        return dto;
    }

    private SolicitacaoDocResponseDTO toDocumentoResponse(SolicitacaoDocProjection p) {
        SolicitacaoDocResponseDTO dto = new SolicitacaoDocResponseDTO();
        dto.setId(p.getId());
        dto.setIdSolicitacao(p.getIdSolicitacao());
        dto.setIdTpSolicTpDoc(p.getIdTpSolicTpDoc());
        dto.setIdTpDoc(p.getIdTpDoc());
        dto.setTpDocNome(p.getTpDocNome());
        dto.setTpDocCodigo(p.getTpDocCodigo());
        dto.setRequisito(p.getRequisito());
        dto.setFlagObrigatorio(p.getFlagObrigatorio());
        dto.setPedResp(p.getPedResp());
        dto.setIdProcesso(p.getIdProcesso());
        dto.setIdEtapa(p.getIdEtapa());
        dto.setDataRegisto(p.getDataRegisto());
        dto.setUserRegisto(p.getUserRegisto());
        dto.setPath(documentViewerUrlService.toViewerUrl(p.getPath()));
        return dto;
    }

    private SolicitacaoDocResponseDTO toDocumentoResponse(SolicitacaoDocumentoConfiguradoProjection p) {
        SolicitacaoDocResponseDTO dto = new SolicitacaoDocResponseDTO();
        dto.setIdTpSolicTpDoc(p.getIdTpSolicTpDoc());
        dto.setIdTpDoc(p.getIdTpDoc());
        dto.setTpDocNome(p.getTpDocNome());
        dto.setTpDocCodigo(p.getTpDocCodigo());
        dto.setRequisito(p.getRequisito());
        dto.setFlagObrigatorio(p.getFlagObrigatorio());
        dto.setPedResp(p.getPedResp());
        return dto;
    }

    private SolicitacaoRequisitoResponseDTO toRequisitoResponse(SolicitacaoRequisitoProjection p) {
        SolicitacaoRequisitoResponseDTO dto = new SolicitacaoRequisitoResponseDTO();
        dto.setIdTpSolicTpDoc(p.getIdTpSolicTpDoc());
        dto.setRequisito(p.getRequisito());
        dto.setFlagObrigatorio(p.getFlagObrigatorio());
        dto.setFlagObrigatorioDesc("SIM".equalsIgnoreCase(p.getFlagObrigatorio()) ? "Sim" : "Nao");
        dto.setCumpre("1");
        dto.setCumpreCheck("0");
        return dto;
    }

    private SolicitacaoTaxaResponseDTO toTaxaResponse(ZeeTTpSolicTaxaEntity entity) {
        SolicitacaoTaxaResponseDTO dto = new SolicitacaoTaxaResponseDTO();
        dto.setTaxa(entity.getDescricao());
        dto.setTipoTaxa(domainHelper.describe(DomainDescriptionHelper.TIPO_TAXA, entity.getTipoTaxa()));
        if (entity.getValor() != null) {
            dto.setValor(entity.getValor().intValue());
        }
        return dto;
    }

    private SolicitacaoResponseDTO toResponse(SolicitacaoInvestidorProjection p) {
        SolicitacaoResponseDTO dto = new SolicitacaoResponseDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setDescricao(p.getDescricao());
        dto.setEstado(p.getEstado());
        dto.setIdTpSolicitacao(p.getIdTpSolicitacao());
        dto.setIdPedido(p.getIdPedido());
        dto.setIdEntidade(p.getIdEntidade());
        dto.setIdOrganica(p.getIdOrganica());
        dto.setIdProcesso(p.getIdProcesso());
        dto.setIdSolicPai(p.getIdSolicPai());
        dto.setIdPromotor(p.getIdPromotor());
        dto.setIdInvestidor(p.getIdInvestidor());
        dto.setIdProjeto(p.getIdProjeto());
        dto.setExposicao(p.getExposicao());
        dto.setDmOrigem(p.getDmOrigem());
        dto.setUserSolic(p.getUserSolic());
        dto.setDataSolic(p.getDataSolic());
        dto.setDataPrevResposta(p.getDataPrevResposta());
        dto.setDescSolic(p.getDescSolic());
        dto.setDmEstadoProc(p.getDmEstadoProc());
        dto.setDmEstadoProcDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROC_SOLICIT, p.getDmEstadoProc()));
        dto.setDataResposta(p.getDataResposta());
        dto.setUserResposta(p.getUserResposta());
        dto.setDescResposta(p.getDescResposta());
        dto.setPrazoDia(p.getPrazoDia());
        dto.setPrazoReal(p.getPrazoReal());
        dto.setEtapaAtual(p.getEtapaAtual());
        dto.setIdPontoFocalResp(p.getIdPontoFocalResp());
        dto.setFlagCorrecao(p.getFlagCorrecao());
        dto.setDataEnvioCorrecao(p.getDataEnvioCorrecao());
        dto.setDataFimPrevistaCorrecao(p.getDataFimPrevistaCorrecao());
        dto.setDataCorrecao(p.getDataCorrecao());
        dto.setUserCorecao(p.getUserCorecao());

        dto.setTpSolicitacaoNome(p.getTpSolicitacaoNome());
        dto.setTpSolicitacaoDescricao(p.getTpSolicitacaoDescricao());
        dto.setTpSolicitacaoCodigo(p.getTpSolicitacaoCodigo());
        dto.setTpSolicitacaoDmTipo(p.getTpSolicitacaoDmTipo());
        dto.setTpSolicitacaoDmTipoDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_SOLICITACAO, p.getTpSolicitacaoDmTipo()));
        dto.setTpSolicitacaoDmEstado(p.getTpSolicitacaoDmEstado());
        dto.setTpSolicitacaoDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, p.getTpSolicitacaoDmEstado()));

        dto.setInvestidorDenominacao(p.getInvestidorDenominacao());
        dto.setInvestidorNif(p.getInvestidorNif());
        dto.setInvestidorEmail(p.getInvestidorEmail());
        dto.setInvestidorTelemovel(p.getInvestidorTelemovel());
        dto.setInvestidorPaisOrigem(p.getInvestidorPaisOrigem());

        dto.setPromotorDenominacao(p.getPromotorDenominacao());
        dto.setPromotorNif(p.getPromotorNif());
        dto.setPromotorEmail(p.getPromotorEmail());
        dto.setPromotorTelemovel(p.getPromotorTelemovel());
        dto.setPromotorPaisOrigem(p.getPromotorPaisOrigem());

        dto.setProjetoDenominacao(p.getProjetoDenominacao());
        dto.setProjetoDmRegime(p.getProjetoDmRegime());
        dto.setProjetoDmRegimeDesc(domainHelper.describe(DomainDescriptionHelper.REGIME, p.getProjetoDmRegime()));
        dto.setProjetoDmProdutoServico(p.getProjetoDmProdutoServico());
        dto.setProjetoDmProdutoServicoDesc(domainHelper.describe(DomainDescriptionHelper.PRODUTO_SERVICO, p.getProjetoDmProdutoServico()));
        dto.setProjetoDmEstadoProc(p.getProjetoDmEstadoProc());
        dto.setProjetoDmEstadoProcDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROCESO, p.getProjetoDmEstadoProc()));
        dto.setProjetoDmSituacao(p.getProjetoDmSituacao());
        dto.setProjetoDmSituacaoDesc(domainHelper.describe(DomainDescriptionHelper.SITUACAO_PROJ, p.getProjetoDmSituacao()));

        dto.setPedidoDmEstadoPedido(p.getPedidoDmEstadoPedido());
        dto.setPedidoDmEstadoPedidoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROC_SOLICIT, p.getPedidoDmEstadoPedido()));
        dto.setPedidoEtapaAtual(p.getPedidoEtapaAtual());
        dto.setPedidoCodEtapaAtual(p.getPedidoCodEtapaAtual());
        dto.setPedidoDtRegisto(p.getPedidoDtRegisto());
        dto.setPedidoDtDespacho(p.getPedidoDtDespacho());
        dto.setPedidoDtFim(p.getPedidoDtFim());
        dto.setPedidoObsDespacho(p.getPedidoObsDespacho());
        dto.setPedidoResultado(p.getPedidoResultado());
        dto.setPedidoRequerente(p.getPedidoRequerente());
        return dto;
    }
}
