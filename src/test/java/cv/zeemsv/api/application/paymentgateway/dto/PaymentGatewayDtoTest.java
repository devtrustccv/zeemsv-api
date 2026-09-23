package cv.zeemsv.api.application.paymentgateway.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PaymentGatewayDtoTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void mapsTokenResponseFieldsFromGatewayPayload() throws Exception {
        String json = """
            {
              "access_token": "token-value",
              "expires_in": 300,
              "refresh_expires_in": 0,
              "token_type": "Bearer",
              "not-before-policy": 0,
              "scope": "email profile"
            }
            """;

        PaymentGatewayTokenResponseDTO dto = mapper.readValue(json, PaymentGatewayTokenResponseDTO.class);

        assertEquals("token-value", dto.getAccessToken());
        assertEquals(300L, dto.getExpiresIn());
        assertEquals(0L, dto.getRefreshExpiresIn());
        assertEquals("Bearer", dto.getTokenType());
        assertEquals(0L, dto.getNotBeforePolicy());
        assertEquals("email profile", dto.getScope());
    }

    @Test
    void mapsPaymentResponseFieldsFromGatewayPayload() throws Exception {
        String json = """
            {
              "intentionId": "20250917095430421095e",
              "paymentUrl": "https://paymentgateway.uniteltmais.cv:8441/payment/intention/20250917095430421095e"
            }
            """;

        PaymentGatewayPaymentResponseDTO dto = mapper.readValue(json, PaymentGatewayPaymentResponseDTO.class);

        assertEquals("20250917095430421095e", dto.getIntentionId());
        assertEquals(
            "https://paymentgateway.uniteltmais.cv:8441/payment/intention/20250917095430421095e",
            dto.getPaymentUrl()
        );
    }

    @Test
    void validatesPaymentRequestRequiredFields() {
        PaymentGatewayPaymentRequestDTO dto = new PaymentGatewayPaymentRequestDTO();
        dto.setTotal(BigDecimal.ZERO);
        dto.setEmail("invalid-email");

        assertFalse(validator.validate(dto).isEmpty());
    }
}
