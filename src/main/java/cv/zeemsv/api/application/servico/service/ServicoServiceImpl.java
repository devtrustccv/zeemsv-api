package cv.zeemsv.api.application.servico.service;

import cv.zeemsv.api.application.domain.DomainDescriptionHelper;
import cv.zeemsv.api.application.servico.dto.ServicoResponseDTO;
import cv.zeemsv.api.infrastructure.repository.ZeeTTpSolicitacaoRepository;
import cv.zeemsv.api.infrastructure.repository.projection.ServicoProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServicoServiceImpl implements ServicoService {
    private final ZeeTTpSolicitacaoRepository repository;
    private final DomainDescriptionHelper domainHelper;

    @Override
    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> findAll() {
        return repository.findAllServicos()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicoResponseDTO> findByTipoRepresentante(String dmTpRepresentante) {
        if (!StringUtils.hasText(dmTpRepresentante)) {
            return findAll();
        }
        return repository.findServicosByTipoRepresentante(dmTpRepresentante.trim())
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private ServicoResponseDTO toResponse(ServicoProjection entity) {
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
        dto.setEntidadeDenominacao(entity.getEntidadeDenominacao());
        dto.setEntidadeSigla(entity.getEntidadeSigla());
        dto.setEntidadeDmTipoEnt(entity.getEntidadeDmTipoEnt());
        dto.setEntidadeDmTipoEntDesc(domainHelper.describe(DomainDescriptionHelper.TIPO_ENTIDADE, entity.getEntidadeDmTipoEnt()));
        return dto;
    }
}
