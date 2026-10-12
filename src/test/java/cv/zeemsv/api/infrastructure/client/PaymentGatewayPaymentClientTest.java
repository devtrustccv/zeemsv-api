package cv.zeemsv.api.infrastructure.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayPaymentValidationRequestDTO;
import org.junit.jupiter.api.Test;

class PaymentGatewayPaymentClientTest {
    @Test
    void serializesValidationRequestBodyInCamelCaseEvenWhenGlobalMapperUsesSnakeCase() throws Exception {
        ObjectMapper mapper = new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        PaymentGatewayPaymentValidationRequestDTO request = new PaymentGatewayPaymentValidationRequestDTO();
        request.setTransactionId("tx-123");
        request.setStatus("1");
        request.setChannelCode("1052");
        request.setMerchantRespErrorDescription("");
        request.setMerchantRespMerchantRef("ref-123");
        request.setMerchantRespMerchantSession("session-123");
        request.setFingerprint("fingerprint");

        String json = PaymentGatewayPaymentClient.toValidationRequestBody(mapper, request);

        assertEquals(
            "{\"transactionId\":\"tx-123\",\"status\":\"1\",\"channelCode\":\"1052\",\"merchantRespErrorDescription\":\"\",\"merchantRespMerchantRef\":\"ref-123\",\"merchantRespMerchantSession\":\"session-123\",\"fingerprint\":\"fingerprint\"}",
            json
        );
    }
}
