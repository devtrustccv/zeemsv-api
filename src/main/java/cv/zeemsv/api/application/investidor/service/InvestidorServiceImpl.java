package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.investidor.dto.InvestidorDocumentoResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorUserResponseDTO;
import cv.zeemsv.api.domain.documento.business.DocumentViewerUrlService;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.application.investidor.mapper.InvestidorDtoMapper;
import cv.zeemsv.api.domain.investidor.business.InvestidorBus;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.repository.ZeeTDocRelacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestidorServiceImpl implements InvestidorService {
    private final InvestidorBus bus;
    private final InvestidorDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTDocRelacaoRepository docRelacaoRepository;
    private final DocumentViewerUrlService documentViewerUrlService;

    @Override @Transactional
    public InvestidorResponseDTO create(InvestidorRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public InvestidorResponseDTO update(Integer id, InvestidorRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public InvestidorResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<InvestidorResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional(readOnly = true)
    public List<InvestidorUserResponseDTO> findByUserEmail(String email) {
        return bus.findByUserEmail(email).stream().map(this::toUserResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestidorDocumentoResponseDTO> findDocumentosByInvestidorId(Integer idInvestidor) {
        if (idInvestidor == null) {
            throw new BusinessException("Informe o id do investidor.");
        }
        bus.findById(idInvestidor);
        return docRelacaoRepository.findDocumentosByInvestidorId(idInvestidor)
            .stream()
            .map(this::toDocumentoResponse)
            .toList();
    }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private InvestidorDocumentoResponseDTO toDocumentoResponse(cv.zeemsv.api.infrastructure.repository.projection.InvestidorDocumentoProjection projection) {
        InvestidorDocumentoResponseDTO dto = new InvestidorDocumentoResponseDTO();
        dto.setId(projection.getId());
        dto.setTipoRelacao(projection.getTipoRelacao());
        dto.setTipoRelacaoDesc(resolveTipoRelacaoDesc(projection.getTipoRelacao()));
        dto.setIdRelacao(projection.getIdRelacao());
        dto.setObjetoDescricao(projection.getObjetoDescricao());
        dto.setIdDoc(projection.getIdDoc());
        dto.setIdTpDoc(projection.getIdTpDoc());
        dto.setNomeDocumento(projection.getNomeDocumento());
        dto.setEstado(projection.getEstado());
        dto.setEstadoDesc(resolveEstadoDesc(projection.getEstado()));
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

    private String resolveTipoRelacaoDesc(String tipoRelacao) {
        return firstText(
            domainHelper.describe(DomainDescriptionHelper.OBJECTO, tipoRelacao),
            domainHelper.describe(DomainDescriptionHelper.TIPO_OBJETO, tipoRelacao),
            defaultTipoRelacaoDesc(tipoRelacao),
            tipoRelacao
        );
    }

    private String resolveEstadoDesc(String estado) {
        return firstText(
            domainHelper.describe(DomainDescriptionHelper.ESTADO, estado),
            defaultEstadoDesc(estado),
            estado
        );
    }

    private String defaultTipoRelacaoDesc(String tipoRelacao) {
        if (!StringUtils.hasText(tipoRelacao)) {
            return null;
        }
        return switch (tipoRelacao.trim().toUpperCase()) {
            case "INVESTIDOR" -> "Investidor";
            case "INVESTIDOR_SINGULAR" -> "Investidor singular";
            case "SOLICITACAO" -> "Solicitação";
            case "NOTIFICACAO" -> "Notificação";
            case "ATIVIDADE" -> "Atividade";
            case "PROJETO" -> "Projeto";
            default -> null;
        };
    }

    private String defaultEstadoDesc(String estado) {
        if (!StringUtils.hasText(estado)) {
            return null;
        }
        return switch (estado.trim().toUpperCase()) {
            case "A", "ATIVO" -> "Ativo";
            case "I", "INATIVO" -> "Inativo";
            case "ELIMINADO" -> "Eliminado";
            case "PENDENTE" -> "Pendente";
            default -> null;
        };
    }

    private String removeExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return fileName;
        }
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private InvestidorUserResponseDTO toUserResponse(cv.zeemsv.api.domain.investidor.model.InvestidorUser model) {
        InvestidorUserResponseDTO dto = mapper.toUserResponse(model);
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, dto.getDmEstado()));
        dto.setDmTipoInvestidorDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_INVESTIDOR, dto.getDmTipoInvestidor()));
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, dto.getDmTpRepresentante()));
        dto.setDmPrincipalDesc(domainHelper.describe(DomainDescriptionHelper.SIM_NAO, dto.getDmPrincipal()));
        return dto;
    }
}
