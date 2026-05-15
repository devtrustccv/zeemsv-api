package cv.zeemsv.api.application.atividade.service;

import cv.zeemsv.api.application.atividade.dto.AtividadeResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoInvestidorResponseDTO;
import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.infrastructure.entity.ZeeTAtividadeEntity;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTAtividadeRepository;
import cv.zeemsv.api.infrastructure.repository.projection.NotificacaoInvestidorProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AtividadeServiceImpl implements AtividadeService {
    private static final String TIPO_NOTIFICACAO = "NOTIFICACAO";

    private final ZeeTAtividadeRepository repository;
    private final TNotificacaoRelacaoRepository notificacaoRelacaoRepository;
    private final DomainDescriptionHelper domainHelper;

    @Override
    @Transactional(readOnly = true)
    public List<NotificacaoInvestidorResponseDTO> findNotificacoesByInvestidorId(Integer idInvestidor) {
        return notificacaoRelacaoRepository.findByInvestidorId(idInvestidor)
            .stream()
            .map(this::toNotificacaoResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> findAgendadasByInvestidorId(Integer idInvestidor) {
        return repository.findByIdInvestidorAndAgendamentoTrueOrderByDataCreateDescIdDesc(idInvestidor)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AtividadeResponseDTO toResponse(ZeeTAtividadeEntity entity) {
        AtividadeResponseDTO dto = new AtividadeResponseDTO();
        dto.setId(entity.getId());
        dto.setIdInvestidor(entity.getIdInvestidor());
        dto.setIdProjeto(entity.getIdProjeto());
        dto.setIdRepresentante(entity.getIdRepresentante());
        dto.setIdRelacao(entity.getIdRelacao());
        dto.setDmTipoAtividade(entity.getDmTipoAtividade());
        dto.setDmTipoAtividadeDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_ATIVIDADE, entity.getDmTipoAtividade()));
        dto.setDmTag(entity.getDmTag());
        dto.setDataConclusao(entity.getDataConclusao());
        dto.setUserResponsavel(entity.getUserResponsavel());
        dto.setDmEstadoAtividade(entity.getDmEstadoAtividade());
        dto.setDmEstadoAtividadeDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO_ATIVIDADE, entity.getDmEstadoAtividade()));
        dto.setIdPessoaCont(entity.getIdPessoaCont());
        dto.setDmTpChamada(entity.getDmTpChamada());
        dto.setDmTpChamadaDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_CHAMADA, entity.getDmTpChamada()));
        dto.setDmResultado(entity.getDmResultado());
        dto.setDmResultadoDesc(domainHelper.describe(DomainDescriptionHelper.RESULTADO_CHAMADA, entity.getDmResultado()));
        dto.setHoraChamada(entity.getHoraChamada());
        dto.setAssuntoChamada(entity.getAssuntoChamada());
        dto.setResumoChamada(entity.getResumoChamada());
        dto.setNotaTitulo(entity.getNotaTitulo());
        dto.setNotaConteudo(entity.getNotaConteudo());
        dto.setTarefaTitulo(entity.getTarefaTitulo());
        dto.setTarefaDescricao(entity.getTarefaDescricao());
        dto.setDmPrioridade(entity.getDmPrioridade());
        dto.setDmPrioridadeDesc(domainHelper.describe(DomainDescriptionHelper.PRIORIDADE_TAREFA, entity.getDmPrioridade()));
        dto.setDataInicio(entity.getDataInicio());
        dto.setDataFim(entity.getDataFim());
        dto.setDmEstado(entity.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getDmEstado()));
        dto.setUserRegisto(entity.getUserRegisto());
        dto.setDataCreate(entity.getDataCreate());
        dto.setTipoRelacao(entity.getTipoRelacao());
        dto.setAgendamento(entity.getAgendamento());
        dto.setTpAgendamento(entity.getTpAgendamento());
        dto.setTpAgendamentoDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_AGENDAMENTO, entity.getTpAgendamento()));
        dto.setHoraInicio(entity.getHoraInicio());
        dto.setHoraFim(entity.getHoraFim());
        return dto;
    }

    private NotificacaoInvestidorResponseDTO toNotificacaoResponse(NotificacaoInvestidorProjection projection) {
        NotificacaoInvestidorResponseDTO dto = new NotificacaoInvestidorResponseDTO();
        dto.setIdRelacaoNotificacao(projection.getIdRelacaoNotificacao());
        dto.setTpRelacao(projection.getTpRelacao());
        dto.setIdRelacao(projection.getIdRelacao());
        dto.setIdNotificacao(projection.getIdNotificacao());
        dto.setIdPai(projection.getIdPai());
        dto.setIdAplicacao(projection.getIdAplicacao());
        dto.setIdOrganica(projection.getIdOrganica());
        dto.setUserRegisto(projection.getUserRegisto());
        dto.setDataRegisto(projection.getDataRegisto());
        dto.setAssunto(projection.getAssunto());
        dto.setDataEnvio(projection.getDataEnvio());
        dto.setMensagem(projection.getMensagem());
        dto.setMensagemConfirmacao(projection.getMensagemConfirmacao());
        dto.setEmail(projection.getEmail());
        dto.setTelemovel(projection.getTelemovel());
        dto.setEstado(projection.getEstado());
        dto.setFlagAutomatico(projection.getFlagAutomatico());
        dto.setFlagSucesso(projection.getFlagSucesso());
        dto.setFlagLeitura(projection.getFlagLeitura());
        dto.setUserLeitura(projection.getUserLeitura());
        dto.setNumeroReenvios(projection.getNumeroReenvios());
        dto.setTipo(projection.getTipo());
        dto.setNotificacaoIdRelacao(projection.getNotificacaoIdRelacao());
        dto.setDe(projection.getDe());
        dto.setEmailsEnviados(projection.getEmailsEnviados());
        dto.setConfirmRecebimento(projection.getConfirmRecebimento());
        dto.setTotalAnexos(projection.getTotalAnexos());
        dto.setRelacao(toRelacao(projection));
        return dto;
    }

    private Map<String, Object> toRelacao(NotificacaoInvestidorProjection projection) {
        String tipo = projection.getTpRelacao() == null ? "" : projection.getTpRelacao().trim().toUpperCase();
        Map<String, Object> relacao = new LinkedHashMap<>();
        relacao.put("tp_relacao", projection.getTpRelacao());
        relacao.put("id_relacao", projection.getIdRelacao());

        if ("ATIVIDADE".equals(tipo)) {
            relacao.put("id", projection.getAtividadeId());
            relacao.put("dm_tipo_atividade", projection.getAtividadeDmTipoAtividade());
            relacao.put("dm_tipo_atividade_desc", domainHelper.describe(DomainDescriptionHelper.TIPO_ATIVIDADE, projection.getAtividadeDmTipoAtividade()));
            relacao.put("titulo", projection.getAtividadeTitulo());
            relacao.put("resumo", projection.getAtividadeResumo());
            relacao.put("dm_estado_atividade", projection.getAtividadeDmEstadoAtividade());
            relacao.put("dm_estado_atividade_desc", domainHelper.describe(DomainDescriptionHelper.ESTADO_ATIVIDADE, projection.getAtividadeDmEstadoAtividade()));
            relacao.put("data_create", projection.getAtividadeDataCreate());
        } else if ("SOLICITACAO".equals(tipo)) {
            relacao.put("id", projection.getSolicitacaoId());
            relacao.put("id_tp_solicitacao", projection.getSolicitacaoIdTpSolicitacao());
            relacao.put("tipo_solicitacao", projection.getSolicitacaoTipoNome());
            relacao.put("id_processo", projection.getSolicitacaoIdProcesso());
            relacao.put("dm_estado_proc", projection.getSolicitacaoDmEstadoProc());
            relacao.put("dm_estado_proc_desc", domainHelper.describe(DomainDescriptionHelper.ESTADO_PROC_SOLICIT, projection.getSolicitacaoDmEstadoProc()));
            relacao.put("data_solic", projection.getSolicitacaoDataSolic());
        } else if ("PROJETO".equals(tipo)) {
            relacao.put("id", projection.getProjetoId());
            relacao.put("denominacao", projection.getProjetoDenominacao());
            relacao.put("dm_regime", projection.getProjetoDmRegime());
            relacao.put("dm_regime_desc", domainHelper.describe(DomainDescriptionHelper.REGIME, projection.getProjetoDmRegime()));
            relacao.put("dm_produto_servico", projection.getProjetoDmProdutoServico());
            relacao.put("dm_produto_servico_desc", domainHelper.describe(DomainDescriptionHelper.PRODUTO_SERVICO, projection.getProjetoDmProdutoServico()));
            relacao.put("dm_estado_proc", projection.getProjetoDmEstadoProc());
            relacao.put("dm_estado_proc_desc", domainHelper.describe(DomainDescriptionHelper.ESTADO_PROCESO, projection.getProjetoDmEstadoProc()));
            relacao.put("dm_situacao", projection.getProjetoDmSituacao());
            relacao.put("dm_situacao_desc", domainHelper.describe(DomainDescriptionHelper.SITUACAO_PROJ, projection.getProjetoDmSituacao()));
        } else if ("LOTE".equals(tipo)) {
            relacao.put("id", projection.getLoteId());
            relacao.put("ref_lote", projection.getLoteRefLote());
            relacao.put("nip", projection.getLoteNip());
            relacao.put("dm_situacao_cd", projection.getLoteDmSituacaoCd());
            relacao.put("dm_situacao_cd_desc", domainHelper.describe(DomainDescriptionHelper.SITUACAO_LOTE, projection.getLoteDmSituacaoCd()));
            relacao.put("zona", projection.getLoteZona());
        }

        return relacao;
    }
}
