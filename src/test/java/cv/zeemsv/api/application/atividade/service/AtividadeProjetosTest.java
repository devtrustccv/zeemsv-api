package cv.zeemsv.api.application.atividade.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.infrastructure.entity.ZeeTAtividadeEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTAtividadeRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTDocRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTProjAtividadeRepository;
import cv.zeemsv.api.infrastructure.repository.projection.AtividadeProjetoProjection;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtividadeProjetosTest {
    @Mock ZeeTAtividadeRepository repository;
    @Mock ZeeTProjAtividadeRepository projAtividadeRepository;
    @Mock ZeeTDocRelacaoRepository docRelacaoRepository;
    @Mock DomainDescriptionHelper domainHelper;
    @Mock org.springframework.jdbc.core.JdbcTemplate igrpJdbcTemplate;
    @InjectMocks AtividadeServiceImpl service;

    @Test
    void groupsProjectsByActivityAndReturnsEmptyListForUnlinkedActivity() {
        ZeeTAtividadeEntity first = atividade(1);
        first.setIdProjeto("99");
        when(repository.findByIdInvestidorAndAgendamentoTrueOrderByDataCreateDescIdDesc(BigDecimal.TEN))
            .thenReturn(List.of(first, atividade(2)));
        when(projAtividadeRepository.findProjetosByAtividadeIds(List.of(1, 2)))
            .thenReturn(List.of(projeto(10, "Projeto A"), projeto(20, "Projeto B")));

        var result = service.findAgendadasByInvestidorId(10);

        assertEquals(List.of(10, 20), result.get(0).getProjetos().stream().map(p -> p.id()).toList());
        assertEquals("Projeto A", result.get(0).getProjetos().get(0).denominacao());
        assertEquals("99", result.get(0).getIdProjeto());
        assertTrue(result.get(1).getProjetos().isEmpty());
        verify(projAtividadeRepository).findProjetosByAtividadeIds(List.of(1, 2));
    }

    @Test
    void skipsProjectQueryWhenThereAreNoActivities() {
        when(repository.findByIdInvestidorAndAgendamentoTrueOrderByDataCreateDescIdDesc(BigDecimal.TEN))
            .thenReturn(List.of());
        assertTrue(service.findAgendadasByInvestidorId(10).isEmpty());
        verifyNoInteractions(projAtividadeRepository);
        verifyNoInteractions(igrpJdbcTemplate);
    }

    @Test
    void resolvesBothUserNamesInOneQueryAndPreservesMissingUserId() throws Exception {
        var first = atividade(1);
        first.setUserRegisto(7);
        first.setUserResponsavel(8);
        var second = atividade(2);
        second.setUserRegisto(7);
        second.setUserResponsavel(99);
        when(repository.findByIdInvestidorAndAgendamentoTrueOrderByDataCreateDescIdDesc(BigDecimal.TEN))
            .thenReturn(List.of(first, second));
        doAnswer(invocation -> {
            org.springframework.jdbc.core.RowCallbackHandler handler = invocation.getArgument(1);
            var rs = mock(java.sql.ResultSet.class);
            when(rs.getInt("id")).thenReturn(7, 8);
            when(rs.getString("name")).thenReturn("Maria", "Joao");
            handler.processRow(rs);
            handler.processRow(rs);
            return null;
        }).when(igrpJdbcTemplate).query(
            eq("select id, name from tbl_user where id in (?,?,?)"),
            any(org.springframework.jdbc.core.RowCallbackHandler.class), eq(7), eq(8), eq(99));

        var result = service.findAgendadasByInvestidorId(10);

        assertEquals("Maria", result.get(0).getUserRegistoNome());
        assertEquals("Joao", result.get(0).getUserResponsavelNome());
        assertEquals("Maria", result.get(1).getUserRegistoNome());
        assertEquals(99, result.get(1).getUserResponsavel());
        assertNull(result.get(1).getUserResponsavelNome());
        verify(igrpJdbcTemplate).query(eq("select id, name from tbl_user where id in (?,?,?)"),
            any(org.springframework.jdbc.core.RowCallbackHandler.class), eq(7), eq(8), eq(99));
    }

    private ZeeTAtividadeEntity atividade(int id) {
        var entity = new ZeeTAtividadeEntity();
        entity.setId(id);
        return entity;
    }

    private AtividadeProjetoProjection projeto(int id, String nome) {
        return new AtividadeProjetoProjection() {
            public Integer getIdAtividade() { return 1; }
            public Integer getIdProjeto() { return id; }
            public String getDenominacao() { return nome; }
        };
    }
}
