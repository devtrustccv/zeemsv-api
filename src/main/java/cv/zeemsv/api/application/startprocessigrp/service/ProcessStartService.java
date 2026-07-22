package cv.zeemsv.api.application.startprocessigrp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cv.zeemsv.api.domain.external.business.ProcessStartException;
import cv.zeemsv.api.domain.external.model.StartProcessResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessStartService {

    private final ObjectMapper mapper;

    @Value("${zeemsv.gateway.base-url}")
    private String baseUrl;

    public StartProcessResponse start(String bearerOrNull, String rawJsonOrNull) {
        return start("correcao", bearerOrNull, rawJsonOrNull);
    }

    public StartProcessResponse start(String processKey, String bearerOrNull, String rawJsonOrNull) {
        try {
            final String url = buildUrl(processKey);
            final String bearer = resolveBearer(bearerOrNull);
            final String jsonBody = sanitizePayload(rawJsonOrNull);

            if (log.isDebugEnabled()) {
                log.debug("[ProcessStart] POST {} | bearer? {} | bodySize={}", url,
                    bearer != null && !bearer.isBlank(), jsonBody == null ? 0 : jsonBody.length());
            }

            final String jsonResponse = sendRequest(url, bearer, jsonBody);
            return parseResponse(jsonResponse);
        } catch (ProcessStartException e) {
            log.error("ProcessStartException: {}", e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrompida ao iniciar o processo. IGRP API nao respondeu.", e);
        } catch (IOException e) {
            throw new java.io.UncheckedIOException("Falha de I/O ao iniciar o processo. IGRP API nao respondeu.", e);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao iniciar o processo. IGRP API nao respondeu.", e);
        }
    }

    private String buildUrl(String processKey) {
        if (!StringUtils.hasText(baseUrl)) {
            throw new ProcessStartException("Base URL do gateway IGRP nao configurada.");
        }
        if (!StringUtils.hasText(processKey)) {
            throw new ProcessStartException("Chave do processo IGRP nao informada.");
        }
        return baseUrl.replaceAll("/+$", "") + "/services/process/start/" + processKey.trim();
    }

    private String resolveBearer(String bearerOrNull) {
        if (bearerOrNull == null || bearerOrNull.isBlank()) {
            return null;
        }
        final String bearer = bearerOrNull.trim();
        return bearer.regionMatches(true, 0, "Bearer ", 0, 7) ? bearer : "Bearer " + bearer;
    }

    private String sanitizePayload(String rawJsonOrNull) {
        if (rawJsonOrNull == null || rawJsonOrNull.isBlank()) {
            return null;
        }
        try {
            JsonNode node = mapper.readTree(rawJsonOrNull);
            return mapper.writeValueAsString(node);
        } catch (Exception e) {
            throw new IllegalArgumentException("JSON de entrada invalido.", e);
        }
    }

    private String sendRequest(String url, String bearer, String jsonBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/json");

        if (bearer != null && !bearer.isBlank()) {
            builder.header("Authorization", bearer);
        }
        if (jsonBody != null) {
            builder.header("Content-Type", "application/json");
        }

        HttpRequest request = jsonBody == null || jsonBody.isBlank()
            ? builder.POST(HttpRequest.BodyPublishers.noBody()).build()
            : builder.POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (log.isDebugEnabled()) {
            log.debug("[ProcessStart] status={} allow={}", response.statusCode(),
                response.headers().firstValue("Allow").orElse("<none>"));
        }
        if (response.statusCode() / 100 != 2) {
            String allow = response.headers().firstValue("Allow").orElse("");
            String message = "Erro ao iniciar processo. HTTP " + response.statusCode()
                + (allow.isBlank() ? "" : " (Allow: " + allow + ")")
                + ". Resposta: " + response.body();
            throw new ProcessStartException(message);
        }
        return Objects.toString(response.body(), "");
    }

    private StartProcessResponse parseResponse(String json) throws IOException {
        if (json == null || json.isBlank()) {
            return StartProcessResponse.builder().build();
        }
        JsonNode root = mapper.readTree(json);

        return StartProcessResponse.builder()
            .id(asText(root, "id"))
            .name(asText(root, "name"))
            .size(asInt(root, "size"))
            .total(asInt(root, "total"))
            .url(asText(root, "url"))
            .createTime(asOffsetDateTime(root, "createTime"))
            .executionId(asText(root, "executionId"))
            .executionUrl(asText(root, "executionUrl"))
            .formKey(asText(root, "formKey"))
            .priority(asInt(root, "priority"))
            .processDefinitionId(asText(root, "processDefinitionId"))
            .processDefinitionKey(asText(root, "processDefinitionKey"))
            .processDefinitionUrl(asText(root, "processDefinitionUrl"))
            .processInstanceId(asText(root, "processInstanceId"))
            .suspended(asBoolean(root, "suspended"))
            .taskDefinitionKey(asText(root, "taskDefinitionKey"))
            .tenantId(asText(root, "tenantId"))
            .variables(asList(root, "variables"))
            .processName(asText(root, "processName"))
            .build();
    }

    private static String asText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static Integer asInt(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isInt() || value.isLong()) {
            return value.asInt();
        }
        try {
            return Integer.parseInt(value.asText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Boolean asBoolean(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        if (value.isBoolean()) {
            return value.asBoolean();
        }
        String text = value.asText();
        return text != null && (text.equalsIgnoreCase("true") || text.equals("1"));
    }

    private static OffsetDateTime asOffsetDateTime(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull()) {
            return null;
        }
        String text = value.asText();
        if (text == null || text.isBlank()) {
            return null;
        }
        try {
            return ZonedDateTime.parse(text).toOffsetDateTime();
        } catch (DateTimeParseException e) {
            try {
                return OffsetDateTime.parse(text);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    private java.util.List<Object> asList(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull() || !value.isArray()) {
            return java.util.Collections.emptyList();
        }
        return mapper.convertValue(value, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<Object>>() {
        });
    }
}
