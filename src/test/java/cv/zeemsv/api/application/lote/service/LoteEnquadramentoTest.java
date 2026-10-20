package cv.zeemsv.api.application.lote.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.infrastructure.repository.ZeeTLoteRepository;
import cv.zeemsv.api.infrastructure.repository.projection.LoteInvestidorProjection;
import cv.zeemsv.api.infrastructure.repository.projection.LoteEnquadramentoProjection;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoteEnquadramentoTest {
    @Mock ZeeTLoteRepository repository;
    @Mock DomainDescriptionHelper domainHelper;
    @InjectMocks LoteServiceImpl service;

    @Test
    void loadsDirectEnquadramentoForOwnerWithoutProject() {
        var lote = mock(LoteInvestidorProjection.class);
        when(lote.getIdLote()).thenReturn(5);
        when(lote.getIdProjeto()).thenReturn(null);
        when(repository.findProprietarioByInvestidorId(41)).thenReturn(List.of(lote));
        var enquadramento = mock(LoteEnquadramentoProjection.class);
        when(enquadramento.getIdLote()).thenReturn(5);
        when(enquadramento.getFormasComercializacao()).thenReturn("A, B");
        when(repository.findEnquadramentosByLoteIds(List.of(5))).thenReturn(List.of(enquadramento));
        when(domainHelper.describe(anyString(), nullable(String.class))).thenAnswer(invocation -> {
            String codigo = invocation.getArgument(1);
            return codigo == null ? null : "Forma " + codigo;
        });

        var result = service.findByInvestidorId(41).get(0);

        assertEquals("PROPRIETARIO", result.getCondicao());
        assertNull(result.getIdProjeto());
        assertEquals("A, B", result.getFormasComercializacao());
        assertEquals("Forma A, Forma B", result.getFormasComercializacaoDesc());
        assertEquals(result.getFormasComercializacao(), result.getDmEnquadramento());
        assertEquals(result.getFormasComercializacaoDesc(), result.getDmEnquadramentoDesc());
    }

    @Test
    void preservesProjectEnquadramento() {
        var lote = mock(LoteInvestidorProjection.class);
        when(lote.getFormasComercializacao()).thenReturn("A");
        when(repository.findByProjectoId(10)).thenReturn(List.of(lote));
        assertEquals("A", service.findAssociados(null, 10).get(0).getFormasComercializacao());
        verify(repository, never()).findEnquadramentosByLoteIds(any());
    }
}
