package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.dto.SessionAuditPageResponseDTO;
import cv.zeemsv.api.application.audit.dto.SessionAuditRequestDTO;
import cv.zeemsv.api.application.audit.dto.SessionAuditResponseDTO;
import cv.zeemsv.api.application.audit.entity.SessionAuditLog;
import cv.zeemsv.api.config.AuditMongoProperties;
import cv.zeemsv.api.config.AuditProperties;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTUserEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Log4j2
public class SessionAuditService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 200;
    private static final String STATE_ACTIVE = "ATIVA";

    private final MongoTemplate mongoTemplate;
    private final ZeeTUserRepository userRepository;
    private final AuditProperties auditProperties;
    private final String collectionName;

    public SessionAuditService(
        MongoTemplate mongoTemplate,
        ZeeTUserRepository userRepository,
        AuditMongoProperties mongoProperties,
        AuditProperties auditProperties
    ) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.auditProperties = auditProperties;
        this.collectionName = mongoProperties.getCollection().getSessions();
    }

    @PostConstruct
    public void ensureIndexes() {
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("date", Sort.Direction.DESC));
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("userId", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("state", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("ip", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("sessionId", Sort.Direction.ASC));
    }

    public SessionAuditResponseDTO create(SessionAuditRequestDTO dto) {
        return toResponse(mongoTemplate.insert(toEntity(dto), collectionName));
    }

    @Async("auditTaskExecutor")
    public void createAsyncSafe(SessionAuditRequestDTO dto) {
        try {
            create(dto);
        } catch (Exception ex) {
            log.warn("Falha ao gravar log de sessao. userId={}, sessionId={}",
                dto != null ? dto.getUserId() : null,
                dto != null ? dto.getSessionId() : null,
                ex
            );
        }
    }

    @Async("auditTaskExecutor")
    public void revokeAsyncSafe(SessionAuditRequestDTO dto) {
        try {
            if (!StringUtils.hasText(dto.getSessionId())) {
                create(dto);
                return;
            }
            Query query = Query.query(Criteria.where("sessionId").is(dto.getSessionId()))
                .with(Sort.by(Sort.Direction.DESC, "date"))
                .limit(1);
            Update update = new Update()
                .set("state", firstText(dto.getState(), "REVOGADA"))
                .set("revokedAt", dto.getRevokedAt() != null ? dto.getRevokedAt() : LocalDateTime.now());
            if (dto.getExpiresAt() != null) {
                update.set("expiresAt", dto.getExpiresAt());
            }
            var result = mongoTemplate.updateFirst(query, update, SessionAuditLog.class, collectionName);
            if (result.getMatchedCount() == 0) {
                create(dto);
            }
        } catch (Exception ex) {
            log.warn("Falha ao revogar log de sessao. userId={}, sessionId={}",
                dto != null ? dto.getUserId() : null,
                dto != null ? dto.getSessionId() : null,
                ex
            );
        }
    }

    public SessionAuditResponseDTO findById(String id) {
        if (!ObjectId.isValid(id)) {
            throw new BusinessException("Id de sessao invalido.");
        }
        SessionAuditLog log = mongoTemplate.findById(new ObjectId(id), SessionAuditLog.class, collectionName);
        if (log == null) {
            throw new BusinessException("Sessao nao encontrada.");
        }
        return toResponse(log);
    }

    public SessionAuditPageResponseDTO findAll(
        String userId,
        String state,
        String authenticationMethod,
        LocalDate dateFrom,
        LocalDate dateTo,
        String ip,
        Integer page,
        Integer size
    ) {
        int safePage = page != null && page >= 0 ? page : DEFAULT_PAGE;
        int safeSize = size != null && size > 0 ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;
        Query baseQuery = buildQuery(userId, state, authenticationMethod, dateFrom, dateTo, ip);
        long total = mongoTemplate.count(baseQuery, SessionAuditLog.class, collectionName);
        Query pageQuery = buildQuery(userId, state, authenticationMethod, dateFrom, dateTo, ip)
            .with(Sort.by(Sort.Direction.DESC, "date"))
            .skip((long) safePage * safeSize)
            .limit(safeSize);
        List<SessionAuditResponseDTO> items = mongoTemplate.find(pageQuery, SessionAuditLog.class, collectionName)
            .stream()
            .map(this::toResponse)
            .toList();
        return new SessionAuditPageResponseDTO(total, safePage, safeSize, items);
    }

    public List<String> findStates() {
        return distinct("state");
    }

    public List<String> findUsers() {
        return distinct("userName");
    }

    private Query buildQuery(
        String userId,
        String state,
        String authenticationMethod,
        LocalDate dateFrom,
        LocalDate dateTo,
        String ip
    ) {
        List<Criteria> filters = new java.util.ArrayList<>();
        addEquals(filters, "userId", userId);
        addEquals(filters, "state", state);
        addEquals(filters, "authenticationMethod", authenticationMethod);
        addEquals(filters, "ip", ip);
        Criteria dateCriteria = buildDateCriteria(dateFrom, dateTo);
        if (dateCriteria != null) {
            filters.add(dateCriteria);
        }
        return filters.isEmpty() ? new Query() : new Query(new Criteria().andOperator(filters.toArray(Criteria[]::new)));
    }

    private Criteria buildDateCriteria(LocalDate dateFrom, LocalDate dateTo) {
        if (dateFrom == null && dateTo == null) {
            return null;
        }
        Criteria criteria = Criteria.where("date");
        if (dateFrom != null) {
            criteria.gte(dateFrom.atStartOfDay());
        }
        if (dateTo != null) {
            criteria.lt(dateTo.plusDays(1).atStartOfDay());
        }
        return criteria;
    }

    private void addEquals(List<Criteria> filters, String field, String value) {
        if (StringUtils.hasText(value)) {
            filters.add(Criteria.where(field).is(value.trim()));
        }
    }

    private List<String> distinct(String field) {
        return mongoTemplate.query(SessionAuditLog.class)
            .inCollection(collectionName)
            .distinct(field)
            .as(String.class)
            .all()
            .stream()
            .filter(StringUtils::hasText)
            .sorted()
            .toList();
    }

    private SessionAuditLog toEntity(SessionAuditRequestDTO dto) {
        UserAuditFields user = resolveUserFields(dto);
        SessionAuditLog log = new SessionAuditLog();
        log.setDate(LocalDateTime.now());
        log.setUserId(trim(dto.getUserId()));
        log.setUserName(firstText(dto.getUserName(), user.userName()));
        log.setUserEmail(firstText(dto.getUserEmail(), user.userEmail()));
        log.setIp(trim(dto.getIp()));
        log.setAuthenticationMethod(trim(dto.getAuthenticationMethod()));
        log.setState(firstText(dto.getState(), STATE_ACTIVE));
        log.setStartedAt(dto.getStartedAt() != null ? dto.getStartedAt() : LocalDateTime.now());
        log.setExpiresAt(dto.getExpiresAt());
        log.setRevokedAt(dto.getRevokedAt());
        log.setSessionId(trim(dto.getSessionId()));
        log.setUserAgent(trim(dto.getUserAgent()));
        log.setMetadata(dto.getMetadata());
        log.setAppDad(auditProperties.getDefaultAppDad());
        log.setAppName(auditProperties.getDefaultAppName());
        return log;
    }

    private SessionAuditResponseDTO toResponse(SessionAuditLog log) {
        SessionAuditResponseDTO dto = new SessionAuditResponseDTO();
        dto.setId(log.getId() != null ? log.getId().toHexString() : null);
        dto.setDate(log.getDate());
        dto.setUserId(log.getUserId());
        dto.setUserName(log.getUserName());
        dto.setUserEmail(log.getUserEmail());
        dto.setIp(log.getIp());
        dto.setAuthenticationMethod(log.getAuthenticationMethod());
        dto.setState(log.getState());
        dto.setStartedAt(log.getStartedAt());
        dto.setExpiresAt(log.getExpiresAt());
        dto.setRevokedAt(log.getRevokedAt());
        dto.setSessionId(log.getSessionId());
        dto.setUserAgent(log.getUserAgent());
        dto.setMetadata(log.getMetadata());
        dto.setAppDad(log.getAppDad());
        dto.setAppName(log.getAppName());
        return dto;
    }

    private UserAuditFields resolveUserFields(SessionAuditRequestDTO dto) {
        Integer id = parseInteger(dto.getUserId());
        if (id == null || (StringUtils.hasText(dto.getUserName()) && StringUtils.hasText(dto.getUserEmail()))) {
            return new UserAuditFields(null, null);
        }
        return userRepository.findById(id).map(this::toUserAuditFields).orElseGet(() -> new UserAuditFields(null, null));
    }

    private UserAuditFields toUserAuditFields(ZeeTUserEntity user) {
        return new UserAuditFields(user.getNome(), user.getEmail());
    }

    private Integer parseInteger(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private String trim(String value) {
        return StringUtils.hasText(value) ? value.trim() : value;
    }

    private record UserAuditFields(String userName, String userEmail) {
    }
}
