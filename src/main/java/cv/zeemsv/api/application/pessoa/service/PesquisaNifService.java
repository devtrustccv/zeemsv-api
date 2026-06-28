package cv.zeemsv.api.application.pessoa.service;

import cv.zeemsv.api.application.pessoa.dto.PesquisaNifResponseDTO;
import cv.zeemsv.api.application.pessoa.helper.PesquisaInvestidorHelper;
import cv.zeemsv.api.infrastructure.client.PesquisaNifClient;
import cv.zeemsv.api.infrastructure.client.PesquisaSircClient;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PesquisaNifService {
    private final ZeeTInvestidorRepository investidorRepository;
    private final PesquisaInvestidorHelper pesquisaInvestidorHelper;
    private final PesquisaSircClient pesquisaSircClient;
    private final PesquisaNifClient pesquisaNifClient;

    public Optional<PesquisaNifResponseDTO> pesquisar(String nif) {
        if (!StringUtils.hasText(nif)) {
            return Optional.empty();
        }
        String nifNormalizado = nif.trim();
        List<ZeeTInvestidorEntity> investidores = investidorRepository.findByNif(nifNormalizado);
        if (!investidores.isEmpty()) {
            return Optional.of(pesquisaInvestidorHelper.fromInvestidorLocal(investidores.get(0)));
        }
        return pesquisaSircClient.pesquisar(nifNormalizado)
            .or(() -> pesquisaNifClient.pesquisar(nifNormalizado));
    }

    public Optional<PesquisaNifResponseDTO> pesquisarLocal(String nif) {
        if (!StringUtils.hasText(nif)) {
            return Optional.empty();
        }
        return investidorRepository.findByNif(nif.trim()).stream()
            .findFirst()
            .map(pesquisaInvestidorHelper::fromInvestidorLocal);
    }
}
