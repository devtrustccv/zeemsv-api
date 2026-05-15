package cv.zeemsv.api.application.lote.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.lote.dto.LoteInvestidorResponseDTO;
import cv.zeemsv.api.application.lote.dto.LoteRequestDTO;
import cv.zeemsv.api.application.lote.dto.LoteResponseDTO;
import cv.zeemsv.api.application.lote.mapper.LoteDtoMapper;
import cv.zeemsv.api.domain.lote.business.LoteBus;
import cv.zeemsv.api.infrastructure.repository.ZeeTLoteRepository;
import cv.zeemsv.api.infrastructure.repository.projection.LoteInvestidorProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class LoteServiceImpl implements LoteService {
    private final LoteBus bus;
    private final LoteDtoMapper mapper;
    private final ZeeTLoteRepository loteRepository;
    private final DomainDescriptionHelper domainHelper;

    @Override @Transactional
    public LoteResponseDTO create(LoteRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public LoteResponseDTO update(Integer id, LoteRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public LoteResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<LoteResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional(readOnly = true)
    public List<LoteInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor) {
        List<LoteInvestidorProjection> proprietarios = loteRepository.findProprietarioByInvestidorId(idInvestidor);
        Set<Integer> idsProprietario = new HashSet<>(proprietarios.stream().map(LoteInvestidorProjection::getIdLote).toList());
        List<LoteInvestidorProjection> projetos = loteRepository.findProjetoByInvestidorId(idInvestidor)
            .stream()
            .filter(projeto -> !idsProprietario.contains(projeto.getIdLote()))
            .toList();

        return Stream.concat(
                proprietarios.stream().map(proprietario -> toInvestidorResponse(proprietario, "PROPRIETARIO")),
                projetos.stream().map(projeto -> toInvestidorResponse(projeto, projeto.getDmEnquadramento()))
            )
            .toList();
    }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private LoteInvestidorResponseDTO toInvestidorResponse(LoteInvestidorProjection projection, String condicao) {
        LoteInvestidorResponseDTO dto = new LoteInvestidorResponseDTO();
        dto.setIdLote(projection.getIdLote());
        dto.setRefLote(projection.getRefLote());
        dto.setNip(projection.getNip());
        dto.setDmSituacaoCd(projection.getDmSituacaoCd());
        dto.setDmSituacaoCdDesc(domainHelper.describe(DomainDescriptionHelper.SITUACAO_LOTE, projection.getDmSituacaoCd()));
        dto.setEstado(projection.getEstado());
        dto.setIdZona(projection.getIdZona());
        dto.setZona(projection.getZona());
        dto.setArea(projection.getArea());
        dto.setAreaInicial(projection.getAreaInicial());
        dto.setIdInvestidor(projection.getIdInvestidor());
        dto.setCondicao(condicao);
        dto.setOrigemAssociacao(projection.getOrigemAssociacao());
        dto.setIdAssociacao(projection.getIdAssociacao());
        dto.setIdProjeto(projection.getIdProjeto());
        dto.setDmEstadoAssociacao(projection.getDmEstadoAssociacao());
        dto.setDmEstadoAssociacaoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, projection.getDmEstadoAssociacao()));
        dto.setDataAssociacao(projection.getDataAssociacao());
        dto.setUtilizadorAssociacao(projection.getUtilizadorAssociacao());
        dto.setDmEnquadramento(projection.getDmEnquadramento());
        dto.setDmEnquadramentoDesc(domainHelper.describe(DomainDescriptionHelper.FORMA_COMERCIALIZACAO, projection.getDmEnquadramento()));
        dto.setProjetoDenominacao(projection.getProjetoDenominacao());
        dto.setProjetoDmRegime(projection.getProjetoDmRegime());
        dto.setProjetoDmRegimeDesc(domainHelper.describe(DomainDescriptionHelper.REGIME, projection.getProjetoDmRegime()));
        dto.setProjetoDmProdutoServico(projection.getProjetoDmProdutoServico());
        dto.setProjetoDmProdutoServicoDesc(domainHelper.describe(DomainDescriptionHelper.PRODUTO_SERVICO, projection.getProjetoDmProdutoServico()));
        dto.setProjetoDmEstadoProc(projection.getProjetoDmEstadoProc());
        dto.setProjetoDmEstadoProcDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROCESO, projection.getProjetoDmEstadoProc()));
        dto.setProjetoDmSituacao(projection.getProjetoDmSituacao());
        dto.setProjetoDmSituacaoDesc(domainHelper.describe(DomainDescriptionHelper.SITUACAO_PROJ, projection.getProjetoDmSituacao()));
        dto.setProjetoDmEstadoProj(projection.getProjetoDmEstadoProj());
        dto.setProjetoDmEstadoProjDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROJETO, projection.getProjetoDmEstadoProj()));
        dto.setProjetoDateCreate(projection.getProjetoDateCreate());
        return dto;
    }
}
