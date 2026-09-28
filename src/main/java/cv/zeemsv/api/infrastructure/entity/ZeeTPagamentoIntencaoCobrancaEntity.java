package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_pagamento_intencao_cobranca", schema = "public")
@Getter
@Setter
public class ZeeTPagamentoIntencaoCobrancaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "id_intencao")
    private Integer idIntencao;

    @Column(name = "id_cobranca")
    private Integer idCobranca;

    @Column(name = "id_pagamento")
    private Integer idPagamento;

    @Column(name = "valor_cobranca")
    private BigDecimal valorCobranca;
}
