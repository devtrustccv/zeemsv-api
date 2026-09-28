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
@Table(name = "zee_t_pagamento_intencao", schema = "public")
@Getter
@Setter
public class ZeeTPagamentoIntencaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "intention_id")
    private String intentionId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "link_payment")
    private String linkPayment;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "dm_estado")
    private String dmEstado;

    @Column(name = "channel_code")
    private String channelCode;

    @Column(name = "merchant_resp_error_description")
    private String merchantRespErrorDescription;

    @Column(name = "merchant_resp_merchant_ref")
    private String merchantRespMerchantRef;

    @Column(name = "merchant_resp_merchant_session")
    private String merchantRespMerchantSession;

    @Column(name = "fingerprint")
    private String fingerprint;

    @Column(name = "user_registo")
    private String userRegisto;

    @Column(name = "data_registo")
    private LocalDate dataRegisto;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
}
