package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.application.cobranca.dto.RealizarPagamentoRequestDTO;
import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.client.PaymentGatewayPaymentClient;
import cv.zeemsv.api.infrastructure.entity.*;
import cv.zeemsv.api.infrastructure.repository.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CobrancaAnaliseTest {
    @Mock ZeeTInvestidorRepository investidorRepository;
    @Mock ZeeTCobrancaRepository cobrancaRepository;
    @Mock ZeeTCobrancaPrestacaoRepository prestacaoRepository;
    @Mock ZeeTPagamentoRepository pagamentoRepository;
    @Mock ZeeTSolicitacaoCobrancaRepository solicitacaoCobrancaRepository;
    @Mock ZeeTCobrancaTaxaRepository cobrancaTaxaRepository;
    @Mock ZeeTPagamentoTaxaRepository pagamentoTaxaRepository;
    @Mock DomainDescriptionHelper domainHelper;
    @Mock PaymentGatewayPaymentClient gateway;
    @InjectMocks CobrancaServiceImpl service;

    @ParameterizedTest
    @CsvSource({
        ", PENDENTE, comprovativo.pdf, true, true, true",
        "2, PENDENTE, comprovativo.pdf, true, true, false",
        "3, PENDENTE, comprovativo.pdf, true, false, true",
        "2, REJEITADO, comprovativo.pdf, false, false, false",
        "2, PAGO, comprovativo.pdf, false, false, false",
        "2, PENDENTE, , false, false, false"
    })
    void listagemApresentaAnalisePorDestino(Integer idPrestacao, String estado, String path,
        boolean cobrancaEmAnalise, boolean primeiraEmAnalise, boolean segundaEmAnalise) {
        var pagamento = pagamento(idPrestacao, estado, path);
        prepararListagem(List.of(pagamento));
        var response = service.findByInvestidorId(5).get(0);
        assertEquals(cobrancaEmAnalise, response.isEmAnalise());
        assertEquals(!cobrancaEmAnalise, response.isPodePagar());
        assertEquals(primeiraEmAnalise, response.getPrestacoes().get(0).isEmAnalise());
        assertEquals(!primeiraEmAnalise, response.getPrestacoes().get(0).isPodePagar());
        assertEquals(segundaEmAnalise, response.getPrestacoes().get(1).isEmAnalise());
        assertEquals(!segundaEmAnalise, response.getPrestacoes().get(1).isPodePagar());
        if (cobrancaEmAnalise) {
            assertEquals("Comprovativo de pagamento em análise.", response.getMotivoBloqueioPagamento());
        } else {
            assertNull(response.getMotivoBloqueioPagamento());
        }
        assertEquals("PENDENTE", response.getDmEstado());
    }

    @Test
    void cobrancaEParcelaLiquidadasNaoPodemSerPagas() {
        var pagamento = pagamento(null, "PAGO", "comprovativo.pdf");
        pagamento.setValorPago("200");
        prepararListagem(List.of(pagamento));
        var response = service.findByInvestidorId(5).get(0);
        assertFalse(response.isEmAnalise());
        assertFalse(response.isPodePagar());
        assertEquals("Sem dívida pendente.", response.getMotivoBloqueioPagamento());
        assertTrue(response.getPrestacoes().stream().noneMatch(p -> p.isPodePagar()));
    }

    @ParameterizedTest
    @CsvSource({", , true", ", 2, true", "2, , true", "2, 2, true", "2, 3, false"})
    void bloqueiaGatewaySomenteParaDestinosEmAnalise(Integer prestacaoPendente, Integer destino, boolean bloqueado) {
        var pagamento = pagamento(prestacaoPendente, "PENDENTE", "comprovativo.pdf");
        when(cobrancaRepository.findAllForPagamento(List.of(1))).thenReturn(List.of(cobranca()));
        when(prestacaoRepository.findByIdCobrancaInOrderByIdCobrancaAscIdAsc(List.of(1)))
            .thenReturn(List.of(prestacao(2), prestacao(3)));
        when(pagamentoRepository.findByIdCobrancaInOrderByIdCobrancaAscDataPagamentoDescIdDesc(List.of(1)))
            .thenReturn(List.of(pagamento));
        var dto = new RealizarPagamentoRequestDTO();
        dto.setIdsCobranca(List.of(1));
        if (destino != null) {
            dto.setIdsPrestacao(List.of(destino));
            when(prestacaoRepository.findAllById(List.of(destino))).thenReturn(List.of(prestacao(destino)));
        }
        if (bloqueado) {
            var ex = assertThrows(BusinessException.class, () -> service.realizarPagamento(dto));
            assertEquals("Comprovativo de pagamento em análise.", ex.getMessage());
            verifyNoInteractions(gateway);
        } else {
            when(gateway.createPayment(any())).thenThrow(new IllegalStateException("gateway chamado"));
            var ex = assertThrows(IllegalStateException.class, () -> service.realizarPagamento(dto));
            assertEquals("gateway chamado", ex.getMessage());
            verify(gateway).createPayment(any());
        }
    }

    private void prepararListagem(List<ZeeTPagamentoEntity> pagamentos) {
        when(investidorRepository.existsById(5)).thenReturn(true);
        when(cobrancaRepository.findByIdInvestidorOrderByDataEmissaoDescIdDesc(5)).thenReturn(List.of(cobranca()));
        when(prestacaoRepository.findByIdCobrancaInOrderByIdCobrancaAscIdAsc(List.of(1)))
            .thenReturn(List.of(prestacao(2), prestacao(3)));
        when(pagamentoRepository.findByIdCobrancaInOrderByIdCobrancaAscDataPagamentoDescIdDesc(List.of(1)))
            .thenReturn(pagamentos);
    }

    private ZeeTCobrancaEntity cobranca() {
        var cobranca = new ZeeTCobrancaEntity();
        cobranca.setId(1);
        cobranca.setValorTotal("200");
        cobranca.setValorDivida("200");
        cobranca.setDmEstado("PENDENTE");
        return cobranca;
    }

    private ZeeTCobrancaPrestacaoEntity prestacao(Integer id) {
        var prestacao = new ZeeTCobrancaPrestacaoEntity();
        prestacao.setId(id);
        prestacao.setIdCobranca(1);
        prestacao.setValor("100");
        prestacao.setDmEstado("PENDENTE");
        return prestacao;
    }

    private ZeeTPagamentoEntity pagamento(Integer idPrestacao, String estado, String path) {
        var pagamento = new ZeeTPagamentoEntity();
        pagamento.setId(10);
        pagamento.setIdCobranca(1);
        pagamento.setIdPrestacao(idPrestacao);
        pagamento.setDmEstadoPag(estado);
        pagamento.setPathComprovativoPagamento(path);
        pagamento.setValorPago("0");
        return pagamento;
    }
}
