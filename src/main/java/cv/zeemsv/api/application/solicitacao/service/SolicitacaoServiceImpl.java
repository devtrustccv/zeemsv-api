package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
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
import cv.zeemsv.api.infrastructure.entity.TPedidoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTDocRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoDocEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTpDocEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.TPedidoRepository;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SolicitacaoServiceImpl implements SolicitacaoService {
    private static final String PROCESS_KEY_SOLICITACAO_INVESTIDOR = "proc_solicitacao_investidor";
    private static final String ESTADO_PENDENTE = "PENDENTE";
    private static final String TIPO_RELACAO_SOLICITACAO = "SOLICITACAO";

    private final SolicitacaoBus bus;
    private final SolicitacaoDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTSolicitacaoRepository solicitacaoRepository;
    private final ZeeTSolicitacaoDocRepository solicitacaoDocRepository;
    private final ZeeTTpSolicitacaoRepository tpSolicitacaoRepository;
    private final ZeeTTpSolicTaxaRepository tpSolicTaxaRepository;
    private final ZeeTTpSolicTpDocRepository tpSolicTpDocRepository;
    private final TPedidoRepository pedidoRepository;
    private final DocumentViewerUrlService documentViewerUrlService;
    private final DocumentoBus documentoBus;
    private final ProcessStartService processStartService;

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
        if (!hasText(dto.getOrigem())) {
            throw new BusinessException("O campo origem e obrigatorio.");
        }

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
        pedido.setDmOrigemReg(dto.getOrigem());
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
        solicitacao.setDmOrigem(dto.getOrigem());
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
