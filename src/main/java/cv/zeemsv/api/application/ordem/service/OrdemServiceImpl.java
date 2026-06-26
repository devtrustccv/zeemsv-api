package cv.zeemsv.api.application.ordem.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.geografia.service.NacionalidadeResolver;
import cv.zeemsv.api.application.ordem.dto.OrdemResponseDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTOrdemEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTOrdemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdemServiceImpl implements OrdemService {
    private final ZeeTOrdemRepository repository;
    private final DomainDescriptionHelper domainHelper;
    private final NacionalidadeResolver nacionalidadeResolver;

    @Override
    @Transactional(readOnly = true)
    public List<OrdemResponseDTO> findAll() {
        return repository.findAllByOrderByNomeAsc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private OrdemResponseDTO toResponse(ZeeTOrdemEntity entity) {
        OrdemResponseDTO dto = new OrdemResponseDTO();
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
        dto.setNacionalidadeId(nacionalidadeResolver.resolveId(entity.getNacionalidade()));
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
