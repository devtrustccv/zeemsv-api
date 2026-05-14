package cv.zeemsv.api.application.projeto.service;

import cv.zeemsv.api.application.projeto.dto.ProjetoRequestDTO;
import cv.zeemsv.api.application.projeto.dto.ProjetoResponseDTO;
import cv.zeemsv.api.application.projeto.mapper.ProjetoDtoMapper;
import cv.zeemsv.api.domain.projeto.business.ProjetoBus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjetoServiceImpl implements ProjetoService {
    private final ProjetoBus bus;
    private final ProjetoDtoMapper mapper;

    @Override @Transactional
    public ProjetoResponseDTO create(ProjetoRequestDTO dto) { return mapper.toResponse(bus.create(mapper.toModel(dto))); }

    @Override @Transactional
    public ProjetoResponseDTO update(Integer id, ProjetoRequestDTO dto) { return mapper.toResponse(bus.update(id, mapper.toModel(dto))); }

    @Override @Transactional(readOnly = true)
    public ProjetoResponseDTO findById(Integer id) { return mapper.toResponse(bus.findById(id)); }

    @Override @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> findAll() { return bus.findAll().stream().map(mapper::toResponse).toList(); }

    @Override @Transactional
    public void delete(Integer id) { bus.delete(id); }
}
