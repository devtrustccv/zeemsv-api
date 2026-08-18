package cv.zeemsv.api.application.domain.dto;

import cv.zeemsv.api.application.servico.dto.ServicoSolicitanteResponseDTO;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaServicoResponseDTO {
    private String dominio;
    private String valor;
    private String description;
    private List<ServicoSolicitanteResponseDTO> relacoes;
}
