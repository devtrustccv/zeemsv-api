package cv.zeemsv.api.application.geografia.service;

import cv.zeemsv.api.application.geografia.dto.NacionalidadeResponseDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTGeografiaEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTGeografiaRepository;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GeografiaServiceImpl implements GeografiaService {
    private final ZeeTGeografiaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getIndicativos() {
        Map<String, String> indicativos = new LinkedHashMap<>();
        indicativos.put("", "-- Selecionar --");

        for (ZeeTGeografiaEntity geografia : repository.findByPaisIsNullOrderByNomeAsc()) {
            indicativos.put(geografia.getId(), "+" + geografia.getId() + " " + geografia.getNome());
        }

        return indicativos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NacionalidadeResponseDTO> getNacionalidades() {
        return repository.findByPaisIsNullOrderByNomeAsc().stream()
            .filter(geografia -> !"0".equals(geografia.getId()))
            .sorted(Comparator.comparing(ZeeTGeografiaEntity::getNome, String.CASE_INSENSITIVE_ORDER))
            .map(geografia -> new NacionalidadeResponseDTO(
                geografia.getId(),
                geografia.getNome(),
                nacionalidadeDescricao(geografia)
            ))
            .toList();
    }

    private String nacionalidadeDescricao(ZeeTGeografiaEntity geografia) {
        return geografia.getNacionalidade() != null
            ? geografia.getNacionalidade()
            : geografia.getNome();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getConcelhosCaboVerde() {
        Map<String, String> concelhos = new LinkedHashMap<>();
        concelhos.put("", "---Nacionalidade---");

        for (ZeeTGeografiaEntity geografia : repository.findByConcelhoIsNullAndPaisAndIlhaIsNotNullOrderByNomeAsc("238")) {
            concelhos.put(geografia.getId(), geografia.getNome());
        }

        return concelhos;
    }
}
