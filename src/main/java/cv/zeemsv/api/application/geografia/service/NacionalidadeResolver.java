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
    private volatile Map<String, String> index;

    public String resolveId(String nacionalidade) {
        if (!StringUtils.hasText(nacionalidade)) {
            return null;
        }

        return getIndex().get(normalize(nacionalidade));
    }

    private Map<String, String> getIndex() {
        Map<String, String> currentIndex = index;
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

    private Map<String, String> buildIndex() {
        Map<String, String> values = new HashMap<>();
        for (ZeeTGeografiaEntity geografia : repository.findByPaisIsNullOrderByNomeAsc()) {
            add(values, geografia.getId(), geografia.getId());
            add(values, geografia.getNome(), geografia.getId());
            add(values, geografia.getNacionalidade(), geografia.getId());
        }
        return Map.copyOf(values);
    }

    private void add(Map<String, String> values, String key, String id) {
        if (StringUtils.hasText(key) && StringUtils.hasText(id)) {
            values.putIfAbsent(normalize(key), id);
        }
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return "";
        }

        String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").toUpperCase();
    }
}
