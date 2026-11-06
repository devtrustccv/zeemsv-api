package cv.zeemsv.api.application.cobranca.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ComprovativoPagamentoRequestDTO {
    private Integer idPrestacao;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valor;
    @NotNull
    @PastOrPresent
    private LocalDate dataPagamento;
    @NotBlank
    private String formaPagamento;
    @NotBlank
    private String user;
    private String referencia;
    private String banco;
}
