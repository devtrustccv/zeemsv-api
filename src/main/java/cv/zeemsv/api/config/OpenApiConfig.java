package cv.zeemsv.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class OpenApiConfig {
    @Value("${application.openapi.server-url:}")
    private String serverUrl;

    @Value("${application.openapi.force-https-hosts:}")
    private String forceHttpsHosts;

    @Bean
    public OpenAPI zeemsvOpenAPI() {
        OpenAPI openAPI = new OpenAPI()
            .info(new Info().title("ZEEMSV API").version("v1").description("API para gestao ZEEMSV."));

        if (serverUrl != null && !serverUrl.isBlank()) {
            openAPI.addServersItem(new Server().url(serverUrl));
        }

        return openAPI;
    }

    @Bean
    public ServerBaseUrlCustomizer serverBaseUrlCustomizer() {
        return (serverBaseUrl, request) -> {
            if (serverUrl != null && !serverUrl.isBlank()) {
                return serverUrl;
            }

            if (serverBaseUrl == null || serverBaseUrl.isBlank()) {
                return serverBaseUrl;
            }

            URI uri;
            try {
                uri = URI.create(serverBaseUrl);
            } catch (IllegalArgumentException ex) {
                return serverBaseUrl;
            }

            String host = uri.getHost();
            if (!"http".equalsIgnoreCase(uri.getScheme()) || host == null || !forceHttpsHosts().contains(host)) {
                return serverBaseUrl;
            }

            return UriComponentsBuilder.fromUri(uri)
                .scheme("https")
                .port(-1)
                .toUriString();
        };
    }

    private Set<String> forceHttpsHosts() {
        return Arrays.stream(forceHttpsHosts.split(","))
            .map(String::trim)
            .filter(host -> !host.isBlank())
            .collect(Collectors.toSet());
    }
}
