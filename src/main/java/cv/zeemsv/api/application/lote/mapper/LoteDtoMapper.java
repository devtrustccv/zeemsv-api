package cv.zeemsv.api.application.lote.mapper;

import cv.zeemsv.api.application.lote.dto.LoteRequestDTO;
import cv.zeemsv.api.application.lote.dto.LoteResponseDTO;
import cv.zeemsv.api.domain.lote.model.Lote;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoteDtoMapper {
    Lote toModel(LoteRequestDTO dto);
    LoteResponseDTO toResponse(Lote model);
}
