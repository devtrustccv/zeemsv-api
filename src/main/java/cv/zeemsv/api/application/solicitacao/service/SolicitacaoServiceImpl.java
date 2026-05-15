package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.application.solicitacao.mapper.SolicitacaoDtoMapper;
import cv.zeemsv.api.domain.solicitacao.business.SolicitacaoBus;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.projection.SolicitacaoInvestidorProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoServiceImpl implements SolicitacaoService {
    private final SolicitacaoBus bus;
    private final SolicitacaoDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTSolicitacaoRepository solicitacaoRepository;

    @Override @Transactional
    public SolicitacaoResponseDTO create(SolicitacaoRequestDTO dto) { return enrich(mapper.toResponse(bus.create(mapper.toModel(dto)))); }

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

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private SolicitacaoResponseDTO enrich(SolicitacaoResponseDTO dto) {
        dto.setDmEstadoProcDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROC_SOLICIT, dto.getDmEstadoProc()));
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
