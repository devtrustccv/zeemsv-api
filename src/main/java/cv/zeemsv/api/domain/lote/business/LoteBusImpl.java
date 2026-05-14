package cv.zeemsv.api.domain.lote.business;

import cv.zeemsv.api.domain.lote.model.Lote;
import cv.zeemsv.api.infrastructure.entity.ZeeTLoteEntity;
import cv.zeemsv.api.infrastructure.mapper.LoteEntityMapper;
import cv.zeemsv.api.infrastructure.repository.ZeeTLoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LoteBusImpl implements LoteBus {
    private final ZeeTLoteRepository repository;
    private final LoteEntityMapper mapper;

    @Override
    public Lote create(Lote model) {
        ZeeTLoteEntity entity = mapper.toEntity(model);
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Lote update(Integer id, Lote model) {
        ZeeTLoteEntity current = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lote não encontrado: " + id));
        ZeeTLoteEntity entity = mapper.toEntity(model);
        entity.setId(current.getId());
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Lote findById(Integer id) {
        return repository.findById(id).map(mapper::toModel).orElseThrow(() -> new EntityNotFoundException("Lote não encontrado: " + id));
    }

    @Override
    public List<Lote> findAll() { return repository.findAll().stream().map(mapper::toModel).toList(); }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Lote não encontrado: " + id);
        repository.deleteById(id);
    }
}
