package cv.zeemsv.api.application.solicitacao.service;

import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoRequestDTO;
import cv.zeemsv.api.application.solicitacao.dto.SolicitacaoResponseDTO;
import cv.zeemsv.api.application.solicitacao.mapper.SolicitacaoDtoMapper;
import cv.zeemsv.api.domain.solicitacao.business.SolicitacaoBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitacaoServiceImpl implements SolicitacaoService {
    private final SolicitacaoBus bus;
    private final SolicitacaoDtoMapper mapper;

    @Override @Transactional
    public SolicitacaoResponseDTO create(SolicitacaoRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public SolicitacaoResponseDTO update(Integer id, SolicitacaoRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public SolicitacaoResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<SolicitacaoResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }
}
