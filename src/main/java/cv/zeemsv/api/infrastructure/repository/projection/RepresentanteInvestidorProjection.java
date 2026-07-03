package cv.zeemsv.api.infrastructure.repository.projection;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RepresentanteInvestidorProjection {
    Integer getId();
    Integer getIdInvestidor();
    Integer getIdSocioRepres();
    Integer getIdOrdem();
    String getDmTpRepresentante();
    Boolean getFlagRepresentante();
    Boolean getFlagSocio();
    String getDmPrincipal();
    String getDmEstado();
    LocalDate getDataRegisto();
    BigDecimal getUserRegisto();
    Integer getIdUser();
    String getNome();
    String getNacionalidade();
    String getNif();
    String getTipoDoc();
    String getNrDoc();
    BigDecimal getTelefone();
    BigDecimal getTelemovel();
    String getEmail();
    String getFotoUrl();
    String getFotoPath();
    String getIndicativoPais();
    String getEndereco();
}
