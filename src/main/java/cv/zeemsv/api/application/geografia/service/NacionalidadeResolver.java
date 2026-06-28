package cv.zeemsv.api.application.geografia.service;

import cv.zeemsv.api.infrastructure.entity.ZeeTGeografiaEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTGeografiaRepository;
import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class NacionalidadeResolver {
    private final ZeeTGeografiaRepository repository;
    private volatile Map<String, NacionalidadeInfo> index;

    public String resolveId(String nacionalidade) {
        NacionalidadeInfo info = resolve(nacionalidade);
        return info != null ? info.id() : null;
    }

    public String resolveDescricao(String nacionalidade) {
        NacionalidadeInfo info = resolve(nacionalidade);
        return info != null ? info.descricao() : nacionalidade;
    }

    private NacionalidadeInfo resolve(String nacionalidade) {
        if (!StringUtils.hasText(nacionalidade)) {
            return null;
        }

        return getIndex().get(normalize(nacionalidade));
    }

    private Map<String, NacionalidadeInfo> getIndex() {
        Map<String, NacionalidadeInfo> currentIndex = index;
        if (currentIndex == null) {
            synchronized (this) {
                currentIndex = index;
                if (currentIndex == null) {
                    currentIndex = buildIndex();
                    index = currentIndex;
                }
            }
        }

        return currentIndex;
    }

    private Map<String, NacionalidadeInfo> buildIndex() {
        Map<String, NacionalidadeInfo> values = new HashMap<>();
        for (ZeeTGeografiaEntity geografia : repository.findByPaisIsNullOrderByNomeAsc()) {
            NacionalidadeInfo info = new NacionalidadeInfo(geografia.getId(), descricao(geografia));
            add(values, geografia.getId(), info);
            add(values, geografia.getNome(), info);
            add(values, geografia.getNacionalidade(), info);
        }
        return Map.copyOf(values);
    }

    private void add(Map<String, NacionalidadeInfo> values, String key, NacionalidadeInfo info) {
        if (StringUtils.hasText(key) && StringUtils.hasText(info.id())) {
            values.putIfAbsent(normalize(key), info);
        }
    }

    private String descricao(ZeeTGeografiaEntity geografia) {
        return StringUtils.hasText(geografia.getNacionalidade())
            ? geografia.getNacionalidade()
            : geografia.getNome();
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }

        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toUpperCase();
    }

    private record NacionalidadeInfo(String id, String descricao) {
    }
}
