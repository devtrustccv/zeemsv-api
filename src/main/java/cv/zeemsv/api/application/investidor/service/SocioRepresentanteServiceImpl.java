package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SocioRepresentanteServiceImpl implements SocioRepresentanteService {
    private final ZeeTSocioRepresRepository repository;

    @Override
    @Transactional
    public SocioRepresentanteResponseDTO create(SocioRepresentanteRequestDTO dto) {
        validateUniqueFields(dto);

        ZeeTSocioRepresEntity entity = new ZeeTSocioRepresEntity();
        entity.setNome(trim(dto.getNome()));
        entity.setNacionalidade(trim(dto.getNacionalidade()));
        entity.setNif(trim(dto.getNif()));
        entity.setTipoDoc(trim(dto.getTipoDoc()));
        entity.setNrDoc(trim(dto.getNrDoc()));
        entity.setTelefone(dto.getTelefone());
        entity.setTelemovel(dto.getTelemovel());
        entity.setEmail(trim(dto.getEmail()));
        entity.setEstado("A");
        entity.setDateCreate(LocalDate.now());
        entity.setIndicativoPais(trim(dto.getIndicativoPais()));

        return toResponse(repository.save(entity));
    }

    private void validateUniqueFields(SocioRepresentanteRequestDTO dto) {
        if (StringUtils.hasText(dto.getNif()) && !repository.findByNif(trim(dto.getNif())).isEmpty()) {
            throw new BusinessException("Ja existe socio/representante com este NIF.",
                new RuntimeException("Ja existe socio/representante com este NIF."));
        }

        if (StringUtils.hasText(dto.getNrDoc()) && !repository.findByNrDoc(trim(dto.getNrDoc())).isEmpty()) {
            throw new BusinessException("Ja existe socio/representante com este numero de documento.",
                new RuntimeException("Ja existe socio/representante com este numero de documento."));
        }
    }

    private String trim(String value) {
        return value != null ? value.trim() : null;
    }

    private SocioRepresentanteResponseDTO toResponse(ZeeTSocioRepresEntity entity) {
        SocioRepresentanteResponseDTO dto = new SocioRepresentanteResponseDTO();
        dto.setId(entity.getId());
        dto.setIdInvestidor(entity.getIdInvestidor());
        dto.setNome(entity.getNome());
        dto.setNacionalidade(entity.getNacionalidade());
        dto.setNif(entity.getNif());
        dto.setTipoDoc(entity.getTipoDoc());
        dto.setNrDoc(entity.getNrDoc());
        dto.setDmTpRepresentante(entity.getDmTpRepresentante());
        dto.setTelefone(entity.getTelefone());
        dto.setTelemovel(entity.getTelemovel());
        dto.setEmail(entity.getEmail());
        dto.setFlagSocio(entity.getFlagSocio());
        dto.setFlagRepresentante(entity.getFlagRepresentante());
        dto.setDmPrincipal(entity.getDmPrincipal());
        dto.setEstado(entity.getEstado());
        dto.setDateCreate(entity.getDateCreate());
        dto.setUserCreate(entity.getUserCreate());
        dto.setIndicativoPais(entity.getIndicativoPais());
        return dto;
    }
}
