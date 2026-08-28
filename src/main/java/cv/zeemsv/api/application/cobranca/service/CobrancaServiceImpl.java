package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.application.audit.dto.AuditContext;
import cv.zeemsv.api.application.audit.entity.ChangeLogsItem;
import cv.zeemsv.api.application.audit.service.ChangeLogsService;
import cv.zeemsv.api.application.cobranca.dto.CobrancaInvestidorResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaPrestacaoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaTaxaResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CriarPagamentoRequestDTO;
import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaPrestacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTaxaEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaPrestacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicTaxaRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CobrancaServiceImpl implements CobrancaService {
    private static final String ESTADO_ATIVO = "A";
    private static final String ESTADO_PENDENTE = "PENDENTE";
    private static final String ESTADO_PAGO = "PAGO";

    private final ZeeTCobrancaRepository cobrancaRepository;
    private final ZeeTCobrancaPrestacaoRepository prestacaoRepository;
    private final ZeeTCobrancaTaxaRepository cobrancaTaxaRepository;
    private final ZeeTPagamentoRepository pagamentoRepository;
    private final ZeeTPagamentoTaxaRepository pagamentoTaxaRepository;
    private final ZeeTSolicitacaoCobrancaRepository solicitacaoCobrancaRepository;
    private final ZeeTSolicitacaoTaxaRepository solicitacaoTaxaRepository;
    private final ZeeTTpSolicTaxaRepository tpSolicTaxaRepository;
    private final ZeeTTaxaRepository taxaRepository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final DomainDescriptionHelper domainHelper;
    private final ChangeLogsService changeLogsService;

    @Override
    @Transactional
    public CobrancaPagamentoResponseDTO criarPagamento(CriarPagamentoRequestDTO dto) {
        ZeeTCobrancaEntity cobranca = cobrancaRepository.findById(dto.getIdCobranca())
            .orElseThrow(() -> new BusinessException("Cobranca nao encontrada: " + dto.getIdCobranca()));
        if (dto.getValor() == null || dto.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("O valor do pagamento deve ser maior que zero.");
        }
        BigDecimal dividaAtual = calcularDividaAtual(cobranca);
        if (dto.getValor().compareTo(dividaAtual) > 0) {
            throw new BusinessException("O valor do pagamento nao pode ser superior a divida atual da cobranca.");
        }

        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas = cobrancaTaxaRepository.findByIdCobrancaOrderByIdAsc(cobranca.getId());
        ZeeTSolicitacaoTaxaEntity solicTaxa = findSolicTaxa(cobranca.getIdSolicTaxa());
        ZeeTPagamentoEntity pagamento = buildPagamento(dto, cobranca, solicTaxa, cobrancaTaxas);
        pagamento = pagamentoRepository.save(pagamento);

        List<ZeeTPagamentoTaxaEntity> pagamentoTaxas = createPagamentoTaxas(pagamento, cobrancaTaxas, dto);
        atualizarCobrancaAposPagamento(cobranca, cobrancaTaxas);
        auditPagamentoCriado(pagamento, dto);

        Map<Integer, ZeeTTaxaEntity> taxasPorId = findTaxas(Collections.emptyMap(), Collections.emptyList(), pagamentoTaxas);
        return toPagamentoResponse(pagamento, pagamentoTaxas, taxasPorId);
    }

    private ZeeTPagamentoEntity buildPagamento(
        CriarPagamentoRequestDTO dto,
        ZeeTCobrancaEntity cobranca,
        ZeeTSolicitacaoTaxaEntity solicTaxa,
        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas
    ) {
        ZeeTPagamentoEntity pagamento = new ZeeTPagamentoEntity();
        pagamento.setIdSolicitacao(cobranca.getIdSolicitacao());
        pagamento.setIdCobranca(cobranca.getId());
        pagamento.setIdPrestacao(dto.getIdPrestacao());
        pagamento.setIdSolicTaxa(cobranca.getIdSolicTaxa());
        pagamento.setIdTpSolicTaxa(solicTaxa != null ? solicTaxa.getIdTpSolicTaxa() : null);
        pagamento.setIdInvestidor(cobranca.getIdInvestidor());
        pagamento.setIdPromotor(solicTaxa != null ? solicTaxa.getIdPromotor() : null);
        pagamento.setIdProjeto(cobranca.getIdProjeto());
        pagamento.setIdProcesso(cobranca.getNrProcesso());
        pagamento.setNrProcesso(cobranca.getNrProcesso() != null ? cobranca.getNrProcesso().toString() : null);
        pagamento.setValor(dto.getValor());
        pagamento.setValorPago(dto.getValor().toPlainString());
        pagamento.setDataPagamento(java.time.LocalDate.now());
        pagamento.setDataRegisto(java.time.LocalDate.now());
        pagamento.setUserRegisto(firstText(dto.getUserRegisto(), dto.getUserPagamento(), "system"));
        pagamento.setUserPagamento(firstText(dto.getUserPagamento(), dto.getUserRegisto(), "system"));
        pagamento.setDmEstadoPag(ESTADO_PAGO);
        pagamento.setDmEstado(ESTADO_ATIVO);
        pagamento.setEntidade(dto.getEntidade());
        pagamento.setReferencia(dto.getReferencia());
        pagamento.setDuc(dto.getDuc());
        pagamento.setFormaPagamento(dto.getFormaPagamento());
        pagamento.setOrigemPagamento(dto.getOrigemPagamento());
        pagamento.setNrCheque(dto.getNrCheque());
        pagamento.setNumCheque(dto.getNumCheque());
        pagamento.setBanco(dto.getBanco());
        pagamento.setLinkDuc(dto.getLinkDuc());
        pagamento.setFlagIntegracao(dto.getFlagIntegracao());
        pagamento.setDataIntegracao(dto.getDataIntegracao());
        pagamento.setUserIntegracao(dto.getUserIntegracao());
        pagamento.setIdTaxa(resolvePagamentoIdTaxa(cobrancaTaxas));
        return pagamento;
    }

    private List<ZeeTPagamentoTaxaEntity> createPagamentoTaxas(
        ZeeTPagamentoEntity pagamento,
        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas,
        CriarPagamentoRequestDTO dto
    ) {
        BigDecimal totalTaxas = cobrancaTaxas.stream()
            .map(ZeeTCobrancaTaxaEntity::getValor)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAlocado = BigDecimal.ZERO;
        List<ZeeTPagamentoTaxaEntity> pagamentoTaxas = new ArrayList<>();
        for (int i = 0; i < cobrancaTaxas.size(); i++) {
            ZeeTCobrancaTaxaEntity cobrancaTaxa = cobrancaTaxas.get(i);
            BigDecimal valorTaxa = resolveValorPagamentoTaxa(dto.getValor(), cobrancaTaxa, totalTaxas, totalAlocado, i == cobrancaTaxas.size() - 1);
            totalAlocado = totalAlocado.add(valorTaxa);
            ZeeTPagamentoTaxaEntity pagamentoTaxa = new ZeeTPagamentoTaxaEntity();
            pagamentoTaxa.setIdPagamento(pagamento.getId());
            pagamentoTaxa.setIdTaxa(cobrancaTaxa.getIdTaxa());
            pagamentoTaxa.setIdTaxaCond(cobrancaTaxa.getIdTaxaCond());
            pagamentoTaxa.setValor(valorTaxa);
            pagamentoTaxa.setDmEstado(ESTADO_PAGO);
            pagamentoTaxa.setUserRegisto(firstText(dto.getUserRegisto(), dto.getUserPagamento(), "system"));
            pagamentoTaxa.setDataRegisto(java.time.LocalDate.now());
            pagamentoTaxas.add(pagamentoTaxaRepository.save(pagamentoTaxa));
        }
        return pagamentoTaxas;
    }

    private BigDecimal resolveValorPagamentoTaxa(
        BigDecimal valorPagamento,
        ZeeTCobrancaTaxaEntity cobrancaTaxa,
        BigDecimal totalTaxas,
        BigDecimal totalAlocado,
        boolean ultimaTaxa
    ) {
        if (ultimaTaxa) {
            return valorPagamento.subtract(totalAlocado);
        }
        if (totalTaxas.compareTo(BigDecimal.ZERO) == 0 || cobrancaTaxa.getValor() == null) {
            return BigDecimal.ZERO;
        }
        return valorPagamento
            .multiply(cobrancaTaxa.getValor())
            .divide(totalTaxas, 2, RoundingMode.HALF_UP);
    }

    private void atualizarCobrancaAposPagamento(
        ZeeTCobrancaEntity cobranca,
        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas
    ) {
        BigDecimal total = parseMoney(cobranca.getValorTotal());
        BigDecimal pagoAtual = calcularTotalPago(cobranca.getId());
        BigDecimal divida = total.subtract(pagoAtual).max(BigDecimal.ZERO);
        boolean pago = divida.compareTo(BigDecimal.ZERO) == 0;

        cobranca.setValorPago(pagoAtual.toPlainString());
        cobranca.setValorDivida(divida.toPlainString());
        cobranca.setDmEstado(pago ? ESTADO_PAGO : ESTADO_PENDENTE);
        cobrancaRepository.save(cobranca);

        if (pago && !cobrancaTaxas.isEmpty()) {
            cobrancaTaxas.forEach(cobrancaTaxa -> cobrancaTaxa.setDmEstado(ESTADO_PAGO));
            cobrancaTaxaRepository.saveAll(cobrancaTaxas);
        }
    }

    private BigDecimal calcularDividaAtual(ZeeTCobrancaEntity cobranca) {
        BigDecimal total = parseMoney(cobranca.getValorTotal());
        return total.subtract(calcularTotalPago(cobranca.getId())).max(BigDecimal.ZERO);
    }

    private BigDecimal calcularTotalPago(Integer idCobranca) {
        return pagamentoRepository.findByIdCobrancaInOrderByIdCobrancaAscDataPagamentoDescIdDesc(List.of(idCobranca))
            .stream()
            .map(this::resolveValorPago)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ZeeTSolicitacaoTaxaEntity findSolicTaxa(Integer idSolicTaxa) {
        if (idSolicTaxa == null) {
            return null;
        }
        return solicitacaoTaxaRepository.findById(idSolicTaxa).orElse(null);
    }

    private BigDecimal resolveValorPago(ZeeTPagamentoEntity pagamento) {
        if (pagamento.getValorPago() != null && !pagamento.getValorPago().isBlank()) {
            return parseMoney(pagamento.getValorPago());
        }
        return pagamento.getValor() != null ? pagamento.getValor() : BigDecimal.ZERO;
    }

    private BigDecimal parseMoney(String value) {
        if (value == null || value.isBlank()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new BusinessException("Valor monetario invalido: " + value);
        }
    }

    private BigDecimal resolvePagamentoIdTaxa(List<ZeeTCobrancaTaxaEntity> cobrancaTaxas) {
        if (cobrancaTaxas.size() != 1 || cobrancaTaxas.get(0).getIdTaxa() == null) {
            return null;
        }
        return BigDecimal.valueOf(cobrancaTaxas.get(0).getIdTaxa());
    }

    private void auditPagamentoCriado(ZeeTPagamentoEntity pagamento, CriarPagamentoRequestDTO dto) {
        changeLogsService.createLogsAsyncSafe(
            List.of(
                logItem("id_cobranca", null, pagamento.getIdCobranca()),
                logItem("valor", null, pagamento.getValor()),
                logItem("referencia", null, pagamento.getReferencia()),
                logItem("duc", null, pagamento.getDuc())
            ),
            "CREATE",
            "zee_t_pagamento",
            String.valueOf(pagamento.getId()),
            "Pagamento registado",
            AuditContext.builder()
                .userId(firstText(dto.getUserPagamento(), dto.getUserRegisto()))
                .userEmail(firstText(dto.getUserPagamento(), dto.getUserRegisto()))
                .build()
        );
    }

    private ChangeLogsItem logItem(String column, Object oldValue, Object newValue) {
        ChangeLogsItem item = new ChangeLogsItem();
        item.setColumn(column);
        item.setOldValue(oldValue);
        item.setNewValue(newValue);
        return item;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CobrancaInvestidorResponseDTO> findByInvestidorId(Integer idInvestidor) {
        if (idInvestidor == null) {
            throw new BusinessException("Informe o id do investidor.");
        }
        if (!investidorRepository.existsById(idInvestidor)) {
            throw new BusinessException("Investidor nao encontrado: " + idInvestidor);
        }

        List<ZeeTCobrancaEntity> cobrancas = cobrancaRepository.findByIdInvestidorOrderByDataEmissaoDescIdDesc(idInvestidor);
        if (cobrancas.isEmpty()) {
            return Collections.emptyList();
        }

        List<Integer> idsCobranca = cobrancas.stream().map(ZeeTCobrancaEntity::getId).toList();
        List<ZeeTCobrancaPrestacaoEntity> prestacoes = prestacaoRepository.findByIdCobrancaInOrderByIdCobrancaAscIdAsc(idsCobranca);
        List<ZeeTPagamentoEntity> pagamentos = findPagamentos(idsCobranca, prestacoes);
        List<ZeeTSolicitacaoCobrancaEntity> relacoes = solicitacaoCobrancaRepository.findByIdCobrancaIn(idsCobranca);
        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas = cobrancaTaxaRepository.findByIdCobrancaInOrderByIdCobrancaAscIdAsc(idsCobranca);
        List<ZeeTPagamentoTaxaEntity> pagamentoTaxas = findPagamentoTaxas(pagamentos);

        Map<Integer, List<ZeeTCobrancaPrestacaoEntity>> prestacoesPorCobranca = prestacoes.stream()
            .filter(prestacao -> prestacao.getIdCobranca() != null)
            .collect(Collectors.groupingBy(ZeeTCobrancaPrestacaoEntity::getIdCobranca));
        Map<Integer, List<ZeeTPagamentoEntity>> pagamentosPorCobranca = pagamentos.stream()
            .filter(pagamento -> pagamento.getIdCobranca() != null)
            .collect(Collectors.groupingBy(ZeeTPagamentoEntity::getIdCobranca));
        Map<Integer, List<ZeeTPagamentoEntity>> pagamentosPorPrestacao = pagamentos.stream()
            .filter(pagamento -> pagamento.getIdPrestacao() != null)
            .collect(Collectors.groupingBy(ZeeTPagamentoEntity::getIdPrestacao));
        Map<Integer, List<ZeeTSolicitacaoCobrancaEntity>> relacoesPorCobranca = relacoes.stream()
            .filter(relacao -> relacao.getIdCobranca() != null)
            .collect(Collectors.groupingBy(ZeeTSolicitacaoCobrancaEntity::getIdCobranca));
        Map<Integer, List<ZeeTCobrancaTaxaEntity>> taxasPorCobranca = cobrancaTaxas.stream()
            .filter(cobrancaTaxa -> cobrancaTaxa.getIdCobranca() != null)
            .collect(Collectors.groupingBy(ZeeTCobrancaTaxaEntity::getIdCobranca));
        Map<Integer, List<ZeeTPagamentoTaxaEntity>> taxasPorPagamento = pagamentoTaxas.stream()
            .filter(pagamentoTaxa -> pagamentoTaxa.getIdPagamento() != null)
            .collect(Collectors.groupingBy(ZeeTPagamentoTaxaEntity::getIdPagamento));

        Map<Integer, ZeeTSolicitacaoTaxaEntity> solicTaxasPorId = findSolicTaxas(cobrancas);
        Map<Integer, ZeeTTpSolicTaxaEntity> tpSolicTaxasPorId = findTpSolicTaxas(solicTaxasPorId);
        Map<Integer, ZeeTTaxaEntity> taxasPorId = findTaxas(tpSolicTaxasPorId, cobrancaTaxas, pagamentoTaxas);

        return cobrancas.stream()
            .map(cobranca -> toResponse(
                cobranca,
                relacoesPorCobranca.getOrDefault(cobranca.getId(), Collections.emptyList()),
                prestacoesPorCobranca.getOrDefault(cobranca.getId(), Collections.emptyList()),
                pagamentosPorCobranca.getOrDefault(cobranca.getId(), Collections.emptyList()),
                pagamentosPorPrestacao,
                taxasPorCobranca.getOrDefault(cobranca.getId(), Collections.emptyList()),
                taxasPorPagamento,
                solicTaxasPorId,
                tpSolicTaxasPorId,
                taxasPorId
            ))
            .toList();
    }

    private List<ZeeTPagamentoTaxaEntity> findPagamentoTaxas(List<ZeeTPagamentoEntity> pagamentos) {
        List<Integer> idsPagamento = pagamentos.stream()
            .map(ZeeTPagamentoEntity::getId)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        return idsPagamento.isEmpty()
            ? Collections.emptyList()
            : pagamentoTaxaRepository.findByIdPagamentoInOrderByIdPagamentoAscIdAsc(idsPagamento);
    }

    private List<ZeeTPagamentoEntity> findPagamentos(List<Integer> idsCobranca, List<ZeeTCobrancaPrestacaoEntity> prestacoes) {
        List<ZeeTPagamentoEntity> pagamentos = new ArrayList<>(pagamentoRepository.findByIdCobrancaInOrderByIdCobrancaAscDataPagamentoDescIdDesc(idsCobranca));
        List<Integer> idsPrestacao = prestacoes.stream()
            .map(ZeeTCobrancaPrestacaoEntity::getId)
            .filter(Objects::nonNull)
            .toList();
        if (!idsPrestacao.isEmpty()) {
            pagamentos.addAll(pagamentoRepository.findByIdPrestacaoInOrderByIdPrestacaoAscDataPagamentoDescIdDesc(idsPrestacao));
        }
        return pagamentos.stream()
            .collect(Collectors.toMap(ZeeTPagamentoEntity::getId, Function.identity(), (left, right) -> left))
            .values()
            .stream()
            .toList();
    }

    private Map<Integer, ZeeTSolicitacaoTaxaEntity> findSolicTaxas(List<ZeeTCobrancaEntity> cobrancas) {
        List<Integer> idsSolicTaxa = cobrancas.stream()
            .map(ZeeTCobrancaEntity::getIdSolicTaxa)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        return idsSolicTaxa.isEmpty()
            ? Collections.emptyMap()
            : solicitacaoTaxaRepository.findAllById(idsSolicTaxa).stream()
                .collect(Collectors.toMap(ZeeTSolicitacaoTaxaEntity::getId, Function.identity()));
    }

    private Map<Integer, ZeeTTpSolicTaxaEntity> findTpSolicTaxas(Map<Integer, ZeeTSolicitacaoTaxaEntity> solicTaxasPorId) {
        List<Integer> idsTpSolicTaxa = solicTaxasPorId.values().stream()
            .map(ZeeTSolicitacaoTaxaEntity::getIdTpSolicTaxa)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        return idsTpSolicTaxa.isEmpty()
            ? Collections.emptyMap()
            : tpSolicTaxaRepository.findAllById(idsTpSolicTaxa).stream()
                .collect(Collectors.toMap(ZeeTTpSolicTaxaEntity::getId, Function.identity()));
    }

    private Map<Integer, ZeeTTaxaEntity> findTaxas(
        Map<Integer, ZeeTTpSolicTaxaEntity> tpSolicTaxasPorId,
        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas,
        List<ZeeTPagamentoTaxaEntity> pagamentoTaxas
    ) {
        Set<Integer> idsTaxa = new LinkedHashSet<>();
        tpSolicTaxasPorId.values().stream()
            .map(ZeeTTpSolicTaxaEntity::getIdTaxa)
            .filter(Objects::nonNull)
            .forEach(idsTaxa::add);
        cobrancaTaxas.stream()
            .map(ZeeTCobrancaTaxaEntity::getIdTaxa)
            .filter(Objects::nonNull)
            .forEach(idsTaxa::add);
        pagamentoTaxas.stream()
            .map(ZeeTPagamentoTaxaEntity::getIdTaxa)
            .filter(Objects::nonNull)
            .forEach(idsTaxa::add);
        return idsTaxa.isEmpty()
            ? Collections.emptyMap()
            : taxaRepository.findAllById(new ArrayList<>(idsTaxa)).stream()
                .collect(Collectors.toMap(ZeeTTaxaEntity::getId, Function.identity()));
    }

    private CobrancaInvestidorResponseDTO toResponse(
        ZeeTCobrancaEntity cobranca,
        List<ZeeTSolicitacaoCobrancaEntity> relacoes,
        List<ZeeTCobrancaPrestacaoEntity> prestacoes,
        List<ZeeTPagamentoEntity> pagamentos,
        Map<Integer, List<ZeeTPagamentoEntity>> pagamentosPorPrestacao,
        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas,
        Map<Integer, List<ZeeTPagamentoTaxaEntity>> taxasPorPagamento,
        Map<Integer, ZeeTSolicitacaoTaxaEntity> solicTaxasPorId,
        Map<Integer, ZeeTTpSolicTaxaEntity> tpSolicTaxasPorId,
        Map<Integer, ZeeTTaxaEntity> taxasPorId
    ) {
        CobrancaInvestidorResponseDTO dto = new CobrancaInvestidorResponseDTO();
        dto.setId(cobranca.getId());
        dto.setNrCobranca(cobranca.getNrCobranca());
        dto.setIdInvestidor(cobranca.getIdInvestidor());
        dto.setIdProjeto(cobranca.getIdProjeto());
        dto.setNrProcesso(cobranca.getNrProcesso());
        dto.setIdsSolicitacao(resolveIdsSolicitacao(cobranca, relacoes));
        dto.setIdSolicitacao(cobranca.getIdSolicitacao());
        dto.setIdSolicTaxa(cobranca.getIdSolicTaxa());
        dto.setDataEmissao(cobranca.getDataEmissao());
        dto.setDataVencimento(cobranca.getDataVencimento());
        dto.setValorTotal(cobranca.getValorTotal());
        dto.setValorPago(cobranca.getValorPago());
        dto.setValorDivida(cobranca.getValorDivida());
        dto.setTipoLiquidacao(cobranca.getTipoLiquidacao());
        dto.setNrPrestacao(cobranca.getNrPrestacao());
        dto.setDmEstado(cobranca.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, cobranca.getDmEstado()));
        dto.setUserRegisto(cobranca.getUserRegisto());
        dto.setDataRegisto(cobranca.getDataRegisto());
        dto.setTaxa(toTaxaResponse(cobranca, solicTaxasPorId, tpSolicTaxasPorId, taxasPorId));
        dto.setTaxas(resolveCobrancaTaxas(cobranca, cobrancaTaxas, solicTaxasPorId, tpSolicTaxasPorId, taxasPorId));
        dto.setPrestacoes(prestacoes.stream()
            .map(prestacao -> toPrestacaoResponse(
                prestacao,
                pagamentosPorPrestacao.getOrDefault(prestacao.getId(), Collections.emptyList()),
                taxasPorPagamento,
                taxasPorId
            ))
            .toList());
        dto.setPagamentos(pagamentos.stream()
            .map(pagamento -> toPagamentoResponse(pagamento, taxasPorPagamento.getOrDefault(pagamento.getId(), Collections.emptyList()), taxasPorId))
            .toList());
        return dto;
    }

    private List<Integer> resolveIdsSolicitacao(ZeeTCobrancaEntity cobranca, List<ZeeTSolicitacaoCobrancaEntity> relacoes) {
        Set<Integer> idsSolicitacao = new LinkedHashSet<>();
        if (cobranca.getIdSolicitacao() != null) {
            idsSolicitacao.add(cobranca.getIdSolicitacao());
        }
        relacoes.stream()
            .map(ZeeTSolicitacaoCobrancaEntity::getIdSolicitacao)
            .filter(Objects::nonNull)
            .forEach(idsSolicitacao::add);
        return new ArrayList<>(idsSolicitacao);
    }

    private CobrancaTaxaResponseDTO toTaxaResponse(
        ZeeTCobrancaEntity cobranca,
        Map<Integer, ZeeTSolicitacaoTaxaEntity> solicTaxasPorId,
        Map<Integer, ZeeTTpSolicTaxaEntity> tpSolicTaxasPorId,
        Map<Integer, ZeeTTaxaEntity> taxasPorId
    ) {
        ZeeTSolicitacaoTaxaEntity solicTaxa = solicTaxasPorId.get(cobranca.getIdSolicTaxa());
        if (solicTaxa == null) {
            return null;
        }
        ZeeTTpSolicTaxaEntity tpSolicTaxa = tpSolicTaxasPorId.get(solicTaxa.getIdTpSolicTaxa());
        ZeeTTaxaEntity taxa = tpSolicTaxa != null ? taxasPorId.get(tpSolicTaxa.getIdTaxa()) : null;

        CobrancaTaxaResponseDTO dto = new CobrancaTaxaResponseDTO();
        dto.setIdSolicTaxa(solicTaxa.getId());
        dto.setIdTpSolicTaxa(solicTaxa.getIdTpSolicTaxa());
        dto.setIdTaxa(tpSolicTaxa != null ? tpSolicTaxa.getIdTaxa() : null);
        dto.setCodigoTaxa(taxa != null ? taxa.getCodigo() : null);
        dto.setRefFin(taxa != null ? taxa.getRefFin() : null);
        dto.setDescricao(firstText(
            taxa != null ? taxa.getDescricao() : null,
            tpSolicTaxa != null ? tpSolicTaxa.getDescricao() : null
        ));
        dto.setTipoTaxa(tpSolicTaxa != null ? tpSolicTaxa.getTipoTaxa() : null);
        dto.setTipoTaxaDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_TAXA, dto.getTipoTaxa()));
        dto.setValor(solicTaxa.getValor());
        dto.setValorConfigurado(tpSolicTaxa != null ? tpSolicTaxa.getValor() : null);
        return dto;
    }

    private List<CobrancaTaxaResponseDTO> resolveCobrancaTaxas(
        ZeeTCobrancaEntity cobranca,
        List<ZeeTCobrancaTaxaEntity> cobrancaTaxas,
        Map<Integer, ZeeTSolicitacaoTaxaEntity> solicTaxasPorId,
        Map<Integer, ZeeTTpSolicTaxaEntity> tpSolicTaxasPorId,
        Map<Integer, ZeeTTaxaEntity> taxasPorId
    ) {
        List<CobrancaTaxaResponseDTO> taxas = cobrancaTaxas.stream()
            .map(cobrancaTaxa -> toCobrancaTaxaResponse(cobrancaTaxa, taxasPorId))
            .toList();
        if (!taxas.isEmpty()) {
            return taxas;
        }
        CobrancaTaxaResponseDTO taxa = toTaxaResponse(cobranca, solicTaxasPorId, tpSolicTaxasPorId, taxasPorId);
        return taxa == null ? Collections.emptyList() : List.of(taxa);
    }

    private CobrancaTaxaResponseDTO toCobrancaTaxaResponse(ZeeTCobrancaTaxaEntity cobrancaTaxa, Map<Integer, ZeeTTaxaEntity> taxasPorId) {
        ZeeTTaxaEntity taxa = taxasPorId.get(cobrancaTaxa.getIdTaxa());
        CobrancaTaxaResponseDTO dto = new CobrancaTaxaResponseDTO();
        dto.setId(cobrancaTaxa.getId());
        dto.setIdCobranca(cobrancaTaxa.getIdCobranca());
        dto.setIdTaxa(cobrancaTaxa.getIdTaxa());
        dto.setIdTaxaCond(cobrancaTaxa.getIdTaxaCond());
        dto.setCodigoTaxa(taxa != null ? taxa.getCodigo() : null);
        dto.setRefFin(taxa != null ? taxa.getRefFin() : null);
        dto.setDescricao(taxa != null ? taxa.getDescricao() : null);
        dto.setTipoTaxa(taxa != null ? taxa.getTipoTaxa() : null);
        dto.setTipoTaxaDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_TAXA, dto.getTipoTaxa()));
        dto.setValor(cobrancaTaxa.getValor());
        dto.setValorConfigurado(taxa != null ? taxa.getValor() : null);
        dto.setDmEstado(cobrancaTaxa.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, cobrancaTaxa.getDmEstado()));
        dto.setUserRegisto(cobrancaTaxa.getUserRegisto());
        dto.setDataRegisto(cobrancaTaxa.getDataRegisto());
        return dto;
    }

    private CobrancaTaxaResponseDTO toPagamentoTaxaResponse(ZeeTPagamentoTaxaEntity pagamentoTaxa, Map<Integer, ZeeTTaxaEntity> taxasPorId) {
        ZeeTTaxaEntity taxa = taxasPorId.get(pagamentoTaxa.getIdTaxa());
        CobrancaTaxaResponseDTO dto = new CobrancaTaxaResponseDTO();
        dto.setId(pagamentoTaxa.getId());
        dto.setIdPagamento(pagamentoTaxa.getIdPagamento());
        dto.setIdTaxa(pagamentoTaxa.getIdTaxa());
        dto.setIdTaxaCond(pagamentoTaxa.getIdTaxaCond());
        dto.setCodigoTaxa(taxa != null ? taxa.getCodigo() : null);
        dto.setRefFin(taxa != null ? taxa.getRefFin() : null);
        dto.setDescricao(taxa != null ? taxa.getDescricao() : null);
        dto.setTipoTaxa(taxa != null ? taxa.getTipoTaxa() : null);
        dto.setTipoTaxaDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_TAXA, dto.getTipoTaxa()));
        dto.setValor(pagamentoTaxa.getValor());
        dto.setValorConfigurado(taxa != null ? taxa.getValor() : null);
        dto.setDmEstado(pagamentoTaxa.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, pagamentoTaxa.getDmEstado()));
        dto.setUserRegisto(pagamentoTaxa.getUserRegisto());
        dto.setDataRegisto(pagamentoTaxa.getDataRegisto());
        return dto;
    }

    private CobrancaPrestacaoResponseDTO toPrestacaoResponse(
        ZeeTCobrancaPrestacaoEntity prestacao,
        List<ZeeTPagamentoEntity> pagamentos,
        Map<Integer, List<ZeeTPagamentoTaxaEntity>> taxasPorPagamento,
        Map<Integer, ZeeTTaxaEntity> taxasPorId
    ) {
        CobrancaPrestacaoResponseDTO dto = new CobrancaPrestacaoResponseDTO();
        dto.setId(prestacao.getId());
        dto.setIdCobranca(prestacao.getIdCobranca());
        dto.setNrPrestacao(prestacao.getNrPrestacao());
        dto.setValor(prestacao.getValor());
        dto.setDataVencimento(prestacao.getDataVencimento());
        dto.setDmEstado(prestacao.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, prestacao.getDmEstado()));
        dto.setUserRegisto(prestacao.getUserRegisto());
        dto.setDataRegisto(prestacao.getDataRegisto());
        dto.setPagamentos(pagamentos.stream()
            .map(pagamento -> toPagamentoResponse(pagamento, taxasPorPagamento.getOrDefault(pagamento.getId(), Collections.emptyList()), taxasPorId))
            .toList());
        return dto;
    }

    private CobrancaPagamentoResponseDTO toPagamentoResponse(
        ZeeTPagamentoEntity pagamento,
        List<ZeeTPagamentoTaxaEntity> pagamentoTaxas,
        Map<Integer, ZeeTTaxaEntity> taxasPorId
    ) {
        CobrancaPagamentoResponseDTO dto = new CobrancaPagamentoResponseDTO();
        dto.setId(pagamento.getId());
        dto.setIdSolicitacao(pagamento.getIdSolicitacao());
        dto.setIdCobranca(pagamento.getIdCobranca());
        dto.setIdPrestacao(pagamento.getIdPrestacao());
        dto.setIdTpSolicTaxa(pagamento.getIdTpSolicTaxa());
        dto.setIdInvestidor(pagamento.getIdInvestidor());
        dto.setIdPromotor(pagamento.getIdPromotor());
        dto.setIdProjeto(pagamento.getIdProjeto());
        dto.setIdProcesso(pagamento.getIdProcesso());
        dto.setValor(pagamento.getValor());
        dto.setValorPago(pagamento.getValorPago());
        dto.setNrProcesso(pagamento.getNrProcesso());
        dto.setEntidade(pagamento.getEntidade());
        dto.setReferencia(pagamento.getReferencia());
        dto.setDuc(pagamento.getDuc());
        dto.setDmEstadoPag(pagamento.getDmEstadoPag());
        dto.setDmEstadoPagDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, pagamento.getDmEstadoPag()));
        dto.setDataPagamento(pagamento.getDataPagamento());
        dto.setDataRegisto(pagamento.getDataRegisto());
        dto.setUserRegisto(pagamento.getUserRegisto());
        dto.setFormaPagamento(pagamento.getFormaPagamento());
        dto.setOrigemPagamento(pagamento.getOrigemPagamento());
        dto.setNrCheque(pagamento.getNrCheque());
        dto.setNumCheque(pagamento.getNumCheque());
        dto.setFlagIntegracao(pagamento.getFlagIntegracao());
        dto.setDataIntegracao(pagamento.getDataIntegracao());
        dto.setUserIntegracao(pagamento.getUserIntegracao());
        dto.setDmEstado(pagamento.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, pagamento.getDmEstado()));
        dto.setBanco(pagamento.getBanco());
        dto.setLinkDuc(pagamento.getLinkDuc());
        dto.setIdSolicTaxa(pagamento.getIdSolicTaxa());
        dto.setUserPagamento(pagamento.getUserPagamento());
        dto.setIdTaxa(pagamento.getIdTaxa());
        dto.setTaxas(pagamentoTaxas.stream()
            .map(pagamentoTaxa -> toPagamentoTaxaResponse(pagamentoTaxa, taxasPorId))
            .toList());
        return dto;
    }

    private String firstText(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
