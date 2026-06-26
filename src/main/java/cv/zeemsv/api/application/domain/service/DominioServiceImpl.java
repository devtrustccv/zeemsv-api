package cv.zeemsv.api.application.domain.service;

import cv.zeemsv.api.application.domain.dto.DominioResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValorResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValoresResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DominioServiceImpl implements DominioService {
    private static final String SQL_FIND_ALL = """
        select distinct dominio
        from tbl_domain
        where dominio is not null
        order by dominio
        """;

    private static final String SQL_FIND_VALORES_BY_DOMINIO = """
        select dominio, valor, description
        from tbl_domain
        where dominio = ?
        order by valor
        """;

    private static final String SQL_FIND_VALORES_BY_DOMINIOS = """
        select dominio, valor, description
        from tbl_domain
        where dominio in (%s)
        order by dominio, valor
        """;

    @Qualifier("igrpJdbcTemplate")
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<DominioResponseDTO> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, (rs, rowNum) -> {
            DominioResponseDTO dto = new DominioResponseDTO();
            dto.setDominio(rs.getString("dominio"));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<DominioValorResponseDTO> findValoresByDominio(String dominio) {
        return jdbcTemplate.query(SQL_FIND_VALORES_BY_DOMINIO, (rs, rowNum) -> {
            DominioValorResponseDTO dto = new DominioValorResponseDTO();
            dto.setDominio(rs.getString("dominio"));
            dto.setValor(rs.getString("valor"));
            dto.setDescription(rs.getString("description"));
            return dto;
        }, dominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DominioValoresResponseDTO> findValoresByDominios(List<String> dominios) {
        List<String> dominiosNormalizados = normalizarDominios(dominios);
        if (dominiosNormalizados.isEmpty()) {
            return List.of();
        }

        Map<String, List<DominioValorResponseDTO>> valoresPorDominio = new LinkedHashMap<>();
        dominiosNormalizados.forEach(dominio -> valoresPorDominio.put(dominio, new ArrayList<>()));

        String placeholders = dominiosNormalizados.stream()
            .map(dominio -> "?")
            .collect(Collectors.joining(", "));
        String sql = SQL_FIND_VALORES_BY_DOMINIOS.formatted(placeholders);

        jdbcTemplate.query(sql, rs -> {
            String dominio = rs.getString("dominio");
            List<DominioValorResponseDTO> valores = valoresPorDominio.get(dominio);
            if (valores == null) {
                return;
            }

            DominioValorResponseDTO dto = new DominioValorResponseDTO();
            dto.setDominio(dominio);
            dto.setValor(rs.getString("valor"));
            dto.setDescription(rs.getString("description"));
            valores.add(dto);
        }, dominiosNormalizados.toArray());

        return valoresPorDominio.entrySet().stream()
            .map(entry -> new DominioValoresResponseDTO(entry.getKey(), entry.getValue()))
            .toList();
    }

    private List<String> normalizarDominios(List<String> dominios) {
        if (dominios == null) {
            return List.of();
        }

        return dominios.stream()
            .filter(dominio -> dominio != null && !dominio.isBlank())
            .map(String::trim)
            .distinct()
            .toList();
    }
}
