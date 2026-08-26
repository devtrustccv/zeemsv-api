package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_pagamento_taxa", schema = "public")
@Getter @Setter
public class ZeeTPagamentoTaxaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_pagamento")
    private Integer idPagamento;

    @Column(name = "id_taxa")
    private Integer idTaxa;

    @Column(name = "id_taxa_cond")
    private Integer idTaxaCond;

    @Column(name = "valor")
    private BigDecimal valor;

    @Column(name = "dm_estado")
    private String dmEstado;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;
}
