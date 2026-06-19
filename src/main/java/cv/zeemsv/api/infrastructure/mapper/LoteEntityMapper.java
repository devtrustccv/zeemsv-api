package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.lote.model.Lote;
import cv.zeemsv.api.infrastructure.entity.ZeeTLoteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoteEntityMapper {
    @Mapping(target = "refLote", source = "nome")
    @Mapping(target = "nip", source = "descricao")
    ZeeTLoteEntity toEntity(Lote model);

    @Mapping(target = "nome", source = "refLote")
    @Mapping(target = "descricao", source = "nip")
    Lote toModel(ZeeTLoteEntity entity);
}
