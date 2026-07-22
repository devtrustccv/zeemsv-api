package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTPedidoAcessoInvestidorEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTPedidoAcessoInvestidorRepository extends JpaRepository<ZeeTPedidoAcessoInvestidorEntity, Integer>, JpaSpecificationExecutor<ZeeTPedidoAcessoInvestidorEntity> {
    List<ZeeTPedidoAcessoInvestidorEntity> findByIdUtilizadorOrderByDataRegistoDescIdDesc(Integer idUtilizador);

    @Query(value = """
        select exists (
            select 1
            from public.zee_t_pedido_acesso_investidor p
            where p.id_utilizador = :idUtilizador
                and p.id_investidor = :idInvestidor
                and coalesce(p.dm_estado, '') <> 'REJEITADO'
        )
        """, nativeQuery = true)
    boolean existsNaoRejeitadoByIdUtilizadorAndIdInvestidor(
        @Param("idUtilizador") Integer idUtilizador,
        @Param("idInvestidor") Integer idInvestidor
    );

    @Query(value = """
        select exists (
            select 1
            from public.zee_t_pedido_acesso_investidor p
            where p.id_socio_repres = :idSocioRepres
                and p.id_investidor = :idInvestidor
                and coalesce(p.dm_estado, '') <> 'REJEITADO'
        )
        """, nativeQuery = true)
    boolean existsNaoRejeitadoByIdSocioRepresAndIdInvestidor(
        @Param("idSocioRepres") Integer idSocioRepres,
        @Param("idInvestidor") Integer idInvestidor
    );

    @Query(value = """
        select exists (
            select 1
            from public.zee_t_pedido_acesso_investidor p
            where p.id_ordem = :idOrdem
                and p.id_investidor = :idInvestidor
                and coalesce(p.dm_estado, '') <> 'REJEITADO'
        )
        """, nativeQuery = true)
    boolean existsNaoRejeitadoByIdOrdemAndIdInvestidor(
        @Param("idOrdem") Integer idOrdem,
        @Param("idInvestidor") Integer idInvestidor
    );
}
