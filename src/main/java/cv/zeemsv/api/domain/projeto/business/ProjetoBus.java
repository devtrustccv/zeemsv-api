package cv.zeemsv.api.domain.projeto.business;

import cv.zeemsv.api.domain.projeto.model.Projeto;
import java.util.List;

public interface ProjetoBus {
    Projeto create(Projeto model);
    Projeto update(Integer id, Projeto model);
    Projeto findById(Integer id);
    List<Projeto> findAll();
    void delete(Integer id);
}
