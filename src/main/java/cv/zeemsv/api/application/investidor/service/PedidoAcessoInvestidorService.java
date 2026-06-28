package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.PedidoAcessoInvestidorResponseDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PedidoAcessoInvestidorService {
    PedidoAcessoInvestidorResponseDTO create(PedidoAcessoInvestidorRequestDTO dto, MultipartFile ficheiroCompravativo);

    List<PedidoAcessoInvestidorResponseDTO> findByUserId(Integer idUser);
}
