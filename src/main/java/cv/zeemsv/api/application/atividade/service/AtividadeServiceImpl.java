package cv.zeemsv.api.application.atividade.service;

import cv.zeemsv.api.application.atividade.dto.AtividadeResponseDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoRequestDTO;
import cv.zeemsv.api.application.atividade.dto.InteracaoResponseDTO;
import cv.zeemsv.api.application.atividade.dto.NotificacaoInvestidorResponseDTO;
import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.generic.service.EmailService;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTEmailsEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTAtividadeEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTParamReportEntity;
import cv.zeemsv.api.infrastructure.repository.TNotificacaoRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTAtividadeRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTEmailsRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTParamReportRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import cv.zeemsv.api.infrastructure.repository.projection.NotificacaoInvestidorProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Log4j2
public class AtividadeServiceImpl implements AtividadeService {
    private static final String TIPO_ATIVIDADE_INTERACAO = "INTERACAO";
    private static final String TIPO_NOTIFICACAO = "NOTIFICACAO";
    private static final String ESTADO_INTERACAO_SUBMETIDO = "SUBMETIDO";
    private static final String ESTADO_ATIVO = "A";

    private final ZeeTAtividadeRepository repository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final ZeeTUserRepository userRepository;
    private final ZeeTParamReportRepository paramReportRepository;
    private final ZeeTEmailsRepository emailsRepository;
    private final TNotificacaoRelacaoRepository notificacaoRelacaoRepository;
    private final EmailService emailService;
    private final DomainDescriptionHelper domainHelper;

