package cv.zeemsv.api.application.ordem.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrdemResponseDTO {
    private Integer id;
    private String tipoOrdem;
    private String nome;
    private String cedula;
    private String concelho;
    private String endereco;
    private String email;
    private String indicativoPais;
    private BigDecimal telemovel;
    private BigDecimal nif;
    private String nrDocumento;
    private String nacionalidade;
    private BigDecimal numeroInscricao;
    private String especialidade;
    private String dmEstado;
    private String dmEstadoDesc;
    private LocalDate dataRegisto;
    private String userRegisto;
    private String dmTpDoc;
    private String dmTpDocDesc;
    private String tipoOrdemDesc;
}
