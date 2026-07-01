package cv.zeemsv.api.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import cv.zeemsv.api.application.pessoa.dto.PesquisaNifResponseDTO;
import cv.zeemsv.api.application.pessoa.helper.PesquisaInvestidorHelper;
import cv.zeemsv.api.config.PesquisaNifProperties;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Log4j2
public class PesquisaNifClient {
    private final RestClient.Builder restClientBuilder;
    private final PesquisaNifProperties properties;
    private final PesquisaInvestidorHelper pesquisaInvestidorHelper;

    public Optional<PesquisaNifResponseDTO> pesquisar(String nif) {
        if (!properties.isEnabled()
            || !StringUtils.hasText(properties.getUrl())
            || !StringUtils.hasText(properties.getAuthorization())
            || !StringUtils.hasText(nif)) {
            return Optional.empty();
        }

        String url = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
            .queryParam("NUM_NIF", nif)
            .toUriString();

        JsonNode response;
        try {
            response = restClientBuilder.build()
                .post()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, properties.getAuthorization())
                .retrieve()
                .body(JsonNode.class);
        } catch (HttpStatusCodeException ex) {
            log.warn("Pesquisa NIF falhou com status {} para NIF {}: {}", ex.getStatusCode(), nif, ex.getResponseBodyAsString());
            return Optional.empty();
        }

        JsonNode entry = extractEntry(response);
        if (entry == null || entry.isMissingNode() || entry.isNull()) {
            return Optional.empty();
        }

        return Optional.of(pesquisaInvestidorHelper.fromNif(entry));
    }

    private JsonNode extractEntry(JsonNode response) {
        if (response == null) {
            return null;
        }
        JsonNode entry = response.path("Entries").path("Entry");
        return entry.isArray() ? entry.path(0) : entry;
    }

}
