package cv.zeemsv.api.application.domain.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.domain.dto.CategoriaServicoResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValorResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValoresResponseDTO;
import cv.zeemsv.api.application.servico.dto.ServicoSolicitanteResponseDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicitacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final ZeeTTpSolicitacaoRepository tpSolicitacaoRepository;
    private final ZeeTTpSolicRelacaoRepository tpSolicRelacaoRepository;
    private final DomainDescriptionHelper domainHelper;

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

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaServicoResponseDTO> findCategoriasServico() {
        List<DominioValorResponseDTO> categorias = findValoresByDominio(DomainDescriptionHelper.CATEGORIA_SERVICO);
        if (categorias.isEmpty()) {
            return List.of();
        }

        List<String> valoresCategoria = categorias.stream()
            .map(DominioValorResponseDTO::getValor)
            .filter(valor -> valor != null && !valor.isBlank())
            .distinct()
            .toList();

        Map<String, List<ServicoSolicitanteResponseDTO>> relacoesPorCategoria = findRelacoesPorCategoria(valoresCategoria);

        return categorias.stream()
            .map(categoria -> {
                CategoriaServicoResponseDTO dto = new CategoriaServicoResponseDTO();
                dto.setDominio(categoria.getDominio());
                dto.setValor(categoria.getValor());
                dto.setDescription(categoria.getDescription());
                dto.setRelacoes(relacoesPorCategoria.getOrDefault(categoria.getValor(), Collections.emptyList()));
                return dto;
            })
            .toList();
    }

    private Map<String, List<ServicoSolicitanteResponseDTO>> findRelacoesPorCategoria(List<String> valoresCategoria) {
        if (valoresCategoria.isEmpty()) {
            return Collections.emptyMap();
        }

        List<ZeeTTpSolicitacaoEntity> servicos = tpSolicitacaoRepository.findByDmCategoriaIn(valoresCategoria);
        if (servicos.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Integer, String> categoriaPorServico = servicos.stream()
            .filter(servico -> servico.getId() != null && servico.getDmCategoria() != null)
            .collect(Collectors.toMap(ZeeTTpSolicitacaoEntity::getId, ZeeTTpSolicitacaoEntity::getDmCategoria, (left, right) -> left));

        List<Integer> idsTpSolic = new ArrayList<>(categoriaPorServico.keySet());
        List<ZeeTTpSolicRelacaoEntity> relacoes = tpSolicRelacaoRepository.findByIdTpSolicIn(idsTpSolic);
        Map<String, Map<String, ServicoSolicitanteResponseDTO>> relacoesPorCategoria = new HashMap<>();

        relacoes.stream()
            .filter(relacao -> relacao.getIdTpSolic() != null && relacao.getDmObjecto() != null)
            .sorted(Comparator.comparing(ZeeTTpSolicRelacaoEntity::getDmObjecto))
            .forEach(relacao -> {
                String categoria = categoriaPorServico.get(relacao.getIdTpSolic());
                if (categoria == null) {
                    return;
                }
                relacoesPorCategoria
                    .computeIfAbsent(categoria, ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(relacao.getDmObjecto(), this::toSolicitanteResponse);
            });

        return relacoesPorCategoria.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> entry.getValue().values().stream()
                    .filter(Objects::nonNull)
                    .toList()
            ));
    }

    private ServicoSolicitanteResponseDTO toSolicitanteResponse(String dmObjecto) {
        ServicoSolicitanteResponseDTO dto = new ServicoSolicitanteResponseDTO();
        dto.setDmObjecto(dmObjecto);
        dto.setDmObjectoDesc(domainHelper.describe(DomainDescriptionHelper.OBJECTO, dmObjecto));
        return dto;
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
