package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_user", schema = "public")
@Getter @Setter
public class ZeeTUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "dm_estado", nullable = false)
    private String dmEstado;

    @Column(name = "origem", nullable = false)
    private String origem;

    @Column(name = "onboarding_realizado")
    private Boolean onboardingRealizado;

    @Column(name = "data_onboarding")
    private LocalDate dataOnboarding;

    @Column(name = "data_registo", nullable = false)
    private LocalDate dataRegisto;

    @Column(name = "pessoa_id")
    private Integer pessoaId;

    @Column(name = "sub_cmdcv")
    private String subCmdcv;

    @Column(name = "password_hash")
    private String passwordHash;

}
