package cv.zeemsv.api.domain.projeto.business;

import cv.zeemsv.api.domain.projeto.model.Projeto;
import cv.zeemsv.api.infrastructure.entity.ZeeTProjInvestEntity;
import cv.zeemsv.api.infrastructure.mapper.ProjetoEntityMapper;
import cv.zeemsv.api.infrastructure.repository.ZeeTProjInvestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjetoBusImpl implements ProjetoBus {
    private final ZeeTProjInvestRepository repository;
    private final ProjetoEntityMapper mapper;

    @Override
    public Projeto create(Projeto model) {
        ZeeTProjInvestEntity entity = mapper.toEntity(model);
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Projeto update(Integer id, Projeto model) {
        ZeeTProjInvestEntity current = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado: " + id));
        ZeeTProjInvestEntity entity = mapper.toEntity(model);
        entity.setId(current.getId());
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Projeto findById(Integer id) {
        return repository.findById(id).map(mapper::toModel).orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado: " + id));
    }

    @Override
    public List<Projeto> findAll() { return repository.findAll().stream().map(mapper::toModel).toList(); }

    @Override
    public List<Projeto> findByInvestidorId(Integer idInvestidor) {
        return repository.findByIdInvestidorOrderByDateCreateDesc(idInvestidor).stream().map(mapper::toModel).toList();
    }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Projeto não encontrado: " + id);
        repository.deleteById(id);
    }
}
