package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "zee_t_proj_atividade", schema = "public")
@Getter @Setter
public class ZeeTProjAtividadeEntity extends BaseEntity {
    // TODO: acrescentar colunas específicas conforme DDL real da tabela zee_t_proj_atividade.
}
