package cv.zeemsv.api.application.solicitacao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SolicitacaoDocumentosRequisitosResponseDTO {
    private List<SolicitacaoDocResponseDTO> documentos;
    private List<SolicitacaoRequisitoResponseDTO> requisitos;
    private List<SolicitacaoTaxaResponseDTO> taxas;
    private Boolean instantPagamento;
    private BigDecimal totalAPagar;
}
