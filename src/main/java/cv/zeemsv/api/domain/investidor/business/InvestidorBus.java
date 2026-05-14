package cv.zeemsv.api.domain.investidor.business;

import cv.zeemsv.api.domain.investidor.model.Investidor;
import java.util.List;

public interface InvestidorBus {
    Investidor create(Investidor model);
    Investidor update(Integer id, Investidor model);
    Investidor findById(Integer id);
    List<Investidor> findAll();
    void delete(Integer id);
}
