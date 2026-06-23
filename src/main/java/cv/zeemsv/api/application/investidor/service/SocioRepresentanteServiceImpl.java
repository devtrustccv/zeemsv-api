package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteRequestDTO;
import cv.zeemsv.api.application.investidor.dto.SocioRepresentanteResponseDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import cv.zeemsv.api.utils.enums.DefaultStatusApp;
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
        ZeeTSocioRepresEntity entity = new ZeeTSocioRepresEntity();
        entity.setIdInvestidor(dto.getIdInvestidor());
        entity.setNome(dto.getNome());
        entity.setNacionalidade(dto.getNacionalidade());
        entity.setNif(dto.getNif());
        entity.setTipoDoc(dto.getTipoDoc());
        entity.setNrDoc(dto.getNrDoc());
        entity.setDmTpRepresentante(dto.getDmTpRepresentante());
        entity.setTelefone(dto.getTelefone());
        entity.setTelemovel(dto.getTelemovel());
        entity.setEmail(dto.getEmail());
        entity.setFlagSocio(dto.getFlagSocio());
        entity.setFlagRepresentante(dto.getFlagRepresentante());
        entity.setDmPrincipal(dto.getDmPrincipal());
        entity.setEstado(StringUtils.hasText(dto.getEstado()) ? dto.getEstado() : DefaultStatusApp.ATIVO.getKey());
        entity.setDateCreate(LocalDate.now());
        entity.setUserCreate(dto.getUserCreate());
        entity.setIndicativoPais(dto.getIndicativoPais());

        return toResponse(repository.save(entity));
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
