package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.solicitacao.model.Solicitacao;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitacaoEntityMapper {
    ZeeTSolicitacaoEntity toEntity(Solicitacao model);
    Solicitacao toModel(ZeeTSolicitacaoEntity entity);
}
