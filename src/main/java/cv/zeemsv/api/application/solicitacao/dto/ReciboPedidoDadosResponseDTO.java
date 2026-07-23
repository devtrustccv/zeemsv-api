package cv.zeemsv.api.application.solicitacao.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ReciboPedidoDadosResponseDTO {
    private Integer idPedido;
    private Integer idSolicitacao;
    private BigDecimal nrProcesso;
    private BigDecimal nrDoc;
    private String tipoProcesso;
    private String tipoSolicitacao;
    private String tipoSolicitacaoDescricao;
    private LocalDate dataEntrada;
    private String entidade;
    private String requerente;
    private String nif;
    private String email;
    private String endereco;
    private InstituicaoDTO instituicao;
    private List<DocumentoDTO> documentos;
    private List<RequisitoDTO> requisitos;

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class InstituicaoDTO {
        private String nome;
        private String nif;
        private String email;
        private String endereco;
        private String telefone;
        private String telemovel;
        private String codigoPostal;
        private String linkPortal;
        private String idLogo;
    }

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class DocumentoDTO {
        private String documento;
        private String codigo;
        private String obrigatorio;
        private Boolean entregue;
        private Boolean sim;
        private Boolean nao;
    }

    @Getter
    @Setter
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RequisitoDTO {
        private Integer idTpSolicTpDoc;
        private String requisito;
        private String obrigatorio;
        private Boolean cumprido;
        private Boolean sim;
        private Boolean nao;
    }
}
