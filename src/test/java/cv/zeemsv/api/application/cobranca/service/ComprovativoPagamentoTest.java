package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.domain.documento.dto.UploadDTO;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaPrestacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaPrestacaoRepository;
import cv.zeemsv.api.application.cobranca.dto.ComprovativoPagamentoRequestDTO;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComprovativoPagamentoTest {
    @Mock ZeeTCobrancaRepository cobrancaRepository;
    @Mock DocumentoBus documentoBus;
    @Mock ZeeTPagamentoRepository pagamentoRepository;
    @Mock ZeeTCobrancaPrestacaoRepository prestacaoRepository;
    @InjectMocks CobrancaServiceImpl service;

    @Test
    void guardaDocumentoEAssociaCaminhoSemAlterarEstado() {
        ZeeTCobrancaEntity cobranca = new ZeeTCobrancaEntity();
        cobranca.setId(1);
        cobranca.setDmEstado("PENDENTE");
        cobranca.setValorTotal("100");
        when(cobrancaRepository.findByIdForPagamento(1)).thenReturn(Optional.of(cobranca));
        when(pagamentoRepository.save(any())).thenAnswer(invocation -> {
            ZeeTPagamentoEntity pagamento = invocation.getArgument(0);
            pagamento.setId(10);
            return pagamento;
        });
        var ficheiro = new MockMultipartFile("ficheiro", "comprovativo.pdf", "application/pdf", new byte[] {1});

        var response = service.receberComprovativoPagamento(1, dados(), ficheiro);

        var upload = ArgumentCaptor.forClass(UploadDTO.class);
        verify(documentoBus).saveOrUpdate(upload.capture(), eq("tester"));
        assertEquals(upload.getValue().getFullPath(), response.getPathComprovativoPagamento());
        assertEquals("PAGAMENTO", upload.getValue().getZeeTDocRelacao().getTipoRelacao());
        assertEquals(BigDecimal.TEN, upload.getValue().getZeeTDocRelacao().getIdRelacao());
        assertEquals(10, response.getIdPagamento());
        assertEquals("PENDENTE", response.getDmEstadoPag());
        assertEquals(1, response.getIdCobranca());
        assertEquals("PENDENTE", cobranca.getDmEstado());
        verify(cobrancaRepository, never()).save(any());
        verify(prestacaoRepository, never()).save(any());
    }

    @Test
    void rejeitaFicheiroVazio() {
        var ficheiro = new MockMultipartFile("ficheiro", new byte[0]);
        assertThrows(BusinessException.class, () -> service.receberComprovativoPagamento(1, dados(), ficheiro));
        verifyNoInteractions(documentoBus, cobrancaRepository);
    }

    @Test
    void rejeitaCobrancaInexistenteAntesDeGuardarDocumento() {
        when(cobrancaRepository.findByIdForPagamento(1)).thenReturn(Optional.empty());
        var ficheiro = new MockMultipartFile("ficheiro", new byte[] {1});
        assertThrows(EntityNotFoundException.class, () -> service.receberComprovativoPagamento(1, dados(), ficheiro));
        verifyNoInteractions(documentoBus);
    }

    @Test
    void rejeitaPrestacaoDeOutraCobranca() {
        var cobranca = new ZeeTCobrancaEntity();
        cobranca.setId(1);
        when(cobrancaRepository.findByIdForPagamento(1)).thenReturn(Optional.of(cobranca));
        var prestacao = new ZeeTCobrancaPrestacaoEntity();
        prestacao.setIdCobranca(2);
        when(prestacaoRepository.findById(3)).thenReturn(Optional.of(prestacao));
        var dto = dados();
        dto.setIdPrestacao(3);
        assertThrows(BusinessException.class, () -> service.receberComprovativoPagamento(1, dto,
            new MockMultipartFile("ficheiro", new byte[] {1})));
        verifyNoInteractions(documentoBus, pagamentoRepository);
    }

    private ZeeTCobrancaEntity cobranca() {
        var cobranca = new ZeeTCobrancaEntity();
        cobranca.setId(1);
        cobranca.setValorTotal("100");
        return cobranca;
    }

    @Test
    void submissaoDeParcelaCriaPagamentoPendenteSemLiquidar() {
        var cobranca = cobranca();
        when(cobrancaRepository.findByIdForPagamento(1)).thenReturn(Optional.of(cobranca));
        var prestacao = new ZeeTCobrancaPrestacaoEntity();
        prestacao.setId(3);
        prestacao.setIdCobranca(1);
        prestacao.setValor("100");
        prestacao.setDmEstado("PENDENTE");
        when(prestacaoRepository.findById(3)).thenReturn(Optional.of(prestacao));
        when(pagamentoRepository.save(any())).thenAnswer(invocation -> {
            ZeeTPagamentoEntity pagamento = invocation.getArgument(0);
            pagamento.setId(10);
            return pagamento;
        });
        var dto = dados();
        dto.setIdPrestacao(3);
        var response = service.receberComprovativoPagamento(1, dto,
            new MockMultipartFile("ficheiro", "comprovativo.pdf", "application/pdf", new byte[] {1}));
        assertEquals(3, response.getIdPrestacao());
        assertEquals("PENDENTE", response.getDmEstadoPag());
        assertNull(prestacao.getIdPagamento());
        verify(prestacaoRepository, never()).save(any());
        verify(cobrancaRepository, never()).save(any());
    }

    private ComprovativoPagamentoRequestDTO dados() {
        var dto = new ComprovativoPagamentoRequestDTO();
        dto.setValor(new BigDecimal("50"));
        dto.setDataPagamento(LocalDate.now());
        dto.setFormaPagamento("TRANSFERENCIA");
        dto.setUser("tester");
        return dto;
    }

    @Test
    void pagamentosPendentesERejeitadosNaoReduzemDividaNaSubmissao() {
        when(cobrancaRepository.findByIdForPagamento(1)).thenReturn(Optional.of(cobranca()));
        var pendente = new ZeeTPagamentoEntity();
        pendente.setDmEstadoPag("PENDENTE");
        pendente.setValorPago("100");
        var rejeitado = new ZeeTPagamentoEntity();
        rejeitado.setDmEstadoPag("REJEITADO");
        rejeitado.setValorPago("100");
        when(pagamentoRepository.findByIdCobrancaInOrderByIdCobrancaAscDataPagamentoDescIdDesc(List.of(1)))
            .thenReturn(List.of(pendente, rejeitado));
        when(pagamentoRepository.save(any())).thenAnswer(invocation -> {
            ZeeTPagamentoEntity pagamento = invocation.getArgument(0);
            pagamento.setId(10);
            return pagamento;
        });
        var response = service.receberComprovativoPagamento(1, dados(),
            new MockMultipartFile("ficheiro", new byte[] {1}));
        assertEquals("PENDENTE", response.getDmEstadoPag());
    }

    @Test
    void pagamentoConfirmadoNoBackofficeReduzDividaDisponivel() {
        when(cobrancaRepository.findByIdForPagamento(1)).thenReturn(Optional.of(cobranca()));
        var pago = new ZeeTPagamentoEntity();
        pago.setDmEstadoPag("PAGO");
        pago.setValorPago("90");
        when(pagamentoRepository.findByIdCobrancaInOrderByIdCobrancaAscDataPagamentoDescIdDesc(List.of(1)))
            .thenReturn(List.of(pago));
        assertThrows(BusinessException.class, () -> service.receberComprovativoPagamento(1, dados(),
            new MockMultipartFile("ficheiro", new byte[] {1})));
        verifyNoInteractions(documentoBus);
        verify(pagamentoRepository, never()).save(any());
    }
}
