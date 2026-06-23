package cv.zeemsv.api.infrastructure.client;

import com.fasterxml.jackson.databind.JsonNode;
import cv.zeemsv.api.application.pessoa.dto.PessoaPesquisaResponseDTO;
import cv.zeemsv.api.config.PesquisaCniProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PesquisaCniClient {
    private final RestClient.Builder restClientBuilder;
    private final PesquisaCniProperties properties;

    public List<PessoaPesquisaResponseDTO> pesquisar(String nrDocumento, String nomeCompleto) {
        if (!properties.isEnabled() || !StringUtils.hasText(properties.getUrl()) || !StringUtils.hasText(properties.getAuthorization())) {
            return List.of();
        }

        String url = UriComponentsBuilder.fromHttpUrl(properties.getUrl())
            .queryParamIfPresent("P_NIC", StringUtils.hasText(nrDocumento) ? Optional.of(nrDocumento) : Optional.empty())
            .queryParamIfPresent("P_NOME_COMPLETO", StringUtils.hasText(nomeCompleto) ? Optional.of(nomeCompleto) : Optional.empty())
            .toUriString();

        RestClient.RequestHeadersSpec<?> request = restClientBuilder.build()
            .post()
            .uri(url)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .body("{}");

        request = request.header(properties.getAuthorizationHeader(), authorizationValue());

        JsonNode response = request.retrieve().body(JsonNode.class);
        if (response == null) {
            return List.of();
        }

        JsonNode entries = response.path("Entries").path("Entry");
        if (entries.isMissingNode() || entries.isNull()) {
            return List.of();
        }

        List<PessoaPesquisaResponseDTO> pessoas = new ArrayList<>();
        if (entries.isArray()) {
            entries.forEach(entry -> pessoas.add(toPessoa(entry)));
        } else if (entries.isObject()) {
            pessoas.add(toPessoa(entries));
        }
        return pessoas;
    }

    private PessoaPesquisaResponseDTO toPessoa(JsonNode entry) {
        PessoaPesquisaResponseDTO pessoa = new PessoaPesquisaResponseDTO();
        pessoa.setNome(firstText(entry, "NOME", "NOME_COMPLETO"));
        pessoa.setNrDocumento(text(entry, "NUM_DOCUMENTO"));
        pessoa.setTipoDocumento(text(entry, "id_tp_doc"));
        pessoa.setTpDoc(StringUtils.hasText(pessoa.getTipoDocumento()) ? pessoa.getTipoDocumento() : "BI");
        pessoa.setDataNascimento(firstText(entry, "DT_NASC", "DATA_NASC"));
        pessoa.setDataEmissao(text(entry, "DT_EMISSAO"));
        pessoa.setDataValidade(text(entry, "DT_VALIDADE"));
        pessoa.setNacionalidade(firstText(entry, "NACIONALIDADE_ID", "NATURALIDADE_ID"));
        pessoa.setTelemovel(text(entry, "TELEMOVEL"));
        pessoa.setEstadoCivil(text(entry, "ESTADO_CIVIL"));
        pessoa.setGenero(text(entry, "SEXO"));
        pessoa.setEmail(text(entry, "EMAIL"));
        pessoa.setResidencia(text(entry, "MORADA"));
        pessoa.setNomeMae(firstTextOrValue(entry, "NOME_MAE", joinText(entry, "NOME_MAE_PROPRIO", "NOME_MAE_APELIDO")));
        pessoa.setNomePai(firstTextOrValue(entry, "NOME_PAI", joinText(entry, "NOME_PAI_PROPRIO", "NOME_PAI_APELIDO")));
        pessoa.setEmissor(firstText(entry, "EMISSOR", "EMISSOR_DOC", "EMISSOR_DESCRICAO"));
        pessoa.setOrigem("CNI");
        return pessoa;
    }

    private String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = text(node, field);
            if (StringUtils.hasText(value)) {
                return value;
            }
        }
        return null;
    }

    private String firstTextOrValue(JsonNode node, String firstField, String fallbackValue) {
        String value = text(node, firstField);
        return StringUtils.hasText(value) ? value : fallbackValue;
    }

    private String joinText(JsonNode node, String... fields) {
        List<String> values = new ArrayList<>();
        for (String field : fields) {
            String value = text(node, field);
            if (StringUtils.hasText(value)) {
                values.add(value);
            }
        }
        return values.isEmpty() ? null : String.join(" ", values);
    }

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (value.isMissingNode() || value.isNull() || isNilNode(value)) {
            return null;
        }

        String text = value.asText();
        return StringUtils.hasText(text) ? text : null;
    }

    private boolean isNilNode(JsonNode value) {
        return value.isObject() && "true".equalsIgnoreCase(value.path("@nil").asText());
    }

    private String authorizationValue() {
        String authorization = properties.getAuthorization().trim();
        if (authorization.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())
            || authorization.regionMatches(true, 0, "Basic ", 0, "Basic ".length())) {
            return authorization;
        }
        if ("apikey".equalsIgnoreCase(properties.getAuthorizationHeader())) {
            return authorization;
        }
        return properties.getAuthorizationScheme() + " " + authorization;
    }
}
