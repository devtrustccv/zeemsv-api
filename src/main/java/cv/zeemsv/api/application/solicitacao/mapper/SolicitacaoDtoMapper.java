package cv.zeemsv.api.application.solicitacao.mapper;

import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.domain.solicitacao.model.Solicitacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitacaoDtoMapper {
    Solicitacao toModel(SolicitacaoRequestDTO dto);
    SolicitacaoResponseDTO toResponse(Solicitacao model);
}
