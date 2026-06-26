package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.geografia.service.NacionalidadeResolver;
import cv.zeemsv.api.application.investidor.dto.RepresentanteInvestidorResponseDTO;
import cv.zeemsv.api.infrastructure.repository.ZeeTRepresInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.projection.RepresentanteInvestidorProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RepresentanteInvestidorServiceImpl implements RepresentanteInvestidorService {
    private final ZeeTRepresInvestidorRepository repository;
    private final DomainDescriptionHelper domainHelper;
    private final NacionalidadeResolver nacionalidadeResolver;

    @Override
    @Transactional(readOnly = true)
    public List<RepresentanteInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor) {
        return repository.findByInvestidorId(idInvestidor)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private RepresentanteInvestidorResponseDTO toResponse(RepresentanteInvestidorProjection projection) {
        RepresentanteInvestidorResponseDTO dto = new RepresentanteInvestidorResponseDTO();
        dto.setId(projection.getId());
        dto.setIdInvestidor(projection.getIdInvestidor());
        dto.setIdSocioRepres(projection.getIdSocioRepres());
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
        dto.setNacionalidade(projection.getNacionalidade());
        dto.setNacionalidadeId(nacionalidadeResolver.resolveId(projection.getNacionalidade()));
        dto.setNif(projection.getNif());
        dto.setTipoDoc(projection.getTipoDoc());
        dto.setTipoDocDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_DOCUMENTO, projection.getTipoDoc()));
        dto.setNrDoc(projection.getNrDoc());
        dto.setTelefone(projection.getTelefone());
        dto.setTelemovel(projection.getTelemovel());
        dto.setEmail(projection.getEmail());
        dto.setIndicativoPais(projection.getIndicativoPais());
        return dto;
    }
}
