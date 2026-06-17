package cv.zeemsv.api.infrastructure.repository;

import cv.zeemsv.api.infrastructure.entity.ZeeTUserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ZeeTUserRepository extends JpaRepository<ZeeTUserEntity, Integer>, JpaSpecificationExecutor<ZeeTUserEntity> {
    Optional<ZeeTUserEntity> findByEmailIgnoreCase(String email);

    Optional<ZeeTUserEntity> findByPessoaId(Integer pessoaId);

    @Query("""
        select u from ZeeTUserEntity u
        where (:email is not null and lower(u.email) = lower(:email))
           or (:subCmdcv is not null and upper(u.subCmdcv) = upper(:subCmdcv))
        """)
    Optional<ZeeTUserEntity> findByEmailOrSubCmdcv(@Param("email") String email, @Param("subCmdcv") String subCmdcv);

    @Modifying
    @Query("update ZeeTUserEntity u set u.pessoaId = :pessoaId where u.id = :id")
    void setPessoaIdToUser(@Param("id") Integer id, @Param("pessoaId") Integer pessoaId);

    @Modifying
    @Query("update ZeeTUserEntity u set u.pessoaId = :pessoaId, u.nome = :nome where u.id = :id")
    void updatePessoaInfoById(@Param("id") Integer id, @Param("pessoaId") Integer pessoaId, @Param("nome") String nome);
}
