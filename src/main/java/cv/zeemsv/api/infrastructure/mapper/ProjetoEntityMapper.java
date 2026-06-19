package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.projeto.model.Projeto;
import cv.zeemsv.api.infrastructure.entity.ZeeTProjInvestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjetoEntityMapper {
    @Mapping(target = "denominacao", source = "nome")
    @Mapping(target = "motivo", source = "descricao")
    ZeeTProjInvestEntity toEntity(Projeto model);

    @Mapping(target = "nome", source = "denominacao")
    @Mapping(target = "descricao", source = "motivo")
    Projeto toModel(ZeeTProjInvestEntity entity);
}
