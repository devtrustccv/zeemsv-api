package cv.zeemsv.api.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import cv.zeemsv.api.application.pessoa.dto.PesquisaNifResponseDTO;
import cv.zeemsv.api.application.pessoa.helper.PesquisaInvestidorHelper;
import cv.zeemsv.api.config.PesquisaSircProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Log4j2
public class PesquisaSircClient {
    private final RestClient.Builder restClientBuilder;
    private final PesquisaSircProperties properties;
    private final PesquisaInvestidorHelper pesquisaInvestidorHelper;

    public Optional<PesquisaNifResponseDTO> pesquisar(String nif) {
        if (!properties.isEnabled()
            || !StringUtils.hasText(properties.getUrl())
            || !StringUtils.hasText(properties.getAuthorization())
            || !StringUtils.hasText(properties.getNifParam())
            || !StringUtils.hasText(nif)) {
            return Optional.empty();
        }

        JsonNode response;
        try {
            response = restClientBuilder.build()
                .post()
                .uri(UriComponentsBuilder.fromUriString(properties.getUrl())
                    .queryParam(properties.getNifParam(), nif)
                    .build(true)
                    .toUri())
                .header(HttpHeaders.AUTHORIZATION, properties.getAuthorization())
                .retrieve()
                .body(JsonNode.class);
        } catch (HttpStatusCodeException ex) {
            log.warn("Pesquisa SIRC falhou com status {} para NIF {}: {}", ex.getStatusCode(), nif, ex.getResponseBodyAsString());
            return Optional.empty();
        }

        JsonNode entry = extractEntry(response);
        if (entry == null || entry.isMissingNode() || entry.isNull()) {
            return Optional.empty();
        }

        PesquisaNifResponseDTO dto = pesquisaInvestidorHelper.fromSirc(entry);
        return StringUtils.hasText(dto.getNif()) || StringUtils.hasText(dto.getNome()) ? Optional.of(dto) : Optional.empty();
    }

    private JsonNode extractEntry(JsonNode response) {
        if (response == null) {
            return null;
        }
        JsonNode entry = response.path("Entries").path("Entry");
        if (entry.isMissingNode() || entry.isNull()) {
            entry = response.path("entries").path("entry");
        }
        if (entry.isMissingNode() || entry.isNull()) {
            entry = response.path("data");
        }
        return entry.isArray() ? entry.path(0) : entry;
    }
}
