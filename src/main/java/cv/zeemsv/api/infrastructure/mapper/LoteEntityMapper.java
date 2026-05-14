package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.lote.model.Lote;
import cv.zeemsv.api.infrastructure.entity.ZeeTLoteEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoteEntityMapper {
    ZeeTLoteEntity toEntity(Lote model);
    Lote toModel(ZeeTLoteEntity entity);
}
