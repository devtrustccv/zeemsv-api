package cv.zeemsv.api.infrastructure.client;

import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayTokenResponseDTO;
import cv.zeemsv.api.config.PaymentGatewayProperties;
import cv.zeemsv.api.exceptions.BusinessException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
@Log4j2
public class PaymentGatewayAuthClient {
    private final RestClient.Builder restClientBuilder;
    private final PaymentGatewayProperties properties;
    private volatile CachedToken cachedToken;

    public String getAccessToken() {
        CachedToken current = cachedToken;
        if (current != null && current.isValid()) {
            return current.accessToken();
        }

        synchronized (this) {
            current = cachedToken;
            if (current != null && current.isValid()) {
                return current.accessToken();
            }
            PaymentGatewayTokenResponseDTO response = requestToken();
            cachedToken = CachedToken.from(response, properties.getTokenExpirySkewSeconds());
            return cachedToken.accessToken();
        }
    }

    public PaymentGatewayTokenResponseDTO requestToken() {
        validateConfiguration();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", properties.getGrantType());
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());

        try {
            PaymentGatewayTokenResponseDTO response = restClientBuilder.build()
                .post()
                .uri(properties.getAuthTokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .body(form)
                .retrieve()
                .body(PaymentGatewayTokenResponseDTO.class);

            if (response == null || !StringUtils.hasText(response.getAccessToken())) {
                throw new BusinessException("Gateway de pagamento retornou token vazio.");
            }
            return response;
        } catch (RestClientResponseException ex) {
            log.warn("Falha ao autenticar no gateway de pagamento. HTTP: {}", ex.getStatusCode());
            throw new BusinessException("Falha ao autenticar no gateway de pagamento.");
        } catch (RuntimeException ex) {
            if (ex instanceof BusinessException businessException) {
                throw businessException;
            }
            log.warn("Erro ao autenticar no gateway de pagamento.", ex);
            throw new BusinessException("Erro ao autenticar no gateway de pagamento.");
        }
    }

    private void validateConfiguration() {
        if (!properties.isEnabled()) {
            throw new BusinessException("Integracao com gateway de pagamento desativada.");
        }
        if (!StringUtils.hasText(properties.getAuthTokenUrl())) {
            throw new BusinessException("URL de autenticacao do gateway de pagamento nao configurada.");
        }
        if (!StringUtils.hasText(properties.getGrantType())) {
            throw new BusinessException("Grant type do gateway de pagamento nao configurado.");
        }
        if (!StringUtils.hasText(properties.getClientId())) {
            throw new BusinessException("Client ID do gateway de pagamento nao configurado.");
        }
        if (!StringUtils.hasText(properties.getClientSecret())) {
            throw new BusinessException("Client secret do gateway de pagamento nao configurado.");
        }
    }

    private record CachedToken(String accessToken, Instant expiresAt) {
        private boolean isValid() {
            return StringUtils.hasText(accessToken) && Instant.now().isBefore(expiresAt);
        }

        private static CachedToken from(PaymentGatewayTokenResponseDTO response, long skewSeconds) {
            long expiresIn = response.getExpiresIn() != null && response.getExpiresIn() > 0
                ? response.getExpiresIn()
                : 300L;
            long safeTtl = Math.max(1L, expiresIn - Math.max(0L, skewSeconds));
            return new CachedToken(response.getAccessToken(), Instant.now().plusSeconds(safeTtl));
        }
    }
}
