package cv.zeemsv.api.infrastructure.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "t_pedido", schema = "public")
@Getter @Setter
public class TPedidoEntity extends BaseEntity {
    // TODO: acrescentar colunas específicas conforme DDL real da tabela t_pedido.
}
