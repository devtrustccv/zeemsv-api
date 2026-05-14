package cv.zeemsv.api.application.projeto.mapper;

import cv.zeemsv.api.application.projeto.dto.ProjetoRequestDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoResponseDTO;
import cv.zeemsv.api.domain.projeto.model.Projeto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjetoDtoMapper {
    Projeto toModel(ProjetoRequestDTO dto);
    ProjetoResponseDTO toResponse(Projeto model);
}
