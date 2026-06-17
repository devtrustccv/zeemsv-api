package cv.zeemsv.api.domain.user.business;

import cv.zeemsv.api.domain.user.model.SessionModel;
import cv.zeemsv.api.infrastructure.entity.ZeeTSessionEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTSessionRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SessionBus {
    private final ZeeTSessionRepository repository;

    public SessionModel save(SessionModel session) {
        return toModel(repository.save(toEntity(session)));
    }

    public Optional<SessionModel> findBySessionToken(String sessionToken) {
        return repository.findBySessionToken(sessionToken).map(this::toModel);
    }

    private ZeeTSessionEntity toEntity(SessionModel model) {
        ZeeTSessionEntity entity = new ZeeTSessionEntity();
        entity.setId(model.getId());
        entity.setUserId(model.getUserId());
        entity.setStatus(model.getStatus());
        entity.setStartDate(model.getStartDate());
        entity.setEndDate(model.getEndDate());
        entity.setSessionToken(model.getSessionToken());
        entity.setProvider(model.getProvider());
        return entity;
    }

    private SessionModel toModel(ZeeTSessionEntity entity) {
        return SessionModel.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .status(entity.getStatus())
            .startDate(entity.getStartDate())
            .endDate(entity.getEndDate())
            .sessionToken(entity.getSessionToken())
            .provider(entity.getProvider())
            .build();
    }
}
