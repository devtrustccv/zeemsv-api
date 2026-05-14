package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.investidor.model.Investidor;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvestidorEntityMapper {
    ZeeTInvestidorEntity toEntity(Investidor model);
    Investidor toModel(ZeeTInvestidorEntity entity);
}
