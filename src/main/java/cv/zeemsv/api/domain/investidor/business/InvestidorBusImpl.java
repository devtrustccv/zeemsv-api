package cv.zeemsv.api.domain.investidor.business;

import cv.zeemsv.api.domain.investidor.model.Investidor;
import cv.zeemsv.api.infrastructure.entity.ZeeTInvestidorEntity;
import cv.zeemsv.api.infrastructure.mapper.InvestidorEntityMapper;
import cv.zeemsv.api.infrastructure.repository.ZeeTInvestidorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InvestidorBusImpl implements InvestidorBus {
    private final ZeeTInvestidorRepository repository;
    private final InvestidorEntityMapper mapper;

    @Override
    public Investidor create(Investidor model) {
        ZeeTInvestidorEntity entity = mapper.toEntity(model);
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Investidor update(Integer id, Investidor model) {
        ZeeTInvestidorEntity current = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Investidor não encontrado: " + id));
        ZeeTInvestidorEntity entity = mapper.toEntity(model);
        entity.setId(current.getId());
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Investidor findById(Integer id) {
        return repository.findById(id).map(mapper::toModel).orElseThrow(() -> new EntityNotFoundException("Investidor não encontrado: " + id));
    }

    @Override
    public List<Investidor> findAll() { return repository.findAll().stream().map(mapper::toModel).toList(); }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Investidor não encontrado: " + id);
        repository.deleteById(id);
    }
}
