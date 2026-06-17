package cv.zeemsv.api.domain.pessoa.business;

import cv.zeemsv.api.domain.pessoa.model.PessoaModel;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class PessoaBus {
    public Optional<PessoaModel> findById(Integer id) {
        return Optional.empty();
    }

    public Optional<PessoaModel> findByEmail(String email) {
        return Optional.empty();
    }

    public Optional<PessoaModel> findByNumDocumento(String numDocumento) {
        return Optional.empty();
    }

    public PessoaModel save(PessoaModel pessoa) {
        return pessoa;
    }
}
