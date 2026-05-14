package cv.zeemsv.api.application.investidor.mapper;

import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorUserResponseDTO;
import cv.zeemsv.api.domain.investidor.model.Investidor;
import cv.zeemsv.api.domain.investidor.model.InvestidorUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvestidorDtoMapper {
    Investidor toModel(InvestidorRequestDTO dto);
    InvestidorResponseDTO toResponse(Investidor model);
    InvestidorUserResponseDTO toUserResponse(InvestidorUser model);
}
