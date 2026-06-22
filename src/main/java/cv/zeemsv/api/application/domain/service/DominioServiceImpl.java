package cv.zeemsv.api.application.domain.service;

import cv.zeemsv.api.application.domain.dto.DominioResponseDTO;
import cv.zeemsv.api.application.domain.dto.DominioValorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DominioServiceImpl implements DominioService {
    private static final String SQL_FIND_ALL = """
        select distinct dominio
        from tbl_domain
        where dominio is not null
        order by dominio
        """;

    private static final String SQL_FIND_VALORES_BY_DOMINIO = """
        select dominio, valor, description
        from tbl_domain
        where dominio = ?
        order by valor
        """;

    @Qualifier("igrpJdbcTemplate")
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(readOnly = true)
    public List<DominioResponseDTO> findAll() {
        return jdbcTemplate.query(SQL_FIND_ALL, (rs, rowNum) -> {
            DominioResponseDTO dto = new DominioResponseDTO();
            dto.setDominio(rs.getString("dominio"));
            return dto;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<DominioValorResponseDTO> findValoresByDominio(String dominio) {
        return jdbcTemplate.query(SQL_FIND_VALORES_BY_DOMINIO, (rs, rowNum) -> {
            DominioValorResponseDTO dto = new DominioValorResponseDTO();
            dto.setDominio(rs.getString("dominio"));
            dto.setValor(rs.getString("valor"));
            dto.setDescription(rs.getString("description"));
            return dto;
        }, dominio);
    }
}
