package cv.zeemsv.api.application.lote.service;

import cv.zeemsv.api.application.lote.dto.LoteRequestDTO;
import cv.zeemsv.api.application.lote.dto.LoteResponseDTO;
import cv.zeemsv.api.application.lote.mapper.LoteDtoMapper;
import cv.zeemsv.api.domain.lote.business.LoteBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoteServiceImpl implements LoteService {
    private final LoteBus bus;
    private final LoteDtoMapper mapper;

    @Override @Transactional
    public LoteResponseDTO create(LoteRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public LoteResponseDTO update(Integer id, LoteRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public LoteResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<LoteResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }
}
