package cv.zeemsv.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zeemsv.integration.pesquisa-sirc")
@Getter @Setter
public class PesquisaSircProperties {
    private boolean enabled = true;
    private String url;
    private String authorization;
    private String nifParam = "NUM_NIF";
}
