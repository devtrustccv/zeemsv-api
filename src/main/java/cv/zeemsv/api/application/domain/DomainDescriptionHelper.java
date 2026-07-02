package cv.zeemsv.api.application.domain;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class DomainDescriptionHelper {
    public static final String ESTADO = "ESTADO";
    public static final String ESTADO_ATIVIDADE = "ESTADO_ATIVIDADE";
    public static final String ESTADO_INSTALACAO = "ESTADO_INSTALACAO";
    public static final String ESTADO_PEDIDO = "ESTADO_PEDIDO";
    public static final String ESTADO_PROC_SOLICIT = "ESTADO_PROC_SOLICIT";
    public static final String ESTADO_PROCESO = "ESTADO_PROCESO";
    public static final String ESTADO_PROJETO = "ESTADO_PROJETO";
    public static final String ESTADO_CIVIL = "ESTADO_CIVIL";
    public static final String FORMA_COMERCIALIZACAO = "FORMA_COMERCIALIZACAO";
    public static final String GENERO = "GENERO";
    public static final String IDIOMA = "IDIOMA";
    public static final String MOEDA = "MOEDA";
    public static final String NATUREZA_JURIDICA = "NATUREZA_JURIDICA";
    public static final String ORIGEM = "ORIGEM";
    public static final String PRIORIDADE_TAREFA = "PRIORIDADE_TAREFA";
    public static final String PRODUTO_SERVICO = "PRODUTO_SERVICO";
    public static final String REGIME = "REGIME";
    public static final String SIM_NAO = "SIM_NAO";
    public static final String SITUACAO_LOTE = "SITUACAO_LOTE";
    public static final String SITUACAO_PROJ = "SITUACAO_PROJ";
    public static final String TIPO_ATIVIDADE = "TIPO_ATIVIDADE";
    public static final String TIPO_AGENDAMENTO = "TIPO_AGENDAMENTO";
    public static final String TIPO_CHAMADA = "TIPO_CHAMADA";
    public static final String TIPO_DOCUMENTO = "TIPO_DOCUMENTO";
    public static final String TIPO_ENTIDADE = "TIPO_ENTIDADE";
    public static final String TIPO_ORDEM = "TIPO_ORDEM";
    public static final String TIPO_PEDIDO_ACESSO = "TIPO_PEDIDO_ACESSO";
    public static final String TIPO_REPRESENTANTE = "TIPO_REPRESENTANTE";
    public static final String TIPO_SOLICITACAO = "TIPO_SOLICITACAO";
    public static final String TIPO_TAXA = "TIPO_TAXA";
    public static final String TIPO_INVESTIDOR = "TIPO_INVESTIDOR";
    public static final String CLASSIFICACAO = "CLASSIFICACAO";
    public static final String RESULTADO_CHAMADA = "RESULTADO_CHAMADA";

    private static final String SQL = """
        select description
        from tbl_domain
        where dominio = ? and valor = ?
        limit 1
        """;

    @Qualifier("igrpJdbcTemplate")
    private final JdbcTemplate jdbcTemplate;
    private final ConcurrentMap<String, Optional<String>> cache = new ConcurrentHashMap<>();

    public String describe(String dominio, String valor) {
        if (!StringUtils.hasText(dominio) || !StringUtils.hasText(valor)) {
            return null;
        }

        String normalizedDominio = dominio.trim();
        String normalizedValor = valor.trim();
        String key = normalizedDominio + "|" + normalizedValor;

        return cache.computeIfAbsent(key, ignored -> findDescription(normalizedDominio, normalizedValor)).orElse(null);
    }

    public String describeBoolean(String dominio, Boolean valor) {
        if (valor == null) {
            return null;
        }
        return describe(dominio, valor ? "SIM" : "NAO");
    }

    private Optional<String> findDescription(String dominio, String valor) {
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(SQL, String.class, dominio, valor));
        } catch (DataAccessException ex) {
            return Optional.empty();
        }
    }
}
