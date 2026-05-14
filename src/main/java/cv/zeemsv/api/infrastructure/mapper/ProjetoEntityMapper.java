package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.projeto.model.Projeto;
import cv.zeemsv.api.infrastructure.entity.ZeeTProjInvestEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjetoEntityMapper {
    ZeeTProjInvestEntity toEntity(Projeto model);
    Projeto toModel(ZeeTProjInvestEntity entity);
}
