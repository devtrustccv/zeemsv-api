package cv.zeemsv.api.application.servico.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.servico.dto.ServicoOnboardingResponseDTO;
import cv.zeemsv.api.application.servico.dto.ServicoResponseDTO;
import cv.zeemsv.api.application.servico.dto.ServicoSolicitanteResponseDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicOnboardingEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicRelacaoEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicRepreEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicTaxaEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicOnboardingRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicRepreRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicTaxaRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.projection.ServicoProjection;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {
    private static final String MOMENTO_PAGAMENTO_NO_INICIO = "NO_INICIO";

    private final ZeeTTpSolicitacaoRepository repository;
    private final ZeeTTpSolicRelacaoRepository tpSolicRelacaoRepository;
    private final ZeeTTpSolicRepreRepository tpSolicRepreRepository;
    private final ZeeTTpSolicTaxaRepository tpSolicTaxaRepository;
    private final ZeeTSolicOnboardingRepository solicOnboardingRepository;
    private final DomainDescriptionHelper domainHelper;

    @Override
    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> findAll() {
        return toResponseList(repository.findAllServicos());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> findByTipoRepresentante(String dmTpRepresentante) {
        if (!StringUtils.hasText(dmTpRepresentante)) {
            return findAll();
        }
        return toResponseList(repository.findServicosByTipoRepresentante(dmTpRepresentante.trim()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> findPendentesEnvioCms() {
        return toResponseList(repository.findServicosPendentesEnvioCms());
    }

    @Override
    @Transactional
    public ServicoResponseDTO marcarEnviadoCms(Integer idTpSolicitacao) {
        ZeeTTpSolicitacaoEntity servico = repository.findById(idTpSolicitacao)
            .orElseThrow(() -> new EntityNotFoundException("Tipo de solicitacao nao encontrado: " + idTpSolicitacao));

        servico.setSendedToCms(true);
        repository.save(servico);

        ServicoProjection projection = repository.findServicoById(idTpSolicitacao)
            .orElseThrow(() -> new EntityNotFoundException("Tipo de solicitacao nao encontrado: " + idTpSolicitacao));
        return toResponseList(List.of(projection)).get(0);
    }

    private List<ServicoResponseDTO> toResponseList(List<ServicoProjection> servicos) {
        List<Integer> ids = servicos.stream()
            .map(ServicoProjection::getId)
            .toList();
        Map<Integer, List<ZeeTTpSolicRelacaoEntity>> relacoesByTpSolic = ids.isEmpty()
            ? Collections.emptyMap()
            : tpSolicRelacaoRepository.findByIdTpSolicIn(ids).stream()
                .collect(Collectors.groupingBy(ZeeTTpSolicRelacaoEntity::getIdTpSolic));
        Map<Integer, List<ZeeTTpSolicRepreEntity>> representantesByTpSolic = ids.isEmpty()
            ? Collections.emptyMap()
            : tpSolicRepreRepository.findByIdTpSolicIn(ids).stream()
                .collect(Collectors.groupingBy(ZeeTTpSolicRepreEntity::getIdTpSolic));
        Map<Integer, List<ZeeTTpSolicTaxaEntity>> taxasByTpSolic = ids.isEmpty()
            ? Collections.emptyMap()
            : tpSolicTaxaRepository.findByIdTpSolicIn(ids).stream()
                .collect(Collectors.groupingBy(ZeeTTpSolicTaxaEntity::getIdTpSolic));
        List<Integer> idsComOnboarding = servicos.stream()
            .filter(servico -> Boolean.TRUE.equals(servico.getPossuiOnboarding()))
            .map(ServicoProjection::getId)
            .toList();
        Map<Integer, List<ZeeTSolicOnboardingEntity>> onboardingByTpSolic = idsComOnboarding.isEmpty()
            ? Collections.emptyMap()
            : solicOnboardingRepository.findByIdTpSolicInOrderByOrdemAscIdAsc(idsComOnboarding).stream()
                .collect(Collectors.groupingBy(ZeeTSolicOnboardingEntity::getIdTpSolic));

        return servicos.stream()
            .map(servico -> toResponse(
                servico,
                relacoesByTpSolic.getOrDefault(servico.getId(), Collections.emptyList()),
                representantesByTpSolic.getOrDefault(servico.getId(), Collections.emptyList()),
                taxasByTpSolic.getOrDefault(servico.getId(), Collections.emptyList()),
                onboardingByTpSolic.getOrDefault(servico.getId(), Collections.emptyList())
            ))
            .toList();
    }

    private ServicoResponseDTO toResponse(
        ServicoProjection entity,
        List<ZeeTTpSolicRelacaoEntity> relacoes,
        List<ZeeTTpSolicRepreEntity> representantes,
        List<ZeeTTpSolicTaxaEntity> taxas,
        List<ZeeTSolicOnboardingEntity> onboardings
    ) {
        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setDmTipoSolicitacao(entity.getDmTipoSolicitacao());
        dto.setDmTipoSolicitacaoDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_SOLICITACAO, entity.getDmTipoSolicitacao()));
        dto.setDmCategoria(entity.getDmCategoria());
        dto.setDmCategoriaDesc(domainHelper.describe(DomainDescriptionHelper.CATEGORIA_SERVICO, entity.getDmCategoria()));
        dto.setDescricao(entity.getDescricao());
        dto.setMsgPedido(entity.getMsgPedido());
        dto.setPrazoDia(entity.getPrazoDia());
        dto.setFlagObrigatorio(entity.getFlagObrigatorio());
        dto.setCodigo(entity.getCodigo());
        dto.setDmEstado(entity.getDmEstado());
        dto.setDmEstadoDesc(domainHelper.describe(DomainDescriptionHelper.ESTADO, entity.getDmEstado()));
        dto.setIdEntExterna(entity.getIdEntExterna());
        dto.setPossuiTaxa(entity.getPossuiTaxa());
        dto.setPossuiOnboarding(entity.getPossuiOnboarding());
        dto.setSendedToCms(entity.getSendedToCms());
        List<ZeeTTpSolicTaxaEntity> taxasNoInicio = taxas.stream()
            .filter(this::isTaxaPagamentoNoInicio)
            .toList();
        dto.setInstantPagamento(!taxasNoInicio.isEmpty());
        dto.setTotalAPagar(taxasNoInicio.stream()
            .map(ZeeTTpSolicTaxaEntity::getValor)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        dto.setEntidadeDenominacao(entity.getEntidadeDenominacao());
        dto.setEntidadeSigla(entity.getEntidadeSigla());
        dto.setEntidadeDmTipoEnt(entity.getEntidadeDmTipoEnt());
        dto.setEntidadeDmTipoEntDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_ENTIDADE, entity.getEntidadeDmTipoEnt()));
        dto.setDmTpRepresentante(representantes.stream()
            .map(ZeeTTpSolicRepreEntity::getDmTpRepresentante)
            .distinct()
            .toList());
        dto.setQuemDeveSolicitar(relacoes.stream()
            .map(this::toSolicitanteResponse)
            .toList());
        dto.setTiposOnboarding(Boolean.TRUE.equals(entity.getPossuiOnboarding())
            ? onboardings.stream().map(this::toOnboardingResponse).toList()
            : Collections.emptyList());
        return dto;
    }

    private boolean isTaxaPagamentoNoInicio(ZeeTTpSolicTaxaEntity taxa) {
        return MOMENTO_PAGAMENTO_NO_INICIO.equalsIgnoreCase(taxa.getDmMomentoPag());
    }

    private ServicoSolicitanteResponseDTO toSolicitanteResponse(ZeeTTpSolicRelacaoEntity entity) {
        ServicoSolicitanteResponseDTO dto = new ServicoSolicitanteResponseDTO();
        dto.setDmObjecto(entity.getDmObjecto());
        dto.setDmObjectoDesc(domainHelper.describeObjecto(entity.getDmObjecto()));
        return dto;
    }

    private ServicoOnboardingResponseDTO toOnboardingResponse(ZeeTSolicOnboardingEntity entity) {
        ServicoOnboardingResponseDTO dto = new ServicoOnboardingResponseDTO();
        dto.setId(entity.getId());
        dto.setDmTipoOnboarding(entity.getDmTipoOnboarding());
        dto.setDmTipoOnboardingDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_ONBOARDING, entity.getDmTipoOnboarding()));
        dto.setOrdem(entity.getOrdem());
        return dto;
    }
}
