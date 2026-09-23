package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.application.cobranca.dto.CobrancaInvestidorResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CriarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoResponseDTO;
import java.util.List;

public interface CobrancaService {
    List<CobrancaInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor);

    CobrancaPagamentoResponseDTO criarPagamento(CriarPagamentoRequestDTO dto);

    RealizarPagamentoResponseDTO realizarPagamento(RealizarPagamentoRequestDTO dto);
}
