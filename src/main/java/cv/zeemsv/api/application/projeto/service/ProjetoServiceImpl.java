package cv.zeemsv.api.application.projeto.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.geografia.service.NacionalidadeResolver;
import cv.zeemsv.api.application.lote.service.LoteService;
import cv.zeemsv.api.application.projeto.dto.ProjetoDocumentoResponseDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoRequestDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoRepresentanteResponseDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoResponseDTO;
import cv.zeemsv.api.application.projeto.mapper.ProjetoDtoMapper;
import cv.zeemsv.api.domain.documento.business.DocumentViewerUrlService;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.domain.projeto.business.ProjetoBus;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.repository.ZeeTDocRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTProjetoRepresRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTProjInvestRepository;
import cv.zeemsv.api.infrastructure.repository.projection.InvestidorDocumentoProjection;
import cv.zeemsv.api.infrastructure.repository.projection.ProjetoInvestidorProjection;
import cv.zeemsv.api.infrastructure.repository.projection.ProjetoRepresentanteProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoServiceImpl implements ProjetoService {
    private final ProjetoBus bus;
    private final ProjetoDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTProjInvestRepository projetoRepository;
    private final ZeeTProjetoRepresRepository projetoRepresRepository;
    private final ZeeTDocRelacaoRepository docRelacaoRepository;
    private final LoteService loteService;
    private final DocumentViewerUrlService documentViewerUrlService;
    private final NacionalidadeResolver nacionalidadeResolver;

    @Override @Transactional
    public ProjetoResponseDTO create(ProjetoRequestDTO dto) { return enrich(mapper.toResponse(bus.create(mapper.toModel(dto)))); }

    @Override @Transactional
    public ProjetoResponseDTO update(Integer id, ProjetoRequestDTO dto) { return enrich(mapper.toResponse(bus.update(id, mapper.toModel(dto)))); }

    @Override @Transactional(readOnly = true)
    public ProjetoResponseDTO findById(Integer id) {
        ProjetoResponseDTO projeto = projetoRepository.findDetalheById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new BusinessException("Projeto nao encontrado: " + id));
        return enrichWithDetalhes(projeto);
    }

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

    private ProjetoResponseDTO enrichWithDetalhes(ProjetoResponseDTO dto) {
        dto.setRepresentantes(projetoRepresRepository.findAtivosByProjetoId(dto.getId()).stream()
            .map(this::toRepresentanteResponse)
            .toList());
        dto.setLotes(loteService.findAssociados(null, dto.getId()));
        dto.setDocumentos(docRelacaoRepository.findDocumentosByProjetoId(dto.getId()).stream()
            .map(this::toDocumentoResponse)
            .toList());
        return dto;
    }

    private ProjetoRepresentanteResponseDTO toRepresentanteResponse(ProjetoRepresentanteProjection projection) {
        ProjetoRepresentanteResponseDTO dto = new ProjetoRepresentanteResponseDTO();
        dto.setId(projection.getId());
        dto.setIdProjeto(projection.getIdProjeto());
        dto.setIdRepresSocio(projection.getIdRepresSocio());
        dto.setIdRepresInvestidor(projection.getIdRepresInvestidor());
        dto.setEstado(projection.getEstado());
        dto.setEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, projection.getEstado()));
        dto.setDateCreate(projection.getDateCreate());
        dto.setUserCreate(projection.getUserCreate());
        dto.setIdInvestidor(projection.getIdInvestidor());
        dto.setIdOrdem(projection.getIdOrdem());
        dto.setDmTpRepresentante(projection.getDmTpRepresentante());
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, projection.getDmTpRepresentante()));
        dto.setFlagRepresentante(projection.getFlagRepresentante());
        dto.setFlagSocio(projection.getFlagSocio());
        dto.setDmPrincipal(projection.getDmPrincipal());
        dto.setDmPrincipalDesc(domainHelper.describe(DomainDescriptionHelper.SIM_NAO, projection.getDmPrincipal()));
        dto.setDmEstado(projection.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, projection.getDmEstado()));
        dto.setDataRegisto(projection.getDataRegisto());
        dto.setUserRegisto(projection.getUserRegisto());
        dto.setIdUser(projection.getIdUser());
        dto.setNome(projection.getNome());
        String nacionalidade = projection.getNacionalidade();
        dto.setNacionalidade(nacionalidadeResolver.resolveDescricao(nacionalidade));
        dto.setNacionalidadeId(nacionalidadeResolver.resolveId(nacionalidade));
        dto.setNif(projection.getNif());
        dto.setTipoDoc(projection.getTipoDoc());
        dto.setTipoDocDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_DOCUMENTO, projection.getTipoDoc()));
        dto.setNrDoc(projection.getNrDoc());
        dto.setTelefone(projection.getTelefone());
        dto.setTelemovel(projection.getTelemovel());
        dto.setEmail(projection.getEmail());
        dto.setFotoUrl(projection.getFotoUrl());
        dto.setFotoPath(projection.getFotoPath());
        dto.setIndicativoPais(projection.getIndicativoPais());
        dto.setEndereco(projection.getEndereco());
        return dto;
    }

    private ProjetoDocumentoResponseDTO toDocumentoResponse(InvestidorDocumentoProjection projection) {
        ProjetoDocumentoResponseDTO dto = new ProjetoDocumentoResponseDTO();
        dto.setId(projection.getId());
        dto.setTipoRelacao(projection.getTipoRelacao());
        dto.setTipoRelacaoDesc("Projeto");
        dto.setIdRelacao(projection.getIdRelacao());
        dto.setObjetoDescricao(projection.getObjetoDescricao());
        dto.setIdDoc(projection.getIdDoc());
        dto.setIdTpDoc(projection.getIdTpDoc());
        dto.setNomeDocumento(projection.getNomeDocumento());
        dto.setEstado(projection.getEstado());
        dto.setEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, projection.getEstado()));
        dto.setDateCreate(projection.getDateCreate());
        dto.setUserCreate(projection.getUserCreate());
        dto.setPath(projection.getPath());
        dto.setUrl(StringUtils.hasText(projection.getPath()) ? documentViewerUrlService.toViewerUrl(projection.getPath(), projection.getMimetype()) : null);
        dto.setNomeFicheiro(removeExtension(DocumentoBus.getFileNameWithExtensionByPath(projection.getPath())));
        dto.setDocSize(projection.getDocSize());
        dto.setMimetype(projection.getMimetype());
        dto.setDescricao(projection.getDescricao());
        return dto;
    }

    private String removeExtension(String filename) {
        if (!StringUtils.hasText(filename)) {
            return filename;
        }
        int index = filename.lastIndexOf('.');
        return index > 0 ? filename.substring(0, index) : filename;
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
        dto.setDateUpdate(projection.getDateUpdate());
        dto.setUserUpdate(projection.getUserUpdate());
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
