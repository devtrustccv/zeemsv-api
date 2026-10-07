package cv.zeemsv.api.application.zona.dto;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZonaCoordenadasResponseDTO {
    private Integer id;
    private String nome;
    private BigDecimal areaTotal;
    private BigDecimal areaTotalDisponivel;
    private BigDecimal areaTotalOcupada;
    private Long quantidadeLotesLivres;
    private Long quantidadeLotesOcupadas;
    private String sigla;
    private String estado;
    private String localGeogId;
    private String nomeNorm;
    private LocalDateTime dateCreate;
    private JsonNode coordenadas;
    private List<ZonaParqueDTO> parques;
    private List<ZonaCoordenadaPontoDTO> pontos;
    private List<ZonaLoteCoordenadasDTO> lotes;
}
