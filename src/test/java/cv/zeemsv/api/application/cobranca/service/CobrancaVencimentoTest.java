package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaPrestacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoEntity;
import cv.zeemsv.api.infrastructure.repository.*;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CobrancaVencimentoTest {
    @Mock ZeeTInvestidorRepository investidorRepository;
    @Mock ZeeTCobrancaRepository cobrancaRepository;
    @Mock ZeeTCobrancaPrestacaoRepository prestacaoRepository;
    @Mock ZeeTPagamentoRepository pagamentoRepository;
    @Mock ZeeTSolicitacaoCobrancaRepository solicitacaoCobrancaRepository;
    @Mock ZeeTCobrancaTaxaRepository cobrancaTaxaRepository;
    @Mock ZeeTPagamentoTaxaRepository pagamentoTaxaRepository;
    @Mock DomainDescriptionHelper domainHelper;
    @InjectMocks CobrancaServiceImpl service;

    private ZeeTCobrancaEntity cobranca;
    private ZeeTCobrancaPrestacaoEntity prestacao;

    @BeforeEach
    void setup() {
        cobranca = new ZeeTCobrancaEntity();
        cobranca.setId(1);
        prestacao = new ZeeTCobrancaPrestacaoEntity();
        prestacao.setId(2);
        prestacao.setIdCobranca(1);
        prestacao.setValor("100");
        when(investidorRepository.existsById(3)).thenReturn(true);
        when(cobrancaRepository.findByIdInvestidorOrderByDataEmissaoDescIdDesc(3)).thenReturn(List.of(cobranca));
        when(prestacaoRepository.findByIdCobrancaInOrderByIdCobrancaAscIdAsc(List.of(1))).thenReturn(List.of(prestacao));
    }

    @ParameterizedTest
    @CsvSource({
        "-1, PENDENTE, 100, true",
        "-1, PENDENTE, 50, true",
        "0, PENDENTE, 100, false",
        "1, PENDENTE, 100, false",
        "-1, PAGO, 0, false",
        "-1, PENDENTE, 0, false",
        "-1, PENDENTE, -10, false",
        "-1, PAGO, 100, false",
        ", PENDENTE, 100, false"
    })
    void indicatesOverdueOnlyForPastDueDatesWithOutstandingBalance(Integer dias, String estado, String divida, boolean esperado) {
        LocalDate vencimento = dias == null ? null : LocalDate.now().plusDays(dias);
        cobranca.setDataVencimento(vencimento);
        cobranca.setDmEstado(estado);
        cobranca.setValorDivida(divida);
        prestacao.setDataVencimento(vencimento);
        prestacao.setDmEstado(estado);
        var pagamento = new ZeeTPagamentoEntity();
        pagamento.setId(4);
        pagamento.setIdCobranca(1);
        pagamento.setIdPrestacao(2);
        pagamento.setValorPago(new java.math.BigDecimal("100").subtract(new java.math.BigDecimal(divida)).toPlainString());
        when(pagamentoRepository.findByIdCobrancaInOrderByIdCobrancaAscDataPagamentoDescIdDesc(List.of(1)))
            .thenReturn(List.of(pagamento));

        var response = service.findByInvestidorId(3).get(0);

        assertEquals(esperado, response.isVencido());
        assertEquals(esperado, response.getPrestacoes().get(0).isVencido());
    }
}
