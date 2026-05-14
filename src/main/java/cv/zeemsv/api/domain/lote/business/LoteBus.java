package cv.zeemsv.api.domain.lote.business;

import cv.zeemsv.api.domain.lote.model.Lote;
import java.util.List;

public interface LoteBus {
    Lote create(Lote model);
    Lote update(Integer id, Lote model);
    Lote findById(Integer id);
    List<Lote> findAll();
    void delete(Integer id);
}
