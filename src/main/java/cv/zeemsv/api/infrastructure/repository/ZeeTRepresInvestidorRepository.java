package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTRepresInvestidorEntity;
import cv.zeemsv.api.infrastructure.repository.projection.RepresentanteInvestidorProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTRepresInvestidorRepository extends JpaRepository<ZeeTRepresInvestidorEntity, Integer>, JpaSpecificationExecutor<ZeeTRepresInvestidorEntity> {
    @Query("""
        select
            r.id as id,
            r.idInvestidor as idInvestidor,
            r.idSocioRepres as idSocioRepres,
            r.idOrdem as idOrdem,
            r.dmTpRepresentante as dmTpRepresentante,
            r.flagRepresentante as flagRepresentante,
            r.flagSocio as flagSocio,
            r.dmPrincipal as dmPrincipal,
            r.dmEstado as dmEstado,
            r.dataRegisto as dataRegisto,
            r.userRegisto as userRegisto,
            r.idUser as idUser,
            coalesce(s.nome, o.nome) as nome,
            coalesce(s.nacionalidade, o.nacionalidade) as nacionalidade,
            coalesce(s.nif, str(o.nif)) as nif,
            coalesce(s.tipoDoc, o.dmTpDoc) as tipoDoc,
            coalesce(s.nrDoc, o.nrDocumento) as nrDoc,
            s.telefone as telefone,
            coalesce(s.telemovel, o.telemovel) as telemovel,
            coalesce(s.email, o.email) as email,
            coalesce(s.indicativoPais, o.indicativoPais) as indicativoPais
        from ZeeTRepresInvestidorEntity r
        left join ZeeTSocioRepresEntity s on s.id = r.idSocioRepres
        left join ZeeTOrdemEntity o on o.id = r.idOrdem
        where r.idInvestidor = :idInvestidor
            and r.dmEstado = 'A'
        order by r.dataRegisto desc, r.id desc
        """)
    List<RepresentanteInvestidorProjection> findByInvestidorId(@Param("idInvestidor") Integer idInvestidor);
}
