package cv.zeemsv.api.domain.solicitacao.business;

import cv.zeemsv.api.domain.solicitacao.model.Solicitacao;
import cv.zeemsv.api.infrastructure.entity.ZeeTSolicitacaoEntity;
import cv.zeemsv.api.infrastructure.mapper.SolicitacaoEntityMapper;
import cv.zeemsv.api.infrastructure.repository.ZeeTSolicitacaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SolicitacaoBusImpl implements SolicitacaoBus {
    private final ZeeTSolicitacaoRepository repository;
    private final SolicitacaoEntityMapper mapper;

    @Override
    public Solicitacao create(Solicitacao model) {
        ZeeTSolicitacaoEntity entity = mapper.toEntity(model);
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Solicitacao update(Integer id, Solicitacao model) {
        ZeeTSolicitacaoEntity current = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Solicitacao não encontrado: " + id));
        ZeeTSolicitacaoEntity entity = mapper.toEntity(model);
        entity.setId(current.getId());
        return mapper.toModel(repository.save(entity));
    }

    @Override
    public Solicitacao findById(Integer id) {
        return repository.findById(id).map(mapper::toModel).orElseThrow(() -> new EntityNotFoundException("Solicitacao não encontrado: " + id));
    }

    @Override
    public List<Solicitacao> findAll() { return repository.findAll().stream().map(mapper::toModel).toList(); }

    @Override
    public void delete(Integer id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Solicitacao não encontrado: " + id);
        repository.deleteById(id);
    }
}
