package cv.zeemsv.api.application.cobranca.service;

import cv.zeemsv.api.application.cobranca.dto.CobrancaInvestidorResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaPagamentoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaPrestacaoResponseDTO;
import cv.zeemsv.api.application.cobranca.dto.CobrancaTaxaResponseDTO;
import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTCobrancaPrestacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTPagamentoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoCobrancaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTaxaEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaPrestacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTPagamentoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoCobrancaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicTaxaRepository;
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
    private final ZeeTCobrancaRepository cobrancaRepository;
    private final ZeeTCobrancaPrestacaoRepository prestacaoRepository;
    private final ZeeTPagamentoRepository pagamentoRepository;
    private final ZeeTSolicitacaoCobrancaRepository solicitacaoCobrancaRepository;
    private final ZeeTSolicitacaoTaxaRepository solicitacaoTaxaRepository;
    private final ZeeTTpSolicTaxaRepository tpSolicTaxaRepository;
    private final ZeeTTaxaRepository taxaRepository;
    private final ZeeTInvestidorRepository investidorRepository;
    private final DomainDescriptionHelper domainHelper;

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

        Map<Integer, ZeeTSolicitacaoTaxaEntity> solicTaxasPorId = findSolicTaxas(cobrancas);
        Map<Integer, ZeeTTpSolicTaxaEntity> tpSolicTaxasPorId = findTpSolicTaxas(solicTaxasPorId);
        Map<Integer, ZeeTTaxaEntity> taxasPorId = findTaxas(tpSolicTaxasPorId);

        return cobrancas.stream()
            .map(cobranca -> toResponse(
                cobranca,
                relacoesPorCobranca.getOrDefault(cobranca.getId(), Collections.emptyList()),
                prestacoesPorCobranca.getOrDefault(cobranca.getId(), Collections.emptyList()),
                pagamentosPorCobranca.getOrDefault(cobranca.getId(), Collections.emptyList()),
                pagamentosPorPrestacao,
                solicTaxasPorId,
                tpSolicTaxasPorId,
                taxasPorId
            ))
            .toList();
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

    private Map<Integer, ZeeTTaxaEntity> findTaxas(Map<Integer, ZeeTTpSolicTaxaEntity> tpSolicTaxasPorId) {
        List<Integer> idsTaxa = tpSolicTaxasPorId.values().stream()
            .map(ZeeTTpSolicTaxaEntity::getIdTaxa)
            .filter(Objects::nonNull)
            .distinct()
            .toList();
        return idsTaxa.isEmpty()
            ? Collections.emptyMap()
            : taxaRepository.findAllById(idsTaxa).stream()
                .collect(Collectors.toMap(ZeeTTaxaEntity::getId, Function.identity()));
    }

    private CobrancaInvestidorResponseDTO toResponse(
        ZeeTCobrancaEntity cobranca,
        List<ZeeTSolicitacaoCobrancaEntity> relacoes,
        List<ZeeTCobrancaPrestacaoEntity> prestacoes,
        List<ZeeTPagamentoEntity> pagamentos,
        Map<Integer, List<ZeeTPagamentoEntity>> pagamentosPorPrestacao,
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
        dto.setPrestacoes(prestacoes.stream()
            .map(prestacao -> toPrestacaoResponse(prestacao, pagamentosPorPrestacao.getOrDefault(prestacao.getId(), Collections.emptyList())))
            .toList());
        dto.setPagamentos(pagamentos.stream().map(this::toPagamentoResponse).toList());
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

    private CobrancaPrestacaoResponseDTO toPrestacaoResponse(ZeeTCobrancaPrestacaoEntity prestacao, List<ZeeTPagamentoEntity> pagamentos) {
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
        dto.setPagamentos(pagamentos.stream().map(this::toPagamentoResponse).toList());
        return dto;
    }

    private CobrancaPagamentoResponseDTO toPagamentoResponse(ZeeTPagamentoEntity pagamento) {
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
