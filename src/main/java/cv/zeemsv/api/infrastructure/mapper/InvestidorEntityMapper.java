package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.investidor.model.Investidor;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InvestidorEntityMapper {
    @Mapping(target = "denominacao", source = "nome")
    @Mapping(target = "setor", source = "descricao")
    @Mapping(target = "dmEstado", source = "estado")
    ZeeTInvestidorEntity toEntity(Investidor model);

    @Mapping(target = "nome", source = "denominacao")
    @Mapping(target = "descricao", source = "setor")
    @Mapping(target = "estado", source = "dmEstado")
    Investidor toModel(ZeeTInvestidorEntity entity);
}
