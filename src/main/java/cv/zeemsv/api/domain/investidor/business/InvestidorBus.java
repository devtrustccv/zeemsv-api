package cv.zeemsv.api.domain.investidor.business;

import cv.zeemsv.api.domain.investidor.model.Investidor;
import cv.zeemsv.api.domain.investidor.model.InvestidorUser;
import java.util.List;

public interface InvestidorBus {
    Investidor create(Investidor model);
    Investidor update(Integer id, Investidor model);
    Investidor findById(Integer id);
    List<Investidor> findAll();
    List<InvestidorUser> findByUserEmail(String email);
    void delete(Integer id);
}
