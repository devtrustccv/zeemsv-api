package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTRepresInvestidorEntity;
import cv.zeemsv.api.infrastructure.repository.projection.RepresentanteInvestidorProjection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTRepresInvestidorRepository extends JpaRepository<ZeeTRepresInvestidorEntity, Integer>, JpaSpecificationExecutor<ZeeTRepresInvestidorEntity> {
    boolean existsByIdUser(Integer idUser);

    Optional<ZeeTRepresInvestidorEntity> findFirstByIdUserOrderByDataRegistoDescIdDesc(Integer idUser);

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
            s.fotoUrl as fotoUrl,
            s.fotoPath as fotoPath,
            coalesce(s.indicativoPais, o.indicativoPais) as indicativoPais,
            coalesce(s.endereco, o.endereco) as endereco
        from ZeeTRepresInvestidorEntity r
        left join ZeeTSocioRepresEntity s on s.id = r.idSocioRepres
        left join ZeeTOrdemEntity o on o.id = r.idOrdem
        where r.idInvestidor = :idInvestidor
            and r.dmEstado in ('A', 'PENDENTE')
        order by r.dataRegisto desc, r.id desc
        """)
    List<RepresentanteInvestidorProjection> findByInvestidorId(@Param("idInvestidor") Integer idInvestidor);

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
            s.nome as nome,
            s.nacionalidade as nacionalidade,
            s.nif as nif,
            s.tipoDoc as tipoDoc,
            s.nrDoc as nrDoc,
            s.telefone as telefone,
            s.telemovel as telemovel,
            s.email as email,
            s.fotoUrl as fotoUrl,
            s.fotoPath as fotoPath,
            s.indicativoPais as indicativoPais,
            s.endereco as endereco
        from ZeeTRepresInvestidorEntity r
        join ZeeTSocioRepresEntity s on s.id = r.idSocioRepres
        where r.idInvestidor = :idInvestidor
            and r.dmEstado in ('A', 'PENDENTE')
        order by s.nome asc, r.id desc
        """)
    List<RepresentanteInvestidorProjection> findSociosByInvestidorId(@Param("idInvestidor") Integer idInvestidor);

    @Query("""
        select r from ZeeTRepresInvestidorEntity r
        where r.idInvestidor = :idInvestidor
          and ((:idSocioRepres is null and r.idSocioRepres is null) or r.idSocioRepres = :idSocioRepres)
          and ((:idOrdem is null and r.idOrdem is null) or r.idOrdem = :idOrdem)
        """)
    Optional<ZeeTRepresInvestidorEntity> findAssociation(
        @Param("idInvestidor") Integer idInvestidor,
        @Param("idSocioRepres") Integer idSocioRepres,
        @Param("idOrdem") Integer idOrdem
    );

    @Query("""
        select count(r)
        from ZeeTRepresInvestidorEntity r
        where r.idInvestidor = :idInvestidor
            and r.dmEstado = 'A'
            and upper(r.dmPrincipal) in ('S', 'SIM')
            and (:id is null or r.id <> :id)
        """)
    long countOutrosPrincipaisAtivos(
        @Param("idInvestidor") Integer idInvestidor,
        @Param("id") Integer id
    );
}
