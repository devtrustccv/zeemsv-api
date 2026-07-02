package cv.zeemsv.api.application.servico.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServicoResponseDTO {
    private Integer id;
    private String nome;
    private String dmTipoSolicitacao;
    private String dmTipoSolicitacaoDesc;
    private String descricao;
    private String msgPedido;
    private BigDecimal prazoDia;
    private String flagObrigatorio;
    private String codigo;
    private String dmEstado;
    private String dmEstadoDesc;
    private Integer idEntExterna;
    private String possuiTaxa;
    private Boolean possuiOnboarding;
    private String entidadeDenominacao;
    private String entidadeSigla;
    private String entidadeDmTipoEnt;
    private String entidadeDmTipoEntDesc;
    private List<ServicoSolicitanteResponseDTO> quemDeveSolicitar;
    private List<ServicoOnboardingResponseDTO> tiposOnboarding;
}
