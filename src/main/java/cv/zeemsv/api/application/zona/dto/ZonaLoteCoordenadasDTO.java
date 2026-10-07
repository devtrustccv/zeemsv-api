package cv.zeemsv.api.application.zona.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZonaLoteCoordenadasDTO {
    private Integer id;
    private Integer idZona;
    private String zona;
    private Integer idPark;
    private String parque;
    private String refLote;
    private String refCm;
    private String nip;
    private BigDecimal area;
    private BigDecimal areaInicial;
    private String dmSituacaoCd;
    private String situacao;
    private String dmDisponibilidade;
    private String disponibilidade;
    private String dmFormalizacao;
    private String estado;
    private Boolean publicado;
    private Boolean validado;
    private BigDecimal valorComercial;
    private BigDecimal valorDireitoSuperficie;
    private JsonNode coordenadas;
}