    @Override
    @Transactional
    public AtividadeResponseDTO createInteracao(InteracaoRequestDTO dto) {
        if (dto.getIdInvestidor() != null && !investidorRepository.existsById(dto.getIdInvestidor())) {
            throw new BusinessException("Investidor nao encontrado.");
        }
        if (dto.getIdUser() != null && !userRepository.existsById(dto.getIdUser())) {
            throw new BusinessException("Utilizador nao encontrado.");
        }

        ZeeTAtividadeEntity entity = new ZeeTAtividadeEntity();
        entity.setIdInvestidor(toBigDecimal(dto.getIdInvestidor()));
        entity.setIdUser(dto.getIdUser());
        entity.setNome(trim(dto.getNome()));
        entity.setEmail(trim(dto.getEmail()));
        entity.setTelefone(trim(dto.getTelefone()));
        entity.setTipoInteracao(trim(dto.getTipoInteracao()));
        entity.setAssuntoInteracao(trim(dto.getAssuntoInteracao()));
        entity.setMensagemInteracao(trim(dto.getMensagemInteracao()));
        entity.setDmEstadoInteracao(ESTADO_INTERACAO_SUBMETIDO);
        entity.setDmTipoAtividade(TIPO_ATIVIDADE_INTERACAO);
        entity.setDmEstado(ESTADO_ATIVO);
        entity.setUserResponsavel(dto.getIdUser());
        entity.setUserRegisto(dto.getIdUser());
        entity.setDataCreate(LocalDate.now());

        ZeeTAtividadeEntity saved = repository.save(entity);
        notifyTecnicosInteracao(saved);
        notifyRequerenteInteracao(saved);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InteracaoResponseDTO> findInteracoes(Integer idUser, Integer idInvestidor, String email) {
        String emailFilter = trim(email);
        if (idUser == null && idInvestidor == null && !StringUtils.hasText(emailFilter)) {
            throw new BusinessException("Informe pelo menos um filtro: id_user, id_investidor ou email.");
        }

        return repository.findInteracoes(
                TIPO_ATIVIDADE_INTERACAO,
                idUser,
                toBigDecimal(idInvestidor),
                emailFilter
            )
            .stream()
            .map(this::toInteracaoResponse)
            .toList();
    }

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
    public List<NotificacaoInvestidorResponseDTO> findNotificacoesByUserId(Integer idUser) {
        return notificacaoRelacaoRepository.findByUserId(idUser)
            .stream()
            .map(this::toNotificacaoResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AtividadeResponseDTO> findAgendadasByInvestidorId(Integer idInvestidor) {
        return repository.findByIdInvestidorAndAgendamentoTrueOrderByDataCreateDescIdDesc(toBigDecimal(idInvestidor))
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AtividadeResponseDTO toResponse(ZeeTAtividadeEntity entity) {
        AtividadeResponseDTO dto = new AtividadeResponseDTO();
        dto.setId(entity.getId());
        dto.setIdInvestidor(toInteger(entity.getIdInvestidor()));
        dto.setIdProjeto(entity.getIdProjeto());
        dto.setIdRepresentante(toInteger(entity.getIdRepresentante()));
        dto.setIdRelacao(toInteger(entity.getIdRelacao()));
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
        dto.setTipoInteracao(entity.getTipoInteracao());
        dto.setDmEstadoInteracao(entity.getDmEstadoInteracao());
        dto.setAssuntoInteracao(entity.getAssuntoInteracao());
        dto.setMensagemInteracao(entity.getMensagemInteracao());
        dto.setUserResposta(entity.getUserResposta());
        dto.setDataResposta(entity.getDataResposta());
        dto.setMensagemResposta(entity.getMensagemResposta());
        dto.setIdUser(entity.getIdUser());
        dto.setEmail(entity.getEmail());
        dto.setTelefone(entity.getTelefone());
        dto.setNome(entity.getNome());
        dto.setTipoInteracaoDesc(resolveTipoInteracao(entity.getTipoInteracao()));
        dto.setDmEstadoInteracaoDesc(resolveEstadoInteracao(entity.getDmEstadoInteracao()));
        return dto;
    }

    private InteracaoResponseDTO toInteracaoResponse(ZeeTAtividadeEntity entity) {
        InteracaoResponseDTO dto = new InteracaoResponseDTO();
        dto.setId(entity.getId());
        dto.setIdInvestidor(toInteger(entity.getIdInvestidor()));
        dto.setIdUser(entity.getIdUser());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setTelefone(entity.getTelefone());
        dto.setTipoInteracao(entity.getTipoInteracao());
        dto.setTipoInteracaoDesc(resolveTipoInteracao(entity.getTipoInteracao()));
        dto.setDmEstadoInteracao(entity.getDmEstadoInteracao());
        dto.setDmEstadoInteracaoDesc(resolveEstadoInteracao(entity.getDmEstadoInteracao()));
        dto.setAssuntoInteracao(entity.getAssuntoInteracao());
        dto.setMensagemInteracao(entity.getMensagemInteracao());
        dto.setUserResposta(entity.getUserResposta());
        dto.setDataResposta(entity.getDataResposta());
        dto.setMensagemResposta(entity.getMensagemResposta());
        dto.setDataCreate(entity.getDataCreate());
        return dto;
    }

    private BigDecimal toBigDecimal(Integer value) {
        return value == null ? null : BigDecimal.valueOf(value);
    }

    private Integer toInteger(BigDecimal value) {
        return value == null ? null : value.intValueExact();
    }

    private String trim(String value) {
        return StringUtils.hasText(value) ? value.trim() : value;
    }

    private void notifyTecnicosInteracao(ZeeTAtividadeEntity interacao) {
        Set<String> recipients = resolveTecnicoEmails();
        if (recipients.isEmpty()) {
            log.info("Nenhum email tecnico configurado para notificacao de interacao {}.", interacao.getId());
            return;
        }

        String tipoInteracao = resolveTipoInteracao(interacao.getTipoInteracao());
        String estadoInteracao = resolveEstadoInteracao(interacao.getDmEstadoInteracao());
        String subject = "Nova interação submetida - Interação nº " + interacao.getId();
        String body = "Caro Técnico da AZEEMSV, informamos que foi submetida uma nova interação"
            + " do tipo " + firstText(tipoInteracao, "não informado")
            + ". Assunto: " + firstText(interacao.getAssuntoInteracao(), "não informado")
            + ". Nome: " + firstText(interacao.getNome(), "não informado")
            + ". Email: " + firstText(interacao.getEmail(), "não informado")
            + ". Telefone: " + firstText(interacao.getTelefone(), "não informado")
            + ". Estado atual: " + firstText(estadoInteracao, ESTADO_INTERACAO_SUBMETIDO)
            + ". Por favor consulte a lista de interações para mais informações.";

        for (String recipient : recipients) {
            sendEmailSafely(recipient, subject, body, interacao.getId(), "tecnico");
        }
    }

    private void notifyRequerenteInteracao(ZeeTAtividadeEntity interacao) {
        if (!StringUtils.hasText(interacao.getEmail())) {
            log.info("Email não informado para notificação de confirmação da interação {}.", interacao.getId());
            return;
        }

        String tipoInteracao = resolveTipoInteracao(interacao.getTipoInteracao());
        String estadoInteracao = resolveEstadoInteracao(interacao.getDmEstadoInteracao());
        String subject = "Interação submetida com sucesso";
        String body = "Caro(a) " + firstText(interacao.getNome(), "utilizador")
            + ", informamos que a sua interação foi submetida com sucesso."
            + " Número da interação: " + interacao.getId()
            + ". Tipo: " + firstText(tipoInteracao, "não informado")
            + ". Assunto: " + firstText(interacao.getAssuntoInteracao(), "não informado")
            + ". Estado atual: " + firstText(estadoInteracao, ESTADO_INTERACAO_SUBMETIDO)
            + ". A equipa da AZEEMSV analisará o pedido e responderá oportunamente.";

        sendEmailSafely(interacao.getEmail(), subject, body, interacao.getId(), "requerente");
    }

    private boolean sendEmailSafely(String recipient, String subject, String body, Integer interacaoId, String tipoDestinatario) {
        try {
            emailService.sendText(recipient, subject, body);
            return true;
        } catch (RuntimeException ex) {
            log.error("Erro ao enviar notificacao de interacao {} para {} {}.", interacaoId, tipoDestinatario, recipient, ex);
            return false;
        }
    }

    private Set<String> resolveTecnicoEmails() {
        Set<String> emails = new LinkedHashSet<>();
        paramReportRepository.findAll().stream()
            .map(ZeeTParamReportEntity::getEmail)
            .filter(StringUtils::hasText)
            .map(String::trim)
            .forEach(emails::add);
        emailsRepository.findAll().stream()
            .map(ZeeTEmailsEntity::getEmail)
            .filter(StringUtils::hasText)
            .map(String::trim)
            .forEach(emails::add);
        return emails;
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String resolveTipoInteracao(String tipoInteracao) {
        return firstText(domainHelper.describe(DomainDescriptionHelper.TIPO_INTERACAO, tipoInteracao), tipoInteracao);
    }

    private String resolveEstadoInteracao(String estadoInteracao) {
        return firstText(domainHelper.describe(DomainDescriptionHelper.ESTADO_INTERACAO, estadoInteracao), estadoInteracao);
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
