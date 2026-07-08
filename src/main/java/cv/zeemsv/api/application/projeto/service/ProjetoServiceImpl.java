package cv.zeemsv.api.application.projeto.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.projeto.dto.ProjetoRequestDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoResponseDTO;
import cv.zeemsv.api.application.projeto.mapper.ProjetoDtoMapper;
import cv.zeemsv.api.domain.projeto.business.ProjetoBus;
import cv.zeemsv.api.infrastructure.repository.ZeeTProjInvestRepository;
import cv.zeemsv.api.infrastructure.repository.projection.ProjetoInvestidorProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoServiceImpl implements ProjetoService {
    private final ProjetoBus bus;
    private final ProjetoDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTProjInvestRepository projetoRepository;

    @Override @Transactional
    public ProjetoResponseDTO create(ProjetoRequestDTO dto) { return enrich(mapper.toResponse(bus.create(mapper.toModel(dto)))); }

    @Override @Transactional
    public ProjetoResponseDTO update(Integer id, ProjetoRequestDTO dto) { return enrich(mapper.toResponse(bus.update(id, mapper.toModel(dto)))); }

    @Override @Transactional(readOnly = true)
    public ProjetoResponseDTO findById(Integer id) { return enrich(mapper.toResponse(bus.findById(id))); }

    @Override @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).map(this::enrich).toList(); }

    @Override @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> findByInvestidorId(Integer idInvestidor) {
        return projetoRepository.findDetalheByInvestidorId(idInvestidor).stream()
            .map(this::toResponse)
            .toList();
    }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private ProjetoResponseDTO enrich(ProjetoResponseDTO dto) {
        dto.setEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, dto.getEstado()));
        dto.setDmEnquadramenoDesc(domainHelper.describe(DomainDescriptionHelper.FORMA_COMERCIALIZACAO, dto.getDmEnquadrameno()));
        dto.setDmRegimeDesc(domainHelper.describe(DomainDescriptionHelper.REGIME, dto.getDmRegime()));
        dto.setDmProdutoServicoDesc(domainHelper.describe(DomainDescriptionHelper.PRODUTO_SERVICO, dto.getDmProdutoServico()));
        dto.setDmEstadoInstallDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_INSTALACAO, dto.getDmEstadoInstall()));
        dto.setDmEstadoProcDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROCESO, dto.getDmEstadoProc()));
        dto.setDmDocFaltaDesc(domainHelper.describeBoolean(DomainDescriptionHelper.SIM_NAO, dto.getDmDocFalta()));
        dto.setDmSituacaoDesc(domainHelper.describe(DomainDescriptionHelper.SITUACAO_PROJ, dto.getDmSituacao()));
        dto.setDmEstadoProjDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_PROJETO, dto.getDmEstadoProj()));
        return dto;
    }

    private ProjetoResponseDTO toResponse(ProjetoInvestidorProjection projection) {
        ProjetoResponseDTO dto = new ProjetoResponseDTO();
        dto.setId(projection.getId());
        dto.setEstado(projection.getEstado());
        dto.setDenominacao(projection.getDenominacao());
        dto.setDmEnquadrameno(projection.getDmEnquadrameno());
        dto.setDmRegime(projection.getDmRegime());
        dto.setDmProdutoServico(projection.getDmProdutoServico());
        dto.setDmEstadoInstall(projection.getDmEstadoInstall());
        dto.setDmEstadoProc(projection.getDmEstadoProc());
        dto.setDateCreate(projection.getDateCreate());
        dto.setUserCreate(projection.getUserCreate());
        dto.setDmDocFalta(projection.getDmDocFalta());
        dto.setIdInvestidorCae(projection.getIdInvestidorCae());
        dto.setAtividadePrincipal(projection.getAtividadePrincipal());
        dto.setAtividadePrincipalSetor(projection.getAtividadePrincipalSetor());
        dto.setDmSituacao(projection.getDmSituacao());
        dto.setIdInvestidor(projection.getIdInvestidor());
        dto.setDmEstadoProj(projection.getDmEstadoProj());
        dto.setDataDesistencia(projection.getDataDesistencia());
        dto.setUserDesistencia(projection.getUserDesistencia());
        dto.setMotivo(projection.getMotivo());
        return enrich(dto);
    }
}
