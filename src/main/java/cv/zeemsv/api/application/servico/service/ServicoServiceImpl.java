package cv.zeemsv.api.application.servico.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.servico.dto.ServicoOnboardingResponseDTO;
import cv.zeemsv.api.application.servico.dto.ServicoResponseDTO;
import cv.zeemsv.api.application.servico.dto.ServicoSolicitanteResponseDTO;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicOnboardingEntity;
import cv.zeemsv.api.infrastructure.entity.ZeeTTpSolicRelacaoEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicOnboardingRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicRelacaoRepository;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.projection.ServicoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {
    private final ZeeTTpSolicitacaoRepository repository;
    private final ZeeTTpSolicRelacaoRepository tpSolicRelacaoRepository;
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

    private List<ServicoResponseDTO> toResponseList(List<ServicoProjection> servicos) {
        List<Integer> ids = servicos.stream()
            .map(ServicoProjection::getId)
            .toList();
        Map<Integer, List<ZeeTTpSolicRelacaoEntity>> relacoesByTpSolic = ids.isEmpty()
            ? Collections.emptyMap()
            : tpSolicRelacaoRepository.findByIdTpSolicIn(ids).stream()
                .collect(Collectors.groupingBy(ZeeTTpSolicRelacaoEntity::getIdTpSolic));
        List<Integer> idsComOnboarding = servicos.stream()
            .filter(servico -> Boolean.TRUE.equals(servico.getPossuiOnboarding()))
            .map(ServicoProjection::getId)
            .toList();
        Map<Integer, List<ZeeTSolicOnboardingEntity>> onboardingByTpSolic = idsComOnboarding.isEmpty()
            ? Collections.emptyMap()
            : solicOnboardingRepository.findByIdTpSolicIn(idsComOnboarding).stream()
                .collect(Collectors.groupingBy(ZeeTSolicOnboardingEntity::getIdTpSolic));

        return servicos.stream()
            .map(servico -> toResponse(
                servico,
                relacoesByTpSolic.getOrDefault(servico.getId(), Collections.emptyList()),
                onboardingByTpSolic.getOrDefault(servico.getId(), Collections.emptyList())
            ))
            .toList();
    }

    private ServicoResponseDTO toResponse(
        ServicoProjection entity,
        List<ZeeTTpSolicRelacaoEntity> relacoes,
        List<ZeeTSolicOnboardingEntity> onboardings
    ) {
        ServicoResponseDTO dto = new ServicoResponseDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setDmTipoSolicitacao(entity.getDmTipoSolicitacao());
        dto.setDmTipoSolicitacaoDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_SOLICITACAO, entity.getDmTipoSolicitacao()));
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
        dto.setEntidadeDenominacao(entity.getEntidadeDenominacao());
        dto.setEntidadeSigla(entity.getEntidadeSigla());
        dto.setEntidadeDmTipoEnt(entity.getEntidadeDmTipoEnt());
        dto.setEntidadeDmTipoEntDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_ENTIDADE, entity.getEntidadeDmTipoEnt()));
        dto.setQuemDeveSolicitar(relacoes.stream()
            .map(this::toSolicitanteResponse)
            .toList());
        dto.setTiposOnboarding(Boolean.TRUE.equals(entity.getPossuiOnboarding())
            ? onboardings.stream().map(this::toOnboardingResponse).toList()
            : Collections.emptyList());
        return dto;
    }

    private ServicoSolicitanteResponseDTO toSolicitanteResponse(ZeeTTpSolicRelacaoEntity entity) {
        ServicoSolicitanteResponseDTO dto = new ServicoSolicitanteResponseDTO();
        dto.setDmObjecto(entity.getDmObjecto());
        dto.setDmObjectoDesc(domainHelper.describe(DomainDescriptionHelper.OBJECTO, entity.getDmObjecto()));
        return dto;
    }

    private ServicoOnboardingResponseDTO toOnboardingResponse(ZeeTSolicOnboardingEntity entity) {
        ServicoOnboardingResponseDTO dto = new ServicoOnboardingResponseDTO();
        dto.setId(entity.getId());
        dto.setDmTipoOnboarding(entity.getDmTipoOnboarding());
        dto.setDmTipoOnboardingDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_ONBOARDING, entity.getDmTipoOnboarding()));
        return dto;
    }
}
