package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbl_cae", schema = "public")
@Getter @Setter
public class TblCaeEntity extends BaseEntity {
    // TODO: acrescentar colunas específicas conforme DDL real da tabela tbl_cae.
}
