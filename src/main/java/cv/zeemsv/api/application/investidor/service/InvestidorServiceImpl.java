package cv.zeemsv.api.application.investidor.service;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import cv.zeemsv.api.application.audit.entity.ChangeLogs;
import cv.zeemsv.api.application.audit.service.ChangeLogsService;
import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.investidor.dto.DashboardAlertasDTO;
import cv.zeemsv.api.application.investidor.dto.DashboardAuditoriaDTO;
import cv.zeemsv.api.application.investidor.dto.DashboardCountDTO;
import cv.zeemsv.api.application.investidor.dto.DashboardPagamentosDTO;
import cv.zeemsv.api.application.investidor.dto.DashboardTaxaItemDTO;
import cv.zeemsv.api.application.investidor.dto.DashboardTaxasDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorDashboardResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorDocumentoResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorUserResponseDTO;
import cv.zeemsv.api.domain.documento.business.DocumentViewerUrlService;
import cv.zeemsv.api.domain.documento.business.DocumentoBus;
import cv.zeemsv.api.application.investidor.mapper.InvestidorDtoMapper;
import cv.zeemsv.api.domain.investidor.business.InvestidorBus;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.repository.InvestidorDashboardRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTDocRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.projection.DashboardCountProjection;
import cv.zeemsv.api.infrastructure.repository.projection.DashboardTaxaProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import org.bson.conversions.Bson;

@Service
@RequiredArgsConstructor
public class InvestidorServiceImpl implements InvestidorService {
    private final InvestidorBus bus;
    private final InvestidorDtoMapper mapper;
    private final DomainDescriptionHelper domainHelper;
    private final ZeeTDocRelacaoRepository docRelacaoRepository;
    private final InvestidorDashboardRepository dashboardRepository;
    private final DocumentViewerUrlService documentViewerUrlService;
    private final ChangeLogsService changeLogsService;

