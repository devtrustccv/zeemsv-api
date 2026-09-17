package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTProjetoRepresEntity;
import cv.zeemsv.api.infrastructure.repository.projection.ProjetoRepresentanteProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTProjetoRepresRepository extends JpaRepository<ZeeTProjetoRepresEntity, Integer>, JpaSpecificationExecutor<ZeeTProjetoRepresEntity> {
    @Query("""
        select
            pr.id as id,
            pr.idProjeto as idProjeto,
            pr.idRepresSocio as idRepresSocio,
            pr.idRepresInvestidor as idRepresInvestidor,
            pr.estado as estado,
            pr.dateCreate as dateCreate,
            pr.userCreate as userCreate,
            ri.idInvestidor as idInvestidor,
            ri.idOrdem as idOrdem,
            coalesce(ri.dmTpRepresentante, sProjeto.dmTpRepresentante, sInvestidor.dmTpRepresentante) as dmTpRepresentante,
            coalesce(ri.flagRepresentante, sProjeto.flagRepresentante, sInvestidor.flagRepresentante) as flagRepresentante,
            coalesce(ri.flagSocio, sProjeto.flagSocio, sInvestidor.flagSocio) as flagSocio,
            coalesce(ri.dmPrincipal, sProjeto.dmPrincipal, sInvestidor.dmPrincipal) as dmPrincipal,
            ri.dmEstado as dmEstado,
            ri.dataRegisto as dataRegisto,
            ri.userRegisto as userRegisto,
            coalesce(sProjeto.idUser, sInvestidor.idUser) as idUser,
            coalesce(sProjeto.nome, sInvestidor.nome, ordem.nome) as nome,
            coalesce(sProjeto.nacionalidade, sInvestidor.nacionalidade, ordem.nacionalidade) as nacionalidade,
            coalesce(sProjeto.nif, sInvestidor.nif, str(ordem.nif)) as nif,
            coalesce(sProjeto.tipoDoc, sInvestidor.tipoDoc, ordem.dmTpDoc) as tipoDoc,
            coalesce(sProjeto.nrDoc, sInvestidor.nrDoc, ordem.nrDocumento) as nrDoc,
            coalesce(sProjeto.telefone, sInvestidor.telefone) as telefone,
            coalesce(sProjeto.telemovel, sInvestidor.telemovel, ordem.telemovel) as telemovel,
            coalesce(sProjeto.email, sInvestidor.email, ordem.email) as email,
            coalesce(sProjeto.fotoUrl, sInvestidor.fotoUrl) as fotoUrl,
            coalesce(sProjeto.fotoPath, sInvestidor.fotoPath) as fotoPath,
            coalesce(sProjeto.indicativoPais, sInvestidor.indicativoPais, ordem.indicativoPais) as indicativoPais,
            coalesce(sProjeto.endereco, sInvestidor.endereco, ordem.endereco) as endereco
        from ZeeTProjetoRepresEntity pr
        left join ZeeTRepresInvestidorEntity ri on ri.id = pr.idRepresInvestidor
        left join ZeeTSocioRepresEntity sProjeto on sProjeto.id = pr.idRepresSocio
        left join ZeeTSocioRepresEntity sInvestidor on sInvestidor.id = ri.idSocioRepres
        left join ZeeTOrdemEntity ordem on ordem.id = ri.idOrdem
        where pr.idProjeto = :idProjeto
            and pr.estado in ('A', 'ATIVO')
            and (ri.id is null or ri.dmEstado = 'A')
            and (sProjeto.id is null or sProjeto.estado = 'A')
            and (sInvestidor.id is null or sInvestidor.estado = 'A')
            and (ordem.id is null or ordem.dmEstado = 'A')
        order by pr.dateCreate desc, pr.id desc
        """)
    List<ProjetoRepresentanteProjection> findAtivosByProjetoId(@Param("idProjeto") Integer idProjeto);
}
