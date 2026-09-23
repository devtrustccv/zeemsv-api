package cv.zeemsv.api.infrastructure.client;

import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayPaymentRequestDTO;
import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayPaymentResponseDTO;
import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayPaymentValidationRequestDTO;
import cv.zeemsv.api.config.PaymentGatewayProperties;
import cv.zeemsv.api.exceptions.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
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
public class PaymentGatewayPaymentClient {
    private static final String VALIDATION_STATUS_SUCCESS = "SUCCESS";
    private static final String VALIDATION_CHANNEL_CODE = "1008";

    private final RestClient.Builder restClientBuilder;
    private final PaymentGatewayProperties properties;
    private final PaymentGatewayAuthClient authClient;

    public PaymentGatewayPaymentResponseDTO createPayment(PaymentGatewayPaymentRequestDTO request) {
        validateConfiguration();
        validateRequest(request);

        String token = authClient.getAccessToken();

        try {
            PaymentGatewayPaymentResponseDTO response = restClientBuilder.build()
                .post()
                .uri(properties.getPaymentUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(toForm(request))
                .retrieve()
                .body(PaymentGatewayPaymentResponseDTO.class);

            validateResponse(response);
            return response;
        } catch (RestClientResponseException ex) {
            log.warn("Falha ao criar pagamento no gateway. HTTP: {}", ex.getStatusCode());
            throw new BusinessException("Falha ao criar pagamento no gateway.");
        } catch (RuntimeException ex) {
            if (ex instanceof BusinessException businessException) {
                throw businessException;
            }
            log.warn("Erro ao criar pagamento no gateway.", ex);
            throw new BusinessException("Erro ao criar pagamento no gateway.");
        }
    }

    public boolean validatePayment(String intentionId) {
        validateValidationConfiguration();
        requireText(intentionId, "intentionId");

        String token = authClient.getAccessToken();

        try {
            Boolean response = restClientBuilder.build()
                .post()
                .uri(properties.getPaymentValidateUrl())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(toValidationRequest(intentionId))
                .retrieve()
                .body(Boolean.class);

            return Boolean.TRUE.equals(response);
        } catch (RestClientResponseException ex) {
            log.warn("Falha ao validar pagamento no gateway. HTTP: {}", ex.getStatusCode());
            throw new BusinessException("Falha ao validar pagamento no gateway.");
        } catch (RuntimeException ex) {
            if (ex instanceof BusinessException businessException) {
                throw businessException;
            }
            log.warn("Erro ao validar pagamento no gateway.", ex);
            throw new BusinessException("Erro ao validar pagamento no gateway.");
        }
    }

    private MultiValueMap<String, String> toForm(PaymentGatewayPaymentRequestDTO request) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("channelCode", request.getChannelCode());
        form.add("transactionId", request.getTransactionId());
        form.add("total", request.getTotal().toPlainString());
        form.add("paymentType", request.getPaymentType());
        form.add("email", request.getEmail());
        form.add("billAddrCountry", request.getBillAddrCountry());
        form.add("billAddrCity", request.getBillAddrCity());
        form.add("billAddrLine1", request.getBillAddrLine1());
        form.add("billAddrPostCode", request.getBillAddrPostCode());
        return form;
    }

    private PaymentGatewayPaymentValidationRequestDTO toValidationRequest(String intentionId) {
        PaymentGatewayPaymentValidationRequestDTO request = new PaymentGatewayPaymentValidationRequestDTO();
        request.setTransactionId(intentionId);
        request.setStatus(VALIDATION_STATUS_SUCCESS);
        request.setChannelCode(VALIDATION_CHANNEL_CODE);
        request.setMerchantRespErrorDescription("");
        request.setMerchantRespMerchantRef("");
        request.setMerchantRespMerchantSession("");
        request.setFingerprint("");
        return request;
    }

    private void validateConfiguration() {
        if (!properties.isEnabled()) {
            throw new BusinessException("Integracao com gateway de pagamento desativada.");
        }
        if (!StringUtils.hasText(properties.getPaymentUrl())) {
            throw new BusinessException("URL de pagamento do gateway nao configurada.");
        }
    }

    private void validateValidationConfiguration() {
        if (!properties.isEnabled()) {
            throw new BusinessException("Integracao com gateway de pagamento desativada.");
        }
        if (!StringUtils.hasText(properties.getPaymentValidateUrl())) {
            throw new BusinessException("URL de validacao de pagamento do gateway nao configurada.");
        }
    }

    private void validateRequest(PaymentGatewayPaymentRequestDTO request) {
        if (request == null) {
            throw new BusinessException("Informe os dados do pagamento.");
        }
        requireText(request.getChannelCode(), "channelCode");
        requireText(request.getTransactionId(), "transactionId");
        if (request.getTotal() == null || request.getTotal().signum() <= 0) {
            throw new BusinessException("O total do pagamento deve ser maior que zero.");
        }
        requireText(request.getPaymentType(), "paymentType");
        requireText(request.getEmail(), "email");
        requireText(request.getBillAddrCountry(), "billAddrCountry");
        requireText(request.getBillAddrCity(), "billAddrCity");
        requireText(request.getBillAddrLine1(), "billAddrLine1");
        requireText(request.getBillAddrPostCode(), "billAddrPostCode");
    }

    private void validateResponse(PaymentGatewayPaymentResponseDTO response) {
        if (response == null) {
            throw new BusinessException("Gateway de pagamento retornou resposta vazia.");
        }
        requireText(response.getIntentionId(), "intentionId");
        requireText(response.getPaymentUrl(), "paymentUrl");
    }

    private void requireText(String value, String field) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Campo obrigatorio do pagamento nao informado: " + field);
        }
    }
}
