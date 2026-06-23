package cv.zeemsv.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "zeemsv.integration.pesquisa-cni")
@Getter @Setter
public class PesquisaCniProperties {
    private boolean enabled = true;
    private String url;
    private String authorization;
    private String authorizationHeader = "Authorization";
    private String authorizationScheme = "Bearer";
}
