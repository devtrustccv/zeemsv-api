package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.generic.service.EmailService;
import cv.zeemsv.api.application.audit.dto.AuditContext;
import cv.zeemsv.api.application.audit.entity.ChangeLogsItem;
import cv.zeemsv.api.application.audit.service.ChangeLogsService;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDetailResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocumentosRequisitosResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoDocumentoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.CorrigirSolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequisitoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequisitoResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoTaxaResponseDTO;
import cv.zeemsv.api.application.solicitacao.dto.SubmeterSolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.ReciboPedidoDadosResponseDTO;
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
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTEmailsEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTLeadPromotorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTParamReportEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoDocEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoLoteEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTpDocEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.TPedidoRepository;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTConfigTemplateNotifRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTEmailsRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTLeadPromotorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTLoteRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTParamReportRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTProjInvestRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoDocRepository;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTaxaEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoLoteRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpDocRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicTpDocRepository;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoDocProjection;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoDocumentoConfiguradoProjection;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoInvestidorProjection;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoRequisitoProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private static final String RECIBO_PEDIDO_DESCRICAO = "Recibo Pedido";
    private static final String RECIBO_PEDIDO_MIMETYPE = "application/pdf";
    private static final String ETAPA_ANALISE_SOLICITACAO = "Análise Solicitação";
    private static final String MOMENTO_PAGAMENTO_NO_INICIO = "NO_INICIO";
    private static final String COD_ETAPA_ANALISE_SOLICITACAO = "analise_solicitacao";
    private static final int RECIBO_PATH_LOOKUP_ATTEMPTS = 5;
    private static final long RECIBO_PATH_LOOKUP_DELAY_MS = 500L;
    private static final BigDecimal ID_ORGANICA_DEFAULT = BigDecimal.valueOf(4);

    private final SolicitacaoBus bus;
    private final SolicitacaoDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTSolicitacaoRepository solicitacaoRepository;
    private final ZeeTSolicitacaoDocRepository solicitacaoDocRepository;
    private final ZeeTTpSolicitacaoRepository tpSolicitacaoRepository;
    private final ZeeTTpSolicTaxaRepository tpSolicTaxaRepository;
    private final ZeeTTpDocRepository tpDocRepository;
    private final ZeeTTpSolicTpDocRepository tpSolicTpDocRepository;
    private final TPedidoRepository pedidoRepository;
    private final ZeeTConfigTemplateNotifRepository templateNotifRepository;
    private final TNotificacaoRepository notificacaoRepository;
    private final TNotificacaoRelacaoRepository notificacaoRelacaoRepository;
    private final ZeeTParamReportRepository paramReportRepository;
    private final ZeeTEmailsRepository emailsRepository;
    private final ZeeTProjInvestRepository projetoRepository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final ZeeTLeadPromotorRepository leadPromotorRepository;
    private final ZeeTLoteRepository loteRepository;
    private final ZeeTSolicitacaoLoteRepository solicitacaoLoteRepository;
    private final ZeeTCobrancaRepository cobrancaRepository;
    private final ZeeTCobrancaTaxaRepository cobrancaTaxaRepository;
    private final ZeeTSolicitacaoCobrancaRepository solicitacaoCobrancaRepository;
    private final ZeeTSolicitacaoTaxaRepository solicitacaoTaxaRepository;
    private final ZeeTPagamentoRepository pagamentoRepository;
    private final DocumentViewerUrlService documentViewerUrlService;
    private final DocumentoBus documentoBus;
    private final ProcessStartService processStartService;
    private final EmailService emailService;
    private final PlatformTransactionManager transactionManager;
    private final ChangeLogsService changeLogsService;

    @Value("${application.reports.recibo-pdf-url-template:${application.reports.recibo-url-template:}}")
    private String reciboPdfUrlTemplate;

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
        validateRelatedEntities(dto);

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

        gerarCobrancaNoInicio(tpSolicitacao, solicitacao);
        saveLotes(dto, solicitacao);
        saveDocumentos(dto.getDocumentos(), solicitacao, idProcesso, idEtapaDoc);
        saveRequisitos(dto.getRequisitos(), solicitacao, idProcesso, idEtapaDoc);
        notifyAfterCommit(dto, solicitacao, pedido, processo, tpSolicitacao);

        return enrich(mapper.toResponse(bus.findById(solicitacao.getId())));
    }

    @Override
    @Transactional
    public SolicitacaoResponseDTO corrigir(Integer id, CorrigirSolicitacaoRequestDTO dto, String authorization) {
        ZeeTSolicitacaoEntity solicitacao = solicitacaoRepository.findById(id)
            .orElseThrow(() -> new BusinessException("Solicitacao nao encontrada: " + id));
        ZeeTSolicitacaoEntity before = copySolicitacaoForAudit(solicitacao);
        List<Integer> beforeLotes = solicitacaoLoteRepository.findByIdSolicitacao(solicitacao.getId()).stream()
            .map(ZeeTSolicitacaoLoteEntity::getIdLote)
            .toList();

        corrigirDadosPedido(dto, solicitacao);
        corrigirDocumentos(dto.getDocumentos(), solicitacao);
        corrigirRequisitos(dto.getRequisitos(), solicitacao);
        atualizarEstadoCorrecaoEAvancarProcesso(dto, solicitacao, authorization);
        auditSolicitacaoCorrection(before, solicitacao, beforeLotes, dto);

        return enrich(mapper.toResponse(bus.findById(solicitacao.getId())));
    }

    private void notifyAfterCommit(
        SubmeterSolicitacaoRequestDTO dto,
        ZeeTSolicitacaoEntity solicitacao,
        TPedidoEntity pedido,
        StartProcessResponse processo,
        ZeeTTpSolicitacaoEntity tpSolicitacao
    ) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            ensureReciboPedidoPath(solicitacao, pedido);
            notifyRequerente(dto, solicitacao, pedido, processo, tpSolicitacao);
            notifyTecnicos(solicitacao, pedido, tpSolicitacao);
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                runAfterCommitInNewTransaction(() -> {
                    ensureReciboPedidoPath(solicitacao, pedido);
                    notifyRequerente(dto, solicitacao, pedido, processo, tpSolicitacao);
                });
                runAfterCommitInNewTransaction(() -> notifyTecnicos(solicitacao, pedido, tpSolicitacao));
            }
        });
    }

    private void runAfterCommitInNewTransaction(Runnable action) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.executeWithoutResult(status -> action.run());
    }

    @Override @Transactional
    public SolicitacaoResponseDTO update(Integer id, SolicitacaoRequestDTO dto) { return enrich(mapper.toResponse(bus.update(id, mapper.toModel(dto)))); }

    @Override @Transactional(readOnly = true)
    public SolicitacaoResponseDTO findById(Integer id) { return enrich(mapper.toResponse(bus.findById(id))); }

    @Override
    @Transactional(readOnly = true)
    public SolicitacaoDetailResponseDTO findDetailById(Integer id) {
        SolicitacaoResponseDTO solicitacao = solicitacaoRepository.findDetalheById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new BusinessException("Solicitacao nao encontrada: " + id));

        SolicitacaoDetailResponseDTO detail = new SolicitacaoDetailResponseDTO();
        detail.setSolicitacao(solicitacao);
        detail.setPedido(resolvePedidoDados(solicitacao));
        detail.setDocumentos(findDocumentosDetalhe(solicitacao));
        detail.setRequisitos(findRequisitosDetalhe(solicitacao));
        detail.setTaxas(findTaxasDetalhe(solicitacao));
        return detail;
    }

    @Override @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).map(this::enrich).toList(); }

    @Override @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> findByInvestidorId(Integer idInvestidor) {
        return solicitacaoRepository.findDetalheByInvestidorId(idInvestidor).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReciboPedidoDadosResponseDTO findReciboDados(Integer idSolicitacao) {
        ZeeTSolicitacaoEntity solicitacao = solicitacaoRepository.findById(idSolicitacao)
            .orElseThrow(() -> new BusinessException("Solicitacao nao encontrada: " + idSolicitacao));
        TPedidoEntity pedido = pedidoRepository.findById(solicitacao.getIdPedido())
            .orElseThrow(() -> new BusinessException("Pedido nao encontrado para a solicitacao: " + idSolicitacao));
        return buildReciboDados(solicitacao, pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public ReciboPedidoDadosResponseDTO findReciboDadosByProcesso(BigDecimal nrProcesso) {
        TPedidoEntity pedido = pedidoRepository.findFirstByIdProcessoAndTipoRelacaoIgnoreCaseOrderByIdDesc(nrProcesso, TIPO_RELACAO_SOLICITACAO)
            .orElseThrow(() -> new BusinessException("Pedido nao encontrado para o processo: " + nrProcesso));
        ZeeTSolicitacaoEntity solicitacao = solicitacaoRepository.findFirstByIdPedido(pedido.getId())
            .orElseThrow(() -> new BusinessException("Solicitacao nao encontrada para o pedido: " + pedido.getId()));
        return buildReciboDados(solicitacao, pedido);
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
        List<ZeeTTpSolicTaxaEntity> taxas = tpSolicTaxaRepository.findByIdTpSolic(idTpSolicitacao);
        List<ZeeTTpSolicTaxaEntity> taxasNoInicio = taxas.stream()
            .filter(this::isTaxaPagamentoNoInicio)
            .toList();
        response.setInstantPagamento(!taxasNoInicio.isEmpty());
        response.setTotalAPagar(taxasNoInicio.stream()
            .map(ZeeTTpSolicTaxaEntity::getValor)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        response.setTaxas(taxas.stream()
            .map(this::toTaxaResponse)
            .toList());
        return response;
    }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private ReciboPedidoDadosResponseDTO resolvePedidoDados(SolicitacaoResponseDTO solicitacao) {
        try {
            return findReciboDados(solicitacao.getId());
        } catch (BusinessException ex) {
            log.warn("Nao foi possivel obter dados do pedido da solicitacao {}.", solicitacao.getId(), ex);
            return null;
        }
    }

    private List<SolicitacaoDocResponseDTO> findDocumentosDetalhe(SolicitacaoResponseDTO solicitacao) {
        return solicitacaoDocRepository.findDetalheByIdSolicitacao(solicitacao.getId(), solicitacao.getIdTpSolicitacao())
            .stream()
            .map(this::toDocumentoResponse)
            .toList();
    }

    private List<SolicitacaoRequisitoResponseDTO> findRequisitosDetalhe(SolicitacaoResponseDTO solicitacao) {
        Set<Integer> requisitosCumpridos = new LinkedHashSet<>(solicitacaoDocRepository.findIdTpSolicTpDocByIdSolicitacao(solicitacao.getId()));
        return tpSolicTpDocRepository.findRequisitosByIdTpSolicitacao(solicitacao.getIdTpSolicitacao())
            .stream()
            .map(requisito -> toRequisitoDetalhe(requisito, requisitosCumpridos))
            .toList();
    }

    private List<SolicitacaoTaxaResponseDTO> findTaxasDetalhe(SolicitacaoResponseDTO solicitacao) {
        List<ZeeTSolicitacaoTaxaEntity> solicitacaoTaxas = solicitacaoTaxaRepository.findByIdSolicitacao(solicitacao.getId());
        Map<Integer, ZeeTTpSolicTaxaEntity> taxasConfiguradas = tpSolicTaxaRepository.findByIdTpSolic(solicitacao.getIdTpSolicitacao())
            .stream()
            .collect(Collectors.toMap(ZeeTTpSolicTaxaEntity::getId, Function.identity(), (first, ignored) -> first));
        Map<Integer, ZeeTPagamentoEntity> pagamentos = pagamentoRepository.findByIdSolicitacao(solicitacao.getId())
            .stream()
            .filter(pagamento -> pagamento.getIdTpSolicTaxa() != null)
            .collect(Collectors.toMap(ZeeTPagamentoEntity::getIdTpSolicTaxa, Function.identity(), (first, ignored) -> first));

        if (!solicitacaoTaxas.isEmpty()) {
            return solicitacaoTaxas.stream()
                .map(taxa -> toTaxaDetalhe(taxa, taxasConfiguradas.get(taxa.getIdTpSolicTaxa()), pagamentos.get(taxa.getIdTpSolicTaxa())))
                .toList();
        }

        return taxasConfiguradas.values().stream()
            .map(taxa -> toTaxaDetalhe(taxa, pagamentos.get(taxa.getId())))
            .toList();
    }

    private SolicitacaoRequisitoResponseDTO toRequisitoDetalhe(
        SolicitacaoRequisitoProjection projection,
        Set<Integer> requisitosCumpridos
    ) {
        SolicitacaoRequisitoResponseDTO dto = toRequisitoResponse(projection);
        dto.setCumpre(requisitosCumpridos.contains(projection.getIdTpSolicTpDoc()) ? "SIM" : "NAO");
        return dto;
    }

    private SolicitacaoTaxaResponseDTO toTaxaDetalhe(
        ZeeTSolicitacaoTaxaEntity solicitacaoTaxa,
        ZeeTTpSolicTaxaEntity taxaConfigurada,
        ZeeTPagamentoEntity pagamento
    ) {
        SolicitacaoTaxaResponseDTO dto = toTaxaDetalhe(taxaConfigurada, pagamento);
        dto.setId(solicitacaoTaxa.getId());
        dto.setIdSolicitacao(solicitacaoTaxa.getIdSolicitacao());
        dto.setIdTpSolicTaxa(solicitacaoTaxa.getIdTpSolicTaxa());
        dto.setValor(solicitacaoTaxa.getValor());
        return dto;
    }

    private SolicitacaoTaxaResponseDTO toTaxaDetalhe(ZeeTTpSolicTaxaEntity taxaConfigurada, ZeeTPagamentoEntity pagamento) {
        SolicitacaoTaxaResponseDTO dto = new SolicitacaoTaxaResponseDTO();
        if (taxaConfigurada != null) {
            dto.setIdTpSolicTaxa(taxaConfigurada.getId());
            dto.setIdTaxa(taxaConfigurada.getIdTaxa());
            dto.setTaxa(taxaConfigurada.getDescricao());
            dto.setTipoTaxaCodigo(taxaConfigurada.getTipoTaxa());
            dto.setTipoTaxa(domainHelper.describe(DomainDescriptionHelper.TIPO_TAXA, taxaConfigurada.getTipoTaxa()));
            dto.setValor(taxaConfigurada.getValor());
            dto.setValorConfigurado(taxaConfigurada.getValor());
        }
        enrichPagamento(dto, pagamento);
        return dto;
    }

    private void enrichPagamento(SolicitacaoTaxaResponseDTO dto, ZeeTPagamentoEntity pagamento) {
        if (pagamento == null) {
            return;
        }
        dto.setIdPagamento(pagamento.getId());
        dto.setReferenciaPagamento(pagamento.getReferencia());
        dto.setDuc(pagamento.getDuc());
        dto.setDmEstadoPagamento(pagamento.getDmEstadoPag());
        dto.setDmEstadoPagamentoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, pagamento.getDmEstadoPag()));
        dto.setFormaPagamento(pagamento.getFormaPagamento());
        dto.setLinkDuc(documentViewerUrlService.toViewerUrl(pagamento.getLinkDuc()));
        if (dto.getValor() == null) {
            dto.setValor(pagamento.getValor());
        }
    }

    private ReciboPedidoDadosResponseDTO buildReciboDados(ZeeTSolicitacaoEntity solicitacao, TPedidoEntity pedido) {
        ZeeTTpSolicitacaoEntity tpSolicitacao = tpSolicitacaoRepository.findById(solicitacao.getIdTpSolicitacao())
            .orElseThrow(() -> new BusinessException("Tipo de solicitacao nao encontrado: " + solicitacao.getIdTpSolicitacao()));
        ZeeTParamReportEntity paramReport = paramReportRepository.findAll().stream().findFirst().orElse(null);
        RequerenteDados requerente = resolveRequerenteDados(solicitacao, pedido);

        ReciboPedidoDadosResponseDTO response = new ReciboPedidoDadosResponseDTO();
        response.setIdPedido(pedido.getId());
        response.setIdSolicitacao(solicitacao.getId());
        response.setNrProcesso(pedido.getIdProcesso());
        response.setNrDoc(pedido.getIdProcesso());
        response.setTipoProcesso(pedido.getTpProcesso());
        response.setTipoSolicitacao(firstText(tpSolicitacao.getNome(), tpSolicitacao.getCodigo()));
        response.setTipoSolicitacaoDescricao(firstText(tpSolicitacao.getDescricao(), solicitacao.getDescSolic()));
        response.setDataEntrada(firstTextDate(pedido.getDtRegisto(), solicitacao.getDataSolic()));
        response.setEntidade(requerente.nome());
        response.setRequerente(requerente.nome());
        response.setNif(requerente.nif());
        response.setEmail(firstText(requerente.email(), pedido.getEmail()));
        response.setEndereco(requerente.endereco());
        response.setLinkRecibo(documentViewerUrlService.toViewerUrl(resolveReciboPedidoPath(solicitacao, pedido), RECIBO_PEDIDO_MIMETYPE));
        response.setInstituicao(toInstituicao(paramReport));
        response.setDocumentos(solicitacaoDocRepository.findDetalheByIdSolicitacao(solicitacao.getId(), solicitacao.getIdTpSolicitacao()).stream()
            .map(this::toReciboDocumento)
            .toList());
        Set<Integer> requisitosCumpridos = new LinkedHashSet<>(solicitacaoDocRepository.findIdTpSolicTpDocByIdSolicitacao(solicitacao.getId()));
        response.setRequisitos(tpSolicTpDocRepository.findRequisitosByIdTpSolicitacao(solicitacao.getIdTpSolicitacao()).stream()
            .map(requisito -> toReciboRequisito(requisito, requisitosCumpridos))
            .toList());
        return response;
    }

    private ReciboPedidoDadosResponseDTO.InstituicaoDTO toInstituicao(ZeeTParamReportEntity paramReport) {
        ReciboPedidoDadosResponseDTO.InstituicaoDTO dto = new ReciboPedidoDadosResponseDTO.InstituicaoDTO();
        dto.setNome("ZEEMSV - ZONA ECONÓMICA ESPECIAL MARÍTIMA EM SÃO VICENTE");
        if (paramReport == null) {
            return dto;
        }
        dto.setNif(paramReport.getNif());
        dto.setEmail(paramReport.getEmail());
        dto.setEndereco(paramReport.getRua());
        dto.setTelefone(paramReport.getTelefone());
        dto.setTelemovel(paramReport.getTelemovel());
        dto.setCodigoPostal(paramReport.getCodigoPostal());
        dto.setLinkPortal(paramReport.getLinkPortal());
        dto.setIdLogo(toLogoViewerUrl(paramReport.getIdLogo()));
        return dto;
    }

    private String toLogoViewerUrl(String path) {
        if (!hasText(path)) {
            return null;
        }
        String mimetype = resolveImageMimeType(path);
        return documentViewerUrlService.toViewerUrl(path, mimetype);
    }

    private static String resolveImageMimeType(String path) {
        String filename = DocumentoBus.getFileNameWithExtensionByPath(path);
        if (!hasText(filename)) {
            return "image/png";
        }
        String lower = filename.toLowerCase();
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        if (lower.endsWith(".gif")) {
            return "image/gif";
        }
        if (lower.endsWith(".webp")) {
            return "image/webp";
        }
        if (lower.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "image/png";
    }

    private ReciboPedidoDadosResponseDTO.DocumentoDTO toReciboDocumento(SolicitacaoDocProjection projection) {
        boolean entregue = projection.getId() != null;
        ReciboPedidoDadosResponseDTO.DocumentoDTO dto = new ReciboPedidoDadosResponseDTO.DocumentoDTO();
        dto.setDocumento(firstText(projection.getTpDocNome(), projection.getRequisito(), projection.getTpDocCodigo()));
        dto.setCodigo(projection.getTpDocCodigo());
        dto.setObrigatorio("SIM".equalsIgnoreCase(projection.getFlagObrigatorio()) ? "Sim" : "Nao");
        dto.setEntregue(entregue);
        dto.setSim(entregue);
        dto.setNao(!entregue);
        return dto;
    }

    private ReciboPedidoDadosResponseDTO.RequisitoDTO toReciboRequisito(
        SolicitacaoRequisitoProjection projection,
        Set<Integer> requisitosCumpridos
    ) {
        boolean cumprido = requisitosCumpridos.contains(projection.getIdTpSolicTpDoc());
        ReciboPedidoDadosResponseDTO.RequisitoDTO dto = new ReciboPedidoDadosResponseDTO.RequisitoDTO();
        dto.setIdTpSolicTpDoc(projection.getIdTpSolicTpDoc());
        dto.setRequisito(projection.getRequisito());
        dto.setObrigatorio("SIM".equalsIgnoreCase(projection.getFlagObrigatorio()) ? "Sim" : "Nao");
        dto.setCumprido(cumprido);
        dto.setSim(cumprido);
        dto.setNao(!cumprido);
        return dto;
    }

    private RequerenteDados resolveRequerenteDados(ZeeTSolicitacaoEntity solicitacao, TPedidoEntity pedido) {
        if (solicitacao.getIdInvestidor() != null) {
            return investidorRepository.findById(solicitacao.getIdInvestidor())
                .map(investidor -> new RequerenteDados(
                    firstText(investidor.getDenominacao(), pedido.getRequerente()),
                    investidor.getNif(),
                    firstText(investidor.getEmail(), pedido.getEmail()),
                    firstText(investidor.getEndereco(), investidor.getSede())
                ))
                .orElseGet(() -> new RequerenteDados(pedido.getRequerente(), null, pedido.getEmail(), null));
        }
        if (solicitacao.getIdPromotor() != null) {
            return leadPromotorRepository.findById(solicitacao.getIdPromotor())
                .map(promotor -> new RequerenteDados(
                    firstText(promotor.getDenominacao(), pedido.getRequerente()),
                    promotor.getNif(),
                    firstText(promotor.getEmail(), pedido.getEmail()),
                    firstText(promotor.getEndereco(), promotor.getSede())
                ))
                .orElseGet(() -> new RequerenteDados(pedido.getRequerente(), null, pedido.getEmail(), null));
        }
        return new RequerenteDados(pedido.getRequerente(), null, pedido.getEmail(), null);
    }

    private static LocalDate firstTextDate(LocalDate... values) {
        if (values == null) {
            return null;
        }
        for (LocalDate value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

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
                if (!isSim(requisito.getCumpre())) {
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

    private void validateRelatedEntities(SubmeterSolicitacaoRequestDTO dto) {
        if (dto.getIdInvestidor() != null && !investidorRepository.existsById(dto.getIdInvestidor())) {
            throw new BusinessException("Investidor nao encontrado: " + dto.getIdInvestidor());
        }
        if (dto.getIdPromotor() != null && !leadPromotorRepository.existsById(dto.getIdPromotor())) {
            throw new BusinessException("Promotor nao encontrado: " + dto.getIdPromotor());
        }
        if (dto.getIdProjeto() != null && !projetoRepository.existsById(dto.getIdProjeto())) {
            throw new BusinessException("Projeto nao encontrado: " + dto.getIdProjeto());
        }
        for (Integer idLote : parseIdsLote(dto.getIdsLote())) {
            if (!loteRepository.existsById(idLote)) {
                throw new BusinessException("Lote nao encontrado: " + idLote);
            }
        }
    }

    private void saveLotes(SubmeterSolicitacaoRequestDTO dto, ZeeTSolicitacaoEntity solicitacao) {
        List<ZeeTSolicitacaoLoteEntity> lotes = parseIdsLote(dto.getIdsLote()).stream()
            .map(idLote -> {
                ZeeTSolicitacaoLoteEntity entity = new ZeeTSolicitacaoLoteEntity();
                entity.setIdSolicitacao(solicitacao.getId());
                entity.setIdLote(idLote);
                return entity;
            })
            .toList();

        if (!lotes.isEmpty()) {
            solicitacaoLoteRepository.saveAll(lotes);
        }
    }

    private List<Integer> parseIdsLote(String idsLote) {
        if (idsLote == null || idsLote.trim().isEmpty()) {
            return List.of();
        }

        Set<Integer> ids = new LinkedHashSet<>();
        for (String rawId : idsLote.split(";")) {
            String value = rawId.trim();
            if (value.isEmpty()) {
                continue;
            }
            try {
                ids.add(Integer.valueOf(value));
            } catch (NumberFormatException ex) {
                throw new BusinessException("ids_lote contem um id invalido: " + value);
            }
        }
        return List.copyOf(ids);
    }

    private void gerarCobrancaNoInicio(ZeeTTpSolicitacaoEntity tpSolicitacao, ZeeTSolicitacaoEntity solicitacao) {
        List<ZeeTTpSolicTaxaEntity> taxasNoInicio = tpSolicTaxaRepository.findByIdTpSolic(tpSolicitacao.getId()).stream()
            .filter(this::isTaxaPagamentoNoInicio)
            .toList();
        if (taxasNoInicio.isEmpty()) {
            return;
        }

        List<ZeeTSolicitacaoTaxaEntity> solicitacaoTaxas = taxasNoInicio.stream()
            .map(taxa -> buildSolicitacaoTaxa(taxa, solicitacao))
            .map(solicitacaoTaxaRepository::save)
            .toList();

        BigDecimal total = taxasNoInicio.stream()
            .map(ZeeTTpSolicTaxaEntity::getValor)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        ZeeTCobrancaEntity cobranca = new ZeeTCobrancaEntity();
        cobranca.setIdSolicitacao(solicitacao.getId());
        cobranca.setIdSolicTaxa(solicitacaoTaxas.get(0).getId());
        cobranca.setIdInvestidor(solicitacao.getIdInvestidor());
        cobranca.setIdProjeto(solicitacao.getIdProjeto());
        cobranca.setNrProcesso(solicitacao.getIdProcesso() != null ? solicitacao.getIdProcesso().intValue() : null);
        cobranca.setDataEmissao(LocalDate.now());
        cobranca.setDataVencimento(LocalDate.now());
        cobranca.setValorTotal(total.toPlainString());
        cobranca.setValorPago(BigDecimal.ZERO.toPlainString());
        cobranca.setValorDivida(total.toPlainString());
        cobranca.setDmEstado(ESTADO_PENDENTE);
        cobranca.setUserRegisto(solicitacao.getUserSolic());
        cobranca.setDataRegisto(LocalDate.now());
        cobranca = cobrancaRepository.save(cobranca);

        ZeeTSolicitacaoCobrancaEntity solicitacaoCobranca = new ZeeTSolicitacaoCobrancaEntity();
        solicitacaoCobranca.setIdSolicitacao(solicitacao.getId());
        solicitacaoCobranca.setIdCobranca(cobranca.getId());
        solicitacaoCobrancaRepository.save(solicitacaoCobranca);

        for (ZeeTTpSolicTaxaEntity taxa : taxasNoInicio) {
            ZeeTCobrancaTaxaEntity cobrancaTaxa = new ZeeTCobrancaTaxaEntity();
            cobrancaTaxa.setIdCobranca(cobranca.getId());
            cobrancaTaxa.setIdTaxa(taxa.getIdTaxa());
            cobrancaTaxa.setValor(taxa.getValor());
            cobrancaTaxa.setDmEstado(ESTADO_PENDENTE);
            cobrancaTaxa.setUserRegisto(solicitacao.getUserSolic());
            cobrancaTaxa.setDataRegisto(LocalDate.now());
            cobrancaTaxaRepository.save(cobrancaTaxa);
        }
    }

    private ZeeTSolicitacaoTaxaEntity buildSolicitacaoTaxa(ZeeTTpSolicTaxaEntity taxa, ZeeTSolicitacaoEntity solicitacao) {
        ZeeTSolicitacaoTaxaEntity solicitacaoTaxa = new ZeeTSolicitacaoTaxaEntity();
        solicitacaoTaxa.setIdSolicitacao(solicitacao.getId());
        solicitacaoTaxa.setIdTpSolicTaxa(taxa.getId());
        solicitacaoTaxa.setValor(taxa.getValor() != null ? taxa.getValor() : BigDecimal.ZERO);
        solicitacaoTaxa.setIdInvestidor(solicitacao.getIdInvestidor());
        solicitacaoTaxa.setIdPromotor(solicitacao.getIdPromotor());
        solicitacaoTaxa.setIdProjeto(solicitacao.getIdProjeto());
        return solicitacaoTaxa;
    }

    private boolean isTaxaPagamentoNoInicio(ZeeTTpSolicTaxaEntity taxa) {
        return MOMENTO_PAGAMENTO_NO_INICIO.equalsIgnoreCase(taxa.getDmMomentoPag());
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
        pedido.setIdOrganica(ID_ORGANICA_DEFAULT);
        pedido.setTipoRelacao(TIPO_RELACAO_SOLICITACAO);
        pedido.setRequerente(dto.getNomeRequerente());
        pedido.setTpProcesso(resolveTpProcesso(processo));
        pedido.setEtapaAtual(ETAPA_ANALISE_SOLICITACAO);
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
        solicitacao.setIdOrganica(ID_ORGANICA_DEFAULT);
        solicitacao.setIdProcesso(idProcesso);
        solicitacao.setIdPromotor(dto.getIdPromotor());
        solicitacao.setIdInvestidor(dto.getIdInvestidor());
        solicitacao.setIdProjeto(dto.getIdProjeto());
        solicitacao.setExposicao(dto.getExposicao());
        solicitacao.setDmOrigem(resolveOrigem(dto));
        solicitacao.setDataSolic(LocalDate.now());
        solicitacao.setUserSolic(firstText(dto.getUserName(), dto.getEmail(), dto.getIdUser() != null ? dto.getIdUser().toString() : null, "system"));
        solicitacao.setDmEstadoProc(ESTADO_PENDENTE);
        solicitacao.setEtapaAtual(ETAPA_ANALISE_SOLICITACAO);
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

            String filename = resolveDocumentoFilename(tpSolicTpDoc, documento);
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
            if (!isSim(requisito.getCumpre())) {
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

    private void corrigirDocumentos(
        List<SolicitacaoDocumentoRequestDTO> documentos,
        ZeeTSolicitacaoEntity solicitacao
    ) {
        if (documentos == null || documentos.isEmpty()) {
            return;
        }

        for (SolicitacaoDocumentoRequestDTO documento : documentos) {
            if (documento.getFicheiro() == null || documento.getFicheiro().isEmpty()) {
                continue;
            }

            ZeeTTpSolicTpDocEntity tpSolicTpDoc = loadDocumentoConfig(documento.getIdTpSolicTpDoc(), solicitacao);
            ZeeTDocRelacaoEntity docRelacao = new ZeeTDocRelacaoEntity();
            docRelacao.setTipoRelacao(TIPO_RELACAO_SOLICITACAO);
            docRelacao.setIdRelacao(BigDecimal.valueOf(solicitacao.getId()));
            docRelacao.setIdTpDoc(tpSolicTpDoc.getIdTpDoc());
            docRelacao.setDescricao(tpSolicTpDoc.getRequisito());

            String filename = resolveDocumentoFilename(tpSolicTpDoc, documento);
            UploadDTO upload = new UploadDTO(
                documento.getFicheiro(),
                filename,
                DocumentoBus.getBasePathForModuloOrObject(TIPO_RELACAO_SOLICITACAO, solicitacao.getId().toString()),
                docRelacao
            );
            documentoBus.saveOrUpdate(upload, solicitacao.getUserSolic());
            upsertSolicitacaoDocPath(solicitacao, tpSolicTpDoc.getId(), upload.getZeeTDocRelacao().getPath());
        }
    }

    private void corrigirDadosPedido(CorrigirSolicitacaoRequestDTO dto, ZeeTSolicitacaoEntity solicitacao) {
        boolean changed = false;
        if (dto.getExposicao() != null) {
            solicitacao.setExposicao(firstText(dto.getExposicao()));
            changed = true;
        }
        if (dto.getIdProjeto() != null) {
            if (!projetoRepository.existsById(dto.getIdProjeto())) {
                throw new BusinessException("Projeto nao encontrado: " + dto.getIdProjeto());
            }
            solicitacao.setIdProjeto(dto.getIdProjeto());
            changed = true;
        }
        if (dto.getIdsLote() != null) {
            replaceLotes(dto.getIdsLote(), solicitacao);
        }
        if (changed) {
            solicitacaoRepository.save(solicitacao);
        }
    }

    private void atualizarEstadoCorrecaoEAvancarProcesso(
        CorrigirSolicitacaoRequestDTO dto,
        ZeeTSolicitacaoEntity solicitacao,
        String authorization
    ) {
        solicitacao.setEtapaAtual(ETAPA_ANALISE_SOLICITACAO);
        solicitacao.setDataCorrecao(LocalDate.now());
        solicitacao.setUserCorecao(firstText(dto.getUserName(), solicitacao.getUserSolic(), "system"));
        solicitacaoRepository.save(solicitacao);

        if (solicitacao.getIdPedido() == null) {
            return;
        }

        TPedidoEntity pedido = pedidoRepository.findById(solicitacao.getIdPedido())
            .orElseThrow(() -> new BusinessException("Pedido nao encontrado: " + solicitacao.getIdPedido()));
        pedido.setEtapaAtual(ETAPA_ANALISE_SOLICITACAO);
        pedido.setCodEtapaAtual(COD_ETAPA_ANALISE_SOLICITACAO);
        pedidoRepository.save(pedido);

        String taskNumber = firstText(pedido.getIdEtapa(), pedido.getIdEtapaAtual());
        if (taskNumber == null) {
            throw new BusinessException("Pedido sem etapa/task para avancar processo: " + pedido.getId());
        }
        processStartService.advanceTaskCorrecao(taskNumber, authorization);
    }

    private void auditSolicitacaoCorrection(
        ZeeTSolicitacaoEntity before,
        ZeeTSolicitacaoEntity after,
        List<Integer> beforeLotes,
        CorrigirSolicitacaoRequestDTO dto
    ) {
        try {
            List<Integer> afterLotes = solicitacaoLoteRepository.findByIdSolicitacao(after.getId()).stream()
                .map(ZeeTSolicitacaoLoteEntity::getIdLote)
                .toList();
            List<ChangeLogsItem> items = List.of(
                logItem("exposicao", before.getExposicao(), after.getExposicao()),
                logItem("id_projeto", before.getIdProjeto(), after.getIdProjeto()),
                logItem("etapa_atual", before.getEtapaAtual(), after.getEtapaAtual()),
                logItem("data_correcao", before.getDataCorrecao(), after.getDataCorrecao()),
                logItem("user_corecao", before.getUserCorecao(), after.getUserCorecao()),
                logItem("ids_lote", beforeLotes, afterLotes),
                logItem("documentos_corrigidos", 0, countDocumentosCorrigidos(dto)),
                logItem("requisitos_corrigidos", 0, dto.getRequisitos() == null ? 0 : dto.getRequisitos().size())
            ).stream().filter(this::hasChanged).toList();
            if (items.isEmpty()) {
                return;
            }
            changeLogsService.createLogsAsyncSafe(
                items,
                "UPDATE",
                "zee_t_solicitacao",
                String.valueOf(after.getId()),
                "Corrigir solicitacao",
                AuditContext.builder()
                    .userId(firstText(after.getUserCorecao(), after.getUserSolic()))
                    .userEmail(firstText(dto.getUserName(), after.getUserCorecao(), after.getUserSolic()))
                    .build()
            );
        } catch (RuntimeException ex) {
            log.warn("Nao foi possivel gravar auditoria da correcao da solicitacao {}.", after.getId(), ex);
        }
    }

    private int countDocumentosCorrigidos(CorrigirSolicitacaoRequestDTO dto) {
        if (dto.getDocumentos() == null) {
            return 0;
        }
        return (int) dto.getDocumentos().stream()
            .filter(documento -> documento.getFicheiro() != null && !documento.getFicheiro().isEmpty())
            .count();
    }

    private ZeeTSolicitacaoEntity copySolicitacaoForAudit(ZeeTSolicitacaoEntity source) {
        ZeeTSolicitacaoEntity copy = new ZeeTSolicitacaoEntity();
        copy.setId(source.getId());
        copy.setExposicao(source.getExposicao());
        copy.setIdProjeto(source.getIdProjeto());
        copy.setEtapaAtual(source.getEtapaAtual());
        copy.setDataCorrecao(source.getDataCorrecao());
        copy.setUserCorecao(source.getUserCorecao());
        copy.setUserSolic(source.getUserSolic());
        return copy;
    }

    private ChangeLogsItem logItem(String column, Object oldValue, Object newValue) {
        ChangeLogsItem item = new ChangeLogsItem();
        item.setColumn(column);
        item.setOldValue(oldValue);
        item.setNewValue(newValue);
        return item;
    }

    private boolean hasChanged(ChangeLogsItem item) {
        return !Objects.equals(item.getOldValue(), item.getNewValue());
    }

    private void replaceLotes(String idsLote, ZeeTSolicitacaoEntity solicitacao) {
        List<Integer> lotes = parseIdsLote(idsLote);
        for (Integer idLote : lotes) {
            if (!loteRepository.existsById(idLote)) {
                throw new BusinessException("Lote nao encontrado: " + idLote);
            }
        }

        solicitacaoLoteRepository.deleteByIdSolicitacao(solicitacao.getId());
        if (lotes.isEmpty()) {
            return;
        }

        List<ZeeTSolicitacaoLoteEntity> entities = lotes.stream()
            .map(idLote -> {
                ZeeTSolicitacaoLoteEntity entity = new ZeeTSolicitacaoLoteEntity();
                entity.setIdSolicitacao(solicitacao.getId());
                entity.setIdLote(idLote);
                return entity;
            })
            .toList();
        solicitacaoLoteRepository.saveAll(entities);
    }

    private void corrigirRequisitos(
        List<SolicitacaoRequisitoRequestDTO> requisitos,
        ZeeTSolicitacaoEntity solicitacao
    ) {
        if (requisitos == null || requisitos.isEmpty()) {
            return;
        }

        for (SolicitacaoRequisitoRequestDTO requisito : requisitos) {
            ZeeTTpSolicTpDocEntity tpSolicTpDoc = loadRequisitoConfig(requisito.getIdTpSolicTpDoc(), solicitacao);
            if (isSim(requisito.getCumpre())) {
                insertSolicitacaoDocIfMissing(solicitacao, tpSolicTpDoc.getId());
            } else {
                solicitacaoDocRepository.deleteByIdSolicitacaoAndIdTpSolicTpDoc(solicitacao.getId(), tpSolicTpDoc.getId());
            }
        }
    }

    private ZeeTTpSolicTpDocEntity loadDocumentoConfig(Integer idTpSolicTpDoc, ZeeTSolicitacaoEntity solicitacao) {
        if (idTpSolicTpDoc == null) {
            throw new BusinessException("Documento sem configuracao informada.");
        }
        ZeeTTpSolicTpDocEntity tpSolicTpDoc = tpSolicTpDocRepository.findById(idTpSolicTpDoc)
            .orElseThrow(() -> new BusinessException("Documento configurado nao encontrado: " + idTpSolicTpDoc));
        if (!Objects.equals(tpSolicTpDoc.getIdTpSolic(), solicitacao.getIdTpSolicitacao())) {
            throw new BusinessException("Documento nao pertence ao tipo de solicitacao informado.");
        }
        if (tpSolicTpDoc.getIdTpDoc() == null) {
            throw new BusinessException("Configuracao informada nao e um documento: " + idTpSolicTpDoc);
        }
        if (!ESTADO_ATIVO.equalsIgnoreCase(tpSolicTpDoc.getDmEstado()) || !"PEDIDO".equalsIgnoreCase(tpSolicTpDoc.getPedResp())) {
            throw new BusinessException("Documento nao esta disponivel para envio pelo utilizador: " + idTpSolicTpDoc);
        }
        return tpSolicTpDoc;
    }

    private ZeeTTpSolicTpDocEntity loadRequisitoConfig(Integer idTpSolicTpDoc, ZeeTSolicitacaoEntity solicitacao) {
        if (idTpSolicTpDoc == null) {
            throw new BusinessException("Requisito sem configuracao informada.");
        }
        ZeeTTpSolicTpDocEntity tpSolicTpDoc = tpSolicTpDocRepository.findById(idTpSolicTpDoc)
            .orElseThrow(() -> new BusinessException("Requisito configurado nao encontrado: " + idTpSolicTpDoc));
        if (!Objects.equals(tpSolicTpDoc.getIdTpSolic(), solicitacao.getIdTpSolicitacao())) {
            throw new BusinessException("Requisito nao pertence ao tipo de solicitacao informado.");
        }
        if (!hasText(tpSolicTpDoc.getRequisito())) {
            throw new BusinessException("Configuracao informada nao e um requisito: " + idTpSolicTpDoc);
        }
        if (!ESTADO_ATIVO.equalsIgnoreCase(tpSolicTpDoc.getDmEstado())) {
            throw new BusinessException("Requisito nao esta ativo: " + idTpSolicTpDoc);
        }
        return tpSolicTpDoc;
    }

    private void upsertSolicitacaoDocPath(ZeeTSolicitacaoEntity solicitacao, Integer idTpSolicTpDoc, String path) {
        List<ZeeTSolicitacaoDocEntity> rows = solicitacaoDocRepository
            .findByIdSolicitacaoAndIdTpSolicTpDocOrderByIdDesc(solicitacao.getId(), idTpSolicTpDoc);
        ZeeTSolicitacaoDocEntity solicitacaoDoc = rows.isEmpty()
            ? newSolicitacaoDoc(solicitacao, idTpSolicTpDoc)
            : rows.get(0);
        solicitacaoDoc.setPath(path);
        solicitacaoDocRepository.save(solicitacaoDoc);
    }

    private void insertSolicitacaoDocIfMissing(ZeeTSolicitacaoEntity solicitacao, Integer idTpSolicTpDoc) {
        if (solicitacaoDocRepository.existsByIdSolicitacaoAndIdTpSolicTpDoc(solicitacao.getId(), idTpSolicTpDoc)) {
            return;
        }
        solicitacaoDocRepository.save(newSolicitacaoDoc(solicitacao, idTpSolicTpDoc));
    }

    private ZeeTSolicitacaoDocEntity newSolicitacaoDoc(ZeeTSolicitacaoEntity solicitacao, Integer idTpSolicTpDoc) {
        ZeeTSolicitacaoDocEntity solicitacaoDoc = new ZeeTSolicitacaoDocEntity();
        solicitacaoDoc.setIdEtapa(resolveIdEtapaForCorrection(solicitacao.getId()));
        solicitacaoDoc.setIdProcesso(solicitacao.getIdProcesso());
        solicitacaoDoc.setDataRegisto(LocalDate.now());
        solicitacaoDoc.setUserRegisto(firstText(solicitacao.getUserSolic(), "system"));
        solicitacaoDoc.setIdSolicitacao(solicitacao.getId());
        solicitacaoDoc.setIdTpSolicTpDoc(idTpSolicTpDoc);
        return solicitacaoDoc;
    }

    private BigDecimal resolveIdEtapaForCorrection(Integer idSolicitacao) {
        return solicitacaoDocRepository.findByIdSolicitacaoOrderByIdDesc(idSolicitacao).stream()
            .map(ZeeTSolicitacaoDocEntity::getIdEtapa)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(BigDecimal.ZERO);
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

    private String resolveDocumentoFilename(ZeeTTpSolicTpDocEntity tpSolicTpDoc, SolicitacaoDocumentoRequestDTO documento) {
        String nomeDocumento = tpSolicTpDoc.getIdTpDoc() == null
            ? null
            : tpDocRepository.findById(tpSolicTpDoc.getIdTpDoc())
                .map(tpDoc -> firstText(tpDoc.getNome(), tpDoc.getCodigo()))
                .orElse(null);
        return firstText(
            nomeDocumento,
            tpSolicTpDoc.getRequisito(),
            tpSolicTpDoc.getIdTpDoc() != null ? tpSolicTpDoc.getIdTpDoc().toString() : null,
            documento.getIdTpSolicTpDoc().toString()
        );
    }

    private static boolean isSim(String value) {
        return "SIM".equalsIgnoreCase(value != null ? value.trim() : null);
    }

    private static String resolveOrigem(SubmeterSolicitacaoRequestDTO dto) {
        return hasText(dto.getOrigem()) ? dto.getOrigem().trim() : ORIGEM_PORTAL;
    }

    private static String resolveTpProcesso(StartProcessResponse processo) {
        return firstText(
            processo.getProcessName(),
            processo.getName(),
            processo.getProcessDefinitionKey(),
            PROCESS_KEY_SOLICITACAO_INVESTIDOR
        );
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
        StartProcessResponse processo,
        ZeeTTpSolicitacaoEntity tpSolicitacao
    ) {
        if (!hasText(dto.getEmail())) {
            log.info("Nenhum email informado para notificacao da solicitacao {}.", solicitacao.getId());
            return;
        }

        String linkRecibo = gerarReciboPedidoPdfLink(solicitacao, pedido);
        TemplateContent content = resolveSubmissaoTemplate(processo, pedido, solicitacao, tpSolicitacao, linkRecibo);
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
        ZeeTTpSolicitacaoEntity tpSolicitacao,
        String linkRecibo
    ) {
        return templateNotifRepository.findFirstByCodigoAndDmEstadoOrderByIdDesc(TEMPLATE_SUBMISSAO_SOLICITACAO, ESTADO_ATIVO)
            .map(template -> new TemplateContent(
                replaceTemplate(template.getAssunto(), processo, pedido, solicitacao, tpSolicitacao, linkRecibo),
                replaceTemplate(template.getTemplateMsg(), processo, pedido, solicitacao, tpSolicitacao, linkRecibo)
            ))
            .orElseGet(() -> new TemplateContent(
                "Solicitação submetida",
                buildDefaultSubmissaoBody(processo, pedido, linkRecibo)
            ));
    }

    private String buildDefaultSubmissaoBody(StartProcessResponse processo, TPedidoEntity pedido, String linkRecibo) {
        StringBuilder body = new StringBuilder()
            .append("<p>Caro utente, a ZEEMSV informa que o seu processo")
            .append(hasText(pedido.getRequerente()) ? " de " + escapeHtml(pedido.getRequerente().trim()) : "")
            .append(" foi submetido.</p>");

        if (hasText(linkRecibo)) {
            body.append("<p>Para aceder ao recibo do pedido clique no link ")
                .append(buildHtmlLink(linkRecibo, "Recibo Pedido"))
                .append(".</p>");
        } else if (hasText(processo.getProcessInstanceId())) {
            body.append("<p>Número de processo: ")
                .append(escapeHtml(processo.getProcessInstanceId()))
                .append(".</p>");
        }

        return body
            .append("<p>Para mais informações, queira contactar os nossos serviços.</p>")
            .toString();
    }

    private String replaceTemplate(
        String template,
        StartProcessResponse processo,
        TPedidoEntity pedido,
        ZeeTSolicitacaoEntity solicitacao,
        ZeeTTpSolicitacaoEntity tpSolicitacao,
        String linkRecibo
    ) {
        String value = hasText(template) ? template : "";
        String processoNome = emptyIfNull(firstText(tpSolicitacao.getNome(), tpSolicitacao.getDescricao()));
        String nrProcesso = emptyIfNull(firstText(
            processo.getProcessInstanceId(),
            pedido.getIdProcesso() != null ? pedido.getIdProcesso().toString() : null
        ));
        String linkReciboValue = emptyIfNull(linkRecibo);
        value = replaceTemplateValue(value, processoNome, "processo", "nomeProcesso", "nome_processo", "processName", "PROCESS");
        value = replaceTemplateValue(value, nrProcesso, "nrProcesso", "nr_processo", "numeroProcesso", "numero_processo", "processInstanceId", "PROCESS_NUMBER");
        value = replaceTemplateValue(value, emptyIfNull(pedido.getRequerente()), "requerente", "nomeRequerente", "nome_requerente");
        value = replaceTemplateValue(value, String.valueOf(pedido.getId()), "nrPedido", "nr_pedido", "numeroPedido", "numero_pedido", "idPedido", "id_pedido");
        value = replaceTemplateValue(
            value,
            String.valueOf(solicitacao.getId()),
            "idSolicitacao",
            "id_solicitacao",
            "solicitacaoId",
            "solicitacao_id"
        );
        return EmailTemplateLinkHelper.applyReciboPedidoLink(value, linkReciboValue);
    }

    private static String replaceTemplateValue(String template, String replacement, String... keys) {
        String value = template;
        String safeReplacement = emptyIfNull(replacement);
        for (String key : keys) {
            value = value
                .replace("${" + key + "}", safeReplacement)
                .replace("{{" + key + "}}", safeReplacement)
                .replace("{" + key + "}", safeReplacement)
                .replace("#" + key + "#", safeReplacement)
                .replace(":" + key, safeReplacement)
                .replace("@" + key + "@", safeReplacement)
                .replace("$" + key + "$", safeReplacement)
                .replace("[[" + key + "]]", safeReplacement);
        }
        return value;
    }

    private String gerarReciboPedidoPdfLink(ZeeTSolicitacaoEntity solicitacao, TPedidoEntity pedido) {
        for (int attempt = 1; attempt <= RECIBO_PATH_LOOKUP_ATTEMPTS; attempt++) {
            TPedidoEntity currentPedido = pedidoRepository.findById(pedido.getId()).orElse(pedido);
            if (hasText(currentPedido.getPathRecibo())) {
                pedido.setPathRecibo(currentPedido.getPathRecibo());
                String viewerUrl = documentViewerUrlService.toViewerUrl(currentPedido.getPathRecibo(), RECIBO_PEDIDO_MIMETYPE);
                log.info(
                    "Link do recibo da solicitacao {} resolvido a partir de path_recibo {}. Viewer URL gerada? {}",
                    solicitacao.getId(),
                    currentPedido.getPathRecibo(),
                    hasText(viewerUrl)
                );
                return viewerUrl != null ? viewerUrl : "";
            }
            waitBeforeNextReciboPathLookup(attempt);
        }
        log.warn("Pedido {} da solicitacao {} sem path_recibo. Email sera enviado sem link do recibo.", pedido.getId(), solicitacao.getId());
        return "";
    }

    private void ensureReciboPedidoPath(ZeeTSolicitacaoEntity solicitacao, TPedidoEntity pedido) {
        TPedidoEntity currentPedido = pedidoRepository.findById(pedido.getId()).orElse(pedido);
        if (hasText(currentPedido.getPathRecibo())) {
            pedido.setPathRecibo(currentPedido.getPathRecibo());
            return;
        }
        if (!hasText(reciboPdfUrlTemplate)) {
            log.warn("Template de URL do recibo PDF nao configurado. Defina RECIBO_PDF_URL_TEMPLATE para gerar o recibo da solicitacao {}.", solicitacao.getId());
            return;
        }

        try {
            String reciboUrl = buildReciboPedidoPdfUrl(solicitacao);
            log.info("A chamar servico Laravel para gerar recibo PDF da solicitacao {}. URL: {}", solicitacao.getId(), reciboUrl);
            byte[] pdf = downloadReciboPedidoPdf(reciboUrl);
            ZeeTDocRelacaoEntity docRelacao = new ZeeTDocRelacaoEntity();
            docRelacao.setTipoRelacao(TIPO_RELACAO_SOLICITACAO);
            docRelacao.setIdRelacao(BigDecimal.valueOf(solicitacao.getId()));
            docRelacao.setDescricao(RECIBO_PEDIDO_DESCRICAO);

            ZeeTDocRelacaoEntity saved = documentoBus.saveGeneratedDocument(
                pdf,
                buildReciboPedidoFilename(solicitacao),
                buildReciboPedidoBasePath(solicitacao),
                RECIBO_PEDIDO_MIMETYPE,
                docRelacao,
                solicitacao.getUserSolic()
            );
            currentPedido.setPathRecibo(saved.getPath());
            pedidoRepository.saveAndFlush(currentPedido);
            pedido.setPathRecibo(saved.getPath());
            log.info("Recibo PDF da solicitacao {} guardado em {}.", solicitacao.getId(), saved.getPath());
        } catch (RuntimeException ex) {
            log.error("Erro ao gerar/gravar recibo PDF da solicitacao {}.", solicitacao.getId(), ex);
        }
    }

    private String resolveReciboPedidoPath(ZeeTSolicitacaoEntity solicitacao, TPedidoEntity pedido) {
        if (pedido != null && hasText(pedido.getPathRecibo())) {
            return pedido.getPathRecibo();
        }
        return DocumentoBus.addSlashToBasePath(buildReciboPedidoBasePath(solicitacao)) + buildReciboPedidoFilename(solicitacao);
    }

    private String buildReciboPedidoBasePath(ZeeTSolicitacaoEntity solicitacao) {
        return DocumentoBus.getBasePathForModuloOrObject(TIPO_RELACAO_SOLICITACAO, solicitacao.getId().toString());
    }

    private String buildReciboPedidoFilename(ZeeTSolicitacaoEntity solicitacao) {
        return "recibo-pedido-" + solicitacao.getId() + ".pdf";
    }

    private String buildReciboPedidoPdfUrl(ZeeTSolicitacaoEntity solicitacao) {
        String idSolicitacao = encode(solicitacao.getId());
        String url = reciboPdfUrlTemplate
            .replace("${id_solicitacao}", idSolicitacao)
            .replace("{id_solicitacao}", idSolicitacao)
            .replace("${idSolicitacao}", idSolicitacao)
            .replace("{idSolicitacao}", idSolicitacao);
        if (url.equals(reciboPdfUrlTemplate)) {
            String separator = url.endsWith("/") ? "" : "/";
            url = url + separator + idSolicitacao;
        }
        return url;
    }

    private byte[] downloadReciboPedidoPdf(String url) {
        try {
            return downloadReciboPedidoPdfOnce(url);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Thread interrompida ao gerar recibo no Laravel.", e);
        } catch (Exception e) {
            if (url.contains("://127.0.0.1:") || url.contains("://localhost:")) {
                String fallbackUrl = url
                    .replace("://127.0.0.1:", "://host.docker.internal:")
                    .replace("://localhost:", "://host.docker.internal:");
                try {
                    log.warn("Falha ao chamar Laravel em {}. A tentar fallback {}.", url, fallbackUrl);
                    return downloadReciboPedidoPdfOnce(fallbackUrl);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException("Thread interrompida ao gerar recibo no Laravel.", interrupted);
                } catch (Exception fallbackEx) {
                    e.addSuppressed(fallbackEx);
                }
            }
            throw new IllegalStateException("Falha ao gerar recibo no Laravel.", e);
        }
    }

    private byte[] downloadReciboPedidoPdfOnce(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .GET()
            .header("Accept", RECIBO_PEDIDO_MIMETYPE)
            .build();
        HttpResponse<byte[]> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofByteArray());
        String contentType = response.headers().firstValue("Content-Type").orElse("");
        int contentLength = response.body() != null ? response.body().length : 0;
        log.info("Resposta do servico Laravel de recibo. URL: {}. HTTP: {}. Content-Type: {}. Bytes: {}",
            url,
            response.statusCode(),
            contentType,
            contentLength
        );
        if (response.statusCode() / 100 != 2) {
            throw new IllegalStateException("Laravel retornou HTTP " + response.statusCode() + " ao gerar recibo.");
        }
        byte[] body = response.body();
        if (body == null || body.length == 0) {
            throw new IllegalStateException("Laravel retornou recibo vazio.");
        }
        if (!isPdf(body)) {
            throw new IllegalStateException("Laravel nao retornou PDF valido. Content-Type: " + contentType + ". Inicio da resposta: " + responsePreview(body));
        }
        return body;
    }

    private static boolean isPdf(byte[] body) {
        return body.length >= 5
            && body[0] == '%'
            && body[1] == 'P'
            && body[2] == 'D'
            && body[3] == 'F'
            && body[4] == '-';
    }

    private static String responsePreview(byte[] body) {
        int length = Math.min(body.length, 120);
        return new String(body, 0, length, StandardCharsets.UTF_8)
            .replace("\r", " ")
            .replace("\n", " ")
            .trim();
    }

    private static void waitBeforeNextReciboPathLookup(int attempt) {
        if (attempt >= RECIBO_PATH_LOOKUP_ATTEMPTS) {
            return;
        }
        try {
            Thread.sleep(RECIBO_PATH_LOOKUP_DELAY_MS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
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

    private static String emptyIfNull(String value) {
        return value != null ? value : "";
    }

    private static String buildHtmlLink(String url, String label) {
        String safeUrl = escapeHtml(url);
        return "<a href=\"" + safeUrl + "\">" + escapeHtml(label) + "</a>";
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    private static String encode(Object value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
    }

    private record TemplateContent(String subject, String body) {
    }

    private record RequerenteDados(String nome, String nif, String email, String endereco) {
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
        dto.setFlagObrigatorioDesc("SIM".equalsIgnoreCase(p.getFlagObrigatorio()) ? "Sim" : "Nao");
        dto.setAnexado(p.getId() != null);
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
        dto.setFlagObrigatorioDesc("SIM".equalsIgnoreCase(p.getFlagObrigatorio()) ? "Sim" : "Nao");
        dto.setAnexado(false);
        dto.setPedResp(p.getPedResp());
        return dto;
    }

    private SolicitacaoRequisitoResponseDTO toRequisitoResponse(SolicitacaoRequisitoProjection p) {
        SolicitacaoRequisitoResponseDTO dto = new SolicitacaoRequisitoResponseDTO();
        dto.setIdTpSolicTpDoc(p.getIdTpSolicTpDoc());
        dto.setRequisito(p.getRequisito());
        dto.setFlagObrigatorio(p.getFlagObrigatorio());
        dto.setFlagObrigatorioDesc("SIM".equalsIgnoreCase(p.getFlagObrigatorio()) ? "Sim" : "Nao");
        dto.setCumpre("NAO");
        return dto;
    }

    private SolicitacaoTaxaResponseDTO toTaxaResponse(ZeeTTpSolicTaxaEntity entity) {
        SolicitacaoTaxaResponseDTO dto = new SolicitacaoTaxaResponseDTO();
        dto.setIdTpSolicTaxa(entity.getId());
        dto.setIdTaxa(entity.getIdTaxa());
        dto.setTaxa(entity.getDescricao());
        dto.setTipoTaxaCodigo(entity.getTipoTaxa());
        dto.setTipoTaxa(domainHelper.describe(DomainDescriptionHelper.TIPO_TAXA, entity.getTipoTaxa()));
        dto.setValor(entity.getValor());
        dto.setValorConfigurado(entity.getValor());
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
