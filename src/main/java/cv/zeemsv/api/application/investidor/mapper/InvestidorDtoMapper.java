package cv.zeemsv.api.application.investidor.mapper;

import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.domain.investidor.model.Investidor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvestidorDtoMapper {
    Investidor toModel(InvestidorRequestDTO dto);
    InvestidorResponseDTO toResponse(Investidor model);
}
