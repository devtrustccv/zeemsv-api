package cv.zeemsv.api.application.pessoa.service;

import cv.zeemsv.api.application.pessoa.dto.ContatoResponseDTO;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ContatoService {
    public List<ContatoResponseDTO> findContactsByPessoaId(Integer pessoaId) {
        return Collections.emptyList();
    }
}
