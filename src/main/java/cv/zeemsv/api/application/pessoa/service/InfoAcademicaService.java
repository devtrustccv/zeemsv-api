package cv.zeemsv.api.application.pessoa.service;

import cv.zeemsv.api.application.pessoa.dto.InfoAcademicaResponseDTO;
import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class InfoAcademicaService {
    public List<InfoAcademicaResponseDTO> findInfoAcademicasByPessoaId(Integer pessoaId) {
        return Collections.emptyList();
    }
}
