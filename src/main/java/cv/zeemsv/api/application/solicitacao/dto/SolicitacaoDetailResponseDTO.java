package cv.zeemsv.api.application.solicitacao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SolicitacaoDetailResponseDTO {
    private SolicitacaoResponseDTO solicitacao;
    private ReciboPedidoDadosResponseDTO pedido;
    private List<SolicitacaoRequisitoResponseDTO> requisitos;
    private List<SolicitacaoDocResponseDTO> documentos;
    private List<SolicitacaoTaxaResponseDTO> taxas;
}
