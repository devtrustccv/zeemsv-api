package cv.zeemsv.api.application.investidor.service;

import cv.zeemsv.api.application.investidor.dto.InvestidorRequestDTO;
import cv.zeemsv.api.application.investidor.dto.InvestidorResponseDTO;
import cv.zeemsv.api.application.investidor.mapper.InvestidorDtoMapper;
import cv.zeemsv.api.domain.investidor.business.InvestidorBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvestidorServiceImpl implements InvestidorService {
    private final InvestidorBus bus;
    private final InvestidorDtoMapper mapper;

    @Override @Transactional
    public InvestidorResponseDTO create(InvestidorRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public InvestidorResponseDTO update(Integer id, InvestidorRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public InvestidorResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<InvestidorResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }
}
