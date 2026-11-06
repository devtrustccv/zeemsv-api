package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.application.cobranca.dto.CobrancaInvestidorResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.ComprovativoPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.ComprovativoPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CriarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.PagamentoIntencaoStatusResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoRequestDTO;
import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoResponseDTO;
import cv.zeemsv.api.application.paymentgateway.dto.PaymentGatewayPaymentValidationRequestDTO;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface CobrancaService {
    ComprovativoPagamentoResponseDTO receberComprovativoPagamento(Integer idCobranca, ComprovativoPagamentoRequestDTO dto, MultipartFile ficheiro);

    List<CobrancaInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor);

    CobrancaPagamentoResponseDTO criarPagamento(CriarPagamentoRequestDTO dto);

    List<CobrancaPagamentoResponseDTO> confirmarPagamento(PaymentGatewayPaymentValidationRequestDTO dto);

    String confirmarPagamentoERetornarIntentionId(PaymentGatewayPaymentValidationRequestDTO dto);

    RealizarPagamentoResponseDTO realizarPagamento(RealizarPagamentoRequestDTO dto);

    PagamentoIntencaoStatusResponseDTO consultarEstadoPagamento(String intentionId);
}
