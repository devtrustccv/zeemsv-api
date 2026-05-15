package cv.zeemsv.api.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import cv.zeemsv.api.application.pessoa.dto.PesquisaNifResponseDTO;
import cv.zeemsv.api.application.pessoa.helper.PesquisaInvestidorHelper;
import cv.zeemsv.api.config.PesquisaNifProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Component
@RequiredArgsConstructor
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

        JsonNode response = restClientBuilder.build()
            .get()
            .uri(url)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, properties.getAuthorization())
            .retrieve()
            .body(JsonNode.class);

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
