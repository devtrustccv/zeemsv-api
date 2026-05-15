package cv.zeemsv.api.application.pessoa.helper;

import com.fasterxml.jackson.databind.JsonNode;
import cv.zeemsv.api.application.pessoa.dto.PesquisaNifResponseDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;

@Component
public class PesquisaInvestidorHelper {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public PesquisaNifResponseDTO fromInvestidorLocal(ZeeTInvestidorEntity entity) {
        PesquisaNifResponseDTO dto = new PesquisaNifResponseDTO();
        dto.setNif(entity.getNif());
        dto.setNome(entity.getDenominacao());
        dto.setMatricula(entity.getMatricula());
        dto.setNaturezaJuridica(entity.getDmNaturezaJuridica());
        dto.setSetor(entity.getSetor());
        dto.setSede(entity.getSede());
        dto.setClassificacao(entity.getDmClassificacao());
        dto.setDataConstituicao(entity.getDataConstituicao() != null ? entity.getDataConstituicao().format(DATE_FORMATTER) : null);
        dto.setTelefone(entity.getPhone());
        dto.setIndicativoPais(entity.getIndicativoPais());
        dto.setTelemovel(entity.getTelemovel());
        dto.setEmail(entity.getEmail());
        dto.setSite(entity.getSite());
        dto.setEstado(entity.getDmEstado());
        dto.setLinkRegComercial(entity.getLinkRegComercial());
        dto.setFormaObrigar(entity.getFormaObrigar());
        dto.setCapitalSocial(entity.getCapitalSocial());
        dto.setPaisOrigem(entity.getPaisOrigem());
        dto.setEndereco(entity.getEndereco());
        dto.setFlagServico(entity.getFlagServico());
        dto.setIdioma(entity.getDmIdoma());
        dto.setTipoInvestidor(entity.getDmTipoInvestidor());
        dto.setGenero(entity.getDmGenero());
        dto.setDataNascimento(entity.getDataNascimento() != null ? entity.getDataNascimento().format(DATE_FORMATTER) : null);
        dto.setEstadoCivil(entity.getDmEstadoCivil());
        dto.setProfissao(entity.getProfissao());
        dto.setNrDocumento(entity.getNrDocumento());
        dto.setMoeda(entity.getMoeda());
        dto.setOrigem("LOCAL_INVESTIDOR");
        return dto;
    }

    public PesquisaNifResponseDTO fromSirc(JsonNode entry) {
        PesquisaNifResponseDTO dto = new PesquisaNifResponseDTO();
        dto.setNif(firstText(entry, "NIF", "NU_NIF", "NUM_NIF", "NUMERO_NIF"));
        dto.setNome(firstText(entry, "DENOMINACAO", "NM_CONTRIBUINTE", "NM_EMPRESA", "NOME", "NOME_EMPRESA"));
        dto.setMatricula(firstText(entry, "MATRICULA", "NUM_MATRICULA", "NR_MATRICULA"));
        dto.setNaturezaJuridica(firstText(entry, "NATUREZA_JURIDICA", "DM_NATUREZA_JURIDICA"));
        dto.setSetor(firstText(entry, "SETOR", "SECTOR"));
        dto.setSede(text(entry, "SEDE"));
        dto.setClassificacao(firstText(entry, "CLASSIFICACAO", "DM_CLASSIFICACAO"));
        dto.setDataConstituicao(firstText(entry, "DATA_CONSTITUICAO", "DT_CONSTITUICAO"));
        dto.setEmail(text(entry, "EMAIL"));
        dto.setSite(text(entry, "SITE"));
        dto.setEstado(firstText(entry, "ESTADO", "DM_ESTADO"));
        dto.setLinkRegComercial(firstText(entry, "LINK_REG_COMERCIAL", "LINK_REGISTO_COMERCIAL"));
        dto.setFormaObrigar(firstText(entry, "FORMA_OBRIGAR", "FORMA_DE_OBRIGAR"));
        dto.setPaisOrigem(firstText(entry, "PAIS_ORIGEM", "PAIS"));
        dto.setEndereco(firstText(entry, "ENDERECO", "MORADA"));
        dto.setOrigem("SIRC");
        return dto;
    }

    public PesquisaNifResponseDTO fromNif(JsonNode entry) {
        PesquisaNifResponseDTO dto = new PesquisaNifResponseDTO();
        dto.setNif(text(entry, "NU_NIF"));
        dto.setNome(text(entry, "NM_CONTRIBUINTE"));
        dto.setOrigem("NIF");
        return dto;
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

    private String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() || !StringUtils.hasText(value.asText()) ? null : value.asText();
    }
}