    @Override @Transactional
    public InvestidorResponseDTO create(InvestidorRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public InvestidorResponseDTO update(Integer id, InvestidorRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public InvestidorResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<InvestidorResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional(readOnly = true)
    public List<InvestidorUserResponseDTO> findByUserEmail(String email) {
        return bus.findByUserEmail(email).stream().map(this::toUserResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvestidorDocumentoResponseDTO> findDocumentosByInvestidorId(Integer idInvestidor) {
        if (idInvestidor == null) {
            throw new BusinessException("Informe o id do investidor.");
        }
        bus.findById(idInvestidor);
        return docRelacaoRepository.findDocumentosByInvestidorId(idInvestidor)
            .stream()
            .map(this::toDocumentoResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InvestidorDashboardResponseDTO getDashboard(Integer idInvestidor, Integer ano, Integer mes) {
        if (idInvestidor == null) {
            throw new BusinessException("Informe o id do investidor.");
        }
        validateDashboardPeriod(ano, mes);
        bus.findById(idInvestidor);

        InvestidorDashboardResponseDTO dto = new InvestidorDashboardResponseDTO();
        dto.setIdInvestidor(idInvestidor);
        dto.setTotalLote(defaultZero(dashboardRepository.countLotesAtivos(idInvestidor, ano, mes)));
        dto.setTotalLoteReservado(defaultZero(dashboardRepository.countLotesReservados(idInvestidor, ano, mes)));
        dto.setTotalInvestimento(formatCurrencyEcv(dashboardRepository.sumValorComercialLotesAtivos(idInvestidor, ano, mes)));
        dto.setTotalProjeto(defaultZero(dashboardRepository.countProjetos(idInvestidor, ano, mes)));
        dto.setProjetoPorSituacao(toDashboardCounts(
            dashboardRepository.countProjetosPorSituacao(idInvestidor, ano, mes),
            DomainDescriptionHelper.SITUACAO_PROJ
        ));
        dto.setTotalProcesso(defaultZero(dashboardRepository.countProcessos(idInvestidor, ano, mes)));
        dto.setProcessoPorEstado(toDashboardCounts(
            dashboardRepository.countProcessosPorEstado(idInvestidor, ano, mes),
            DomainDescriptionHelper.ESTADO_PROC_SOLICIT
        ));
        dto.setProcessoPorEtapa(toDashboardCounts(
            dashboardRepository.countProcessosPorEtapa(idInvestidor, ano, mes),
            null
        ));
        dto.setAlertas(toAlertas(idInvestidor, ano, mes));
        dto.setTaxasEmAtraso(toTaxasEmAtraso(idInvestidor, ano, mes));
        dto.setPagamentos(toPagamentos(idInvestidor, ano, mes));
        dto.setUltimasAuditorias(findUltimasAuditorias(idInvestidor, ano, mes));
        return dto;
    }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }

    private InvestidorDocumentoResponseDTO toDocumentoResponse(cv.zeemsv.api.infrastructure.repository.projection.InvestidorDocumentoProjection projection) {
        InvestidorDocumentoResponseDTO dto = new InvestidorDocumentoResponseDTO();
        dto.setId(projection.getId());
        dto.setTipoRelacao(projection.getTipoRelacao());
        dto.setTipoRelacaoDesc(resolveTipoRelacaoDesc(projection.getTipoRelacao()));
        dto.setIdRelacao(projection.getIdRelacao());
        dto.setObjetoDescricao(projection.getObjetoDescricao());
        dto.setIdDoc(projection.getIdDoc());
        dto.setIdTpDoc(projection.getIdTpDoc());
        dto.setNomeDocumento(projection.getNomeDocumento());
        dto.setEstado(projection.getEstado());
        dto.setEstadoDesc(resolveEstadoDesc(projection.getEstado()));
        dto.setDateCreate(projection.getDateCreate());
        dto.setUserCreate(projection.getUserCreate());
        dto.setPath(projection.getPath());
        dto.setUrl(StringUtils.hasText(projection.getPath()) ? documentViewerUrlService.toViewerUrl(projection.getPath(), projection.getMimetype()) : null);
        dto.setNomeFicheiro(removeExtension(DocumentoBus.getFileNameWithExtensionByPath(projection.getPath())));
        dto.setDocSize(projection.getDocSize());
        dto.setMimetype(projection.getMimetype());
        dto.setDescricao(projection.getDescricao());
        return dto;
    }

    private List<DashboardCountDTO> toDashboardCounts(List<DashboardCountProjection> projections, String dominio) {
        return projections.stream()
            .map(projection -> new DashboardCountDTO(
                projection.getCodigo(),
                firstText(domainHelper.describe(dominio, projection.getCodigo()), projection.getCodigo()),
                defaultZero(projection.getTotal())
            ))
            .toList();
    }

    private DashboardAlertasDTO toAlertas(Integer idInvestidor, Integer ano, Integer mes) {
        DashboardAlertasDTO dto = new DashboardAlertasDTO();
        dto.setCritico(0L);
        dto.setAtencao(defaultZero(dashboardRepository.countAgendamentosPendentes(idInvestidor, ano, mes)));
        dto.setInformativos(defaultZero(dashboardRepository.countNotificacoesNaoLidas(idInvestidor, ano, mes)));
        return dto;
    }

    private DashboardPagamentosDTO toPagamentos(Integer idInvestidor, Integer ano, Integer mes) {
        DashboardPagamentosDTO dto = new DashboardPagamentosDTO();
        dto.setTotalPagamentos(formatCurrencyEcv(dashboardRepository.sumPagamentos(idInvestidor, ano, mes)));
        dto.setPagamentosPendentes(defaultZero(dashboardRepository.countPagamentosPendentes(idInvestidor, ano, mes)));
        return dto;
    }

    private DashboardTaxasDTO toTaxasEmAtraso(Integer idInvestidor, Integer ano, Integer mes) {
        DashboardTaxasDTO dto = new DashboardTaxasDTO();
        dto.setTotalEmAtraso(formatCurrencyEcv(dashboardRepository.sumTaxasEmAtraso(idInvestidor, ano, mes)));
        dto.setTaxas(dashboardRepository.findTaxasEmAtraso(idInvestidor, ano, mes).stream()
            .map(this::toTaxaItem)
            .toList());
        return dto;
    }

    private DashboardTaxaItemDTO toTaxaItem(DashboardTaxaProjection projection) {
        return new DashboardTaxaItemDTO(
            firstText(projection.getDescricao(), "Taxa sem descricao"),
            formatCurrencyEcv(projection.getValor())
        );
    }

    private List<DashboardAuditoriaDTO> findUltimasAuditorias(Integer idInvestidor, Integer ano, Integer mes) {
        List<Bson> relationFilters = new ArrayList<>();
        relationFilters.add(Filters.and(
            Filters.eq("tableName", "zee_t_investidor"),
            Filters.eq("tableId", String.valueOf(idInvestidor))
        ));
        addTableIdFilter(relationFilters, "zee_t_solicitacao", dashboardRepository.findSolicitacaoAuditTableIds(idInvestidor));
        addTableIdFilter(relationFilters, "zee_t_pagamento", dashboardRepository.findPagamentoAuditTableIds(idInvestidor));
        addTableIdFilter(relationFilters, "zee_t_cobranca", dashboardRepository.findCobrancaAuditTableIds(idInvestidor));

        List<Bson> filters = new ArrayList<>();
        filters.add(Filters.or(relationFilters));
        buildAuditPeriodFilter(ano, mes).stream().forEach(filters::add);

        try {
            return changeLogsService.filterLogs(5, Filters.and(filters), Sorts.descending("date")).stream()
                .map(this::toAuditoria)
                .toList();
        } catch (RuntimeException ex) {
            return List.of();
        }
    }

    private void addTableIdFilter(List<Bson> filters, String tableName, List<String> ids) {
        List<String> safeIds = ids == null ? List.of() : ids.stream()
            .filter(StringUtils::hasText)
            .distinct()
            .toList();
        if (!safeIds.isEmpty()) {
            filters.add(Filters.and(
                Filters.eq("tableName", tableName),
                Filters.in("tableId", safeIds)
            ));
        }
    }

    private List<Bson> buildAuditPeriodFilter(Integer ano, Integer mes) {
        if (ano == null) {
            return List.of();
        }
        LocalDateTime start = mes == null
            ? LocalDateTime.of(ano, Month.JANUARY, 1, 0, 0)
            : LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime end = mes == null ? start.plusYears(1) : start.plusMonths(1);
        return List.of(Filters.gte("date", start), Filters.lt("date", end));
    }

    private DashboardAuditoriaDTO toAuditoria(ChangeLogs log) {
        return new DashboardAuditoriaDTO(
            log.getDate(),
            firstText(log.getUserEmail(), log.getUserId(), "Sistema"),
            buildAcaoRealizada(log),
            log.getAction(),
            log.getTableName(),
            log.getTableId()
        );
    }

    private String buildAcaoRealizada(ChangeLogs log) {
        return firstText(log.getObs(), log.getAction() + " " + log.getTableName(), "Atividade registada");
    }

    private void validateDashboardPeriod(Integer ano, Integer mes) {
        if (ano != null && ano < 1) {
            throw new BusinessException("Informe um ano valido.");
        }
        if (mes != null && (mes < 1 || mes > 12)) {
            throw new BusinessException("Informe um mes valido entre 1 e 12.");
        }
    }

    private Long defaultZero(Long value) {
        return value != null ? value : 0L;
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }

    private String formatCurrencyEcv(BigDecimal value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');

        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(defaultZero(value)) + " ECV";
    }

    private String resolveTipoRelacaoDesc(String tipoRelacao) {
        return firstText(
            domainHelper.describe(DomainDescriptionHelper.OBJECTO, tipoRelacao),
            domainHelper.describe(DomainDescriptionHelper.TIPO_OBJETO, tipoRelacao),
            defaultTipoRelacaoDesc(tipoRelacao),
            tipoRelacao
        );
    }

    private String resolveEstadoDesc(String estado) {
        return firstText(
            domainHelper.describe(DomainDescriptionHelper.ESTADO, estado),
            defaultEstadoDesc(estado),
            estado
        );
    }

    private String defaultTipoRelacaoDesc(String tipoRelacao) {
        if (!StringUtils.hasText(tipoRelacao)) {
            return null;
        }
        return switch (tipoRelacao.trim().toUpperCase()) {
            case "INVESTIDOR" -> "Investidor";
            case "INVESTIDOR_SINGULAR" -> "Investidor singular";
            case "SOLICITACAO" -> "Solicitação";
            case "NOTIFICACAO" -> "Notificação";
            case "ATIVIDADE" -> "Atividade";
            case "PROJETO" -> "Projeto";
            default -> null;
        };
    }

    private String defaultEstadoDesc(String estado) {
        if (!StringUtils.hasText(estado)) {
            return null;
        }
        return switch (estado.trim().toUpperCase()) {
            case "A", "ATIVO" -> "Ativo";
            case "I", "INATIVO" -> "Inativo";
            case "ELIMINADO" -> "Eliminado";
            case "PENDENTE" -> "Pendente";
            default -> null;
        };
    }

    private String removeExtension(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return fileName;
        }
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private InvestidorUserResponseDTO toUserResponse(cv.zeemsv.api.domain.investidor.model.InvestidorUser model) {
        InvestidorUserResponseDTO dto = mapper.toUserResponse(model);
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, dto.getDmEstado()));
        dto.setDmTipoInvestidorDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_INVESTIDOR, dto.getDmTipoInvestidor()));
        dto.setDmTpRepresentanteDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_REPRESENTANTE, dto.getDmTpRepresentante()));
        dto.setDmPrincipalDesc(domainHelper.describe(DomainDescriptionHelper.SIM_NAO, dto.getDmPrincipal()));
        return dto;
    }
}
