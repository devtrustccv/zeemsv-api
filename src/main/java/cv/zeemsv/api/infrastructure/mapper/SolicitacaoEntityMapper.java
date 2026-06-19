package cv.zeemsv.api.infrastructure.mapper;

import cv.zeemsv.api.domain.solicitacao.model.Solicitacao;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SolicitacaoEntityMapper {
    @Mapping(target = "descSolic", source = "nome")
    @Mapping(target = "exposicao", source = "descricao")
    @Mapping(target = "dmEstadoProc", source = "estado")
    ZeeTSolicitacaoEntity toEntity(Solicitacao model);

    @Mapping(target = "nome", source = "descSolic")
    @Mapping(target = "descricao", source = "exposicao")
    @Mapping(target = "estado", source = "dmEstadoProc")
    Solicitacao toModel(ZeeTSolicitacaoEntity entity);
}
