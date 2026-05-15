package cv.zeemsv.api.application.pessoa.service;

import cv.zeemsv.api.application.pessoa.dto.PessoaPesquisaResponseDTO;
import cv.zeemsv.api.infrastructure.client.PesquisaCniClient;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSocioRepresEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSocioRepresRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PessoaPesquisaService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final ZeeTSocioRepresRepository socioRepresRepository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final PesquisaCniClient pesquisaCniClient;

    @Transactional(readOnly = true)
    public List<PessoaPesquisaResponseDTO> pesquisar(String nomeCompleto, String nrDocumento) {
        if (!StringUtils.hasText(nomeCompleto) && !StringUtils.hasText(nrDocumento)) {
            return List.of();
        }

        String nome = blankToNull(nomeCompleto);
        String documento = blankToNull(nrDocumento);

        List<PessoaPesquisaResponseDTO> sociosRepresentantes = pesquisarSocioRepresLocal(nome, documento)
            .stream()
            .map(this::toPessoaSocioRepresLocal)
            .toList();

        if (!sociosRepresentantes.isEmpty()) {
            return sociosRepresentantes;
        }

        List<PessoaPesquisaResponseDTO> investidores = pesquisarInvestidorLocal(nome, documento)
            .stream()
            .map(this::toPessoaInvestidorLocal)
            .toList();

        if (!investidores.isEmpty()) {
            return investidores;
        }

        return pesquisaCniClient.pesquisar(documento, nome);
    }

    private List<ZeeTSocioRepresEntity> pesquisarSocioRepresLocal(String nomeCompleto, String nrDocumento) {
        if (nomeCompleto != null && nrDocumento != null) {
            return socioRepresRepository.findByNomeContainingIgnoreCaseAndNrDoc(nomeCompleto, nrDocumento);
        }
        if (nomeCompleto != null) {
            return socioRepresRepository.findByNomeContainingIgnoreCase(nomeCompleto);
        }
        return socioRepresRepository.findByNrDoc(nrDocumento);
    }

    private List<ZeeTInvestidorEntity> pesquisarInvestidorLocal(String nomeCompleto, String nrDocumento) {
        if (nomeCompleto != null && nrDocumento != null) {
            return investidorRepository.findByDenominacaoContainingIgnoreCaseAndNrDocumento(nomeCompleto, nrDocumento);
        }
        if (nomeCompleto != null) {
            return investidorRepository.findByDenominacaoContainingIgnoreCase(nomeCompleto);
        }
        return investidorRepository.findByNrDocumento(nrDocumento);
    }

    private PessoaPesquisaResponseDTO toPessoaSocioRepresLocal(ZeeTSocioRepresEntity entity) {
        PessoaPesquisaResponseDTO pessoa = new PessoaPesquisaResponseDTO();
        pessoa.setNome(entity.getNome());
        pessoa.setNrDocumento(entity.getNrDoc());
        pessoa.setTipoDocumento(entity.getTipoDoc());
        pessoa.setTpDoc(entity.getTipoDoc());
        pessoa.setNacionalidade(entity.getNacionalidade());
        pessoa.setTelemovel(entity.getTelemovel() != null ? entity.getTelemovel().toPlainString() : null);
        pessoa.setEmail(entity.getEmail());
        pessoa.setOrigem("LOCAL_SOCIO_REPRES");
        return pessoa;
    }

    private PessoaPesquisaResponseDTO toPessoaInvestidorLocal(ZeeTInvestidorEntity entity) {
        PessoaPesquisaResponseDTO pessoa = new PessoaPesquisaResponseDTO();
        pessoa.setNome(entity.getDenominacao());
        pessoa.setNrDocumento(entity.getNrDocumento());
        pessoa.setDataNascimento(entity.getDataNascimento() != null ? entity.getDataNascimento().format(DATE_FORMATTER) : null);
        pessoa.setNacionalidade(entity.getPaisOrigem());
        pessoa.setTelemovel(entity.getTelemovel() != null ? entity.getTelemovel().toPlainString() : null);
        pessoa.setEstadoCivil(entity.getDmEstadoCivil());
        pessoa.setGenero(entity.getDmGenero());
        pessoa.setEmail(entity.getEmail());
        pessoa.setOrigem("LOCAL_INVESTIDOR");
        return pessoa;
    }

    private String blankToNull(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }
}
