package cv.zeemsv.api.domain.user.business;

import cv.zeemsv.api.domain.user.model.UserModel;
import cv.zeemsv.api.infrastructure.entity.ZeeTUserEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import cv.zeemsv.api.utils.enums.UserStatus;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBus {
    private final ZeeTUserRepository repository;

    public Optional<UserModel> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(this::toModel);
    }

    public Optional<UserModel> findByEmailOrSubCmdcv(String email, String subCmdcv) {
        String normalizedSub = subCmdcv != null ? subCmdcv.toUpperCase() : null;
        String normalizedEmail = email != null ? email.toLowerCase() : null;
        return repository.findByEmailOrSubCmdcv(normalizedEmail, normalizedSub).map(this::toModel);
    }

    public Optional<UserModel> findByPessoaId(Integer pessoaId) {
        return repository.findByPessoaId(pessoaId).map(this::toModel);
    }

    public Optional<UserModel> findById(Integer id) {
        return repository.findById(id).map(this::toModel);
    }

    public UserModel save(UserModel userModel) {
        ZeeTUserEntity entity = userModel.getId() != null
            ? repository.findById(userModel.getId()).orElseGet(ZeeTUserEntity::new)
            : new ZeeTUserEntity();

        entity.setId(userModel.getId());
        entity.setNome(userModel.getName());
        entity.setEmail(userModel.getEmail() != null ? userModel.getEmail() : userModel.getSubCmdcv());
        entity.setDmEstado(userModel.getStatus() != null ? userModel.getStatus().name() : UserStatus.PENDENTE.name());
        entity.setOrigem(userModel.getProvider());
        entity.setPessoaId(userModel.getPessoaId());
        entity.setSubCmdcv(userModel.getSubCmdcv());
        if (entity.getDataRegisto() == null) {
            entity.setDataRegisto(LocalDate.now());
        }
        if (entity.getOnboardingRealizado() == null) {
            entity.setOnboardingRealizado(false);
        }

        return toModel(repository.save(entity));
    }

    public void setPessoaIdToUser(Integer id, Integer pessoaId) {
        repository.setPessoaIdToUser(id, pessoaId);
    }

    public void updatePessoaInfoById(Integer id, Integer pessoaId, String nome) {
        repository.updatePessoaInfoById(id, pessoaId, nome);
    }

    private UserModel toModel(ZeeTUserEntity entity) {
        return UserModel.builder()
            .id(entity.getId())
            .pessoaId(entity.getPessoaId())
            .name(entity.getNome())
            .email(entity.getEmail())
            .status(parseStatus(entity.getDmEstado()))
            .subCmdcv(entity.getSubCmdcv())
            .provider(entity.getOrigem())
            .build();
    }

    private UserStatus parseStatus(String value) {
        try {
            return value != null ? UserStatus.valueOf(value) : UserStatus.PENDENTE;
        } catch (IllegalArgumentException e) {
            return UserStatus.PENDENTE;
        }
    }
}
