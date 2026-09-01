package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.dto.TransactionAuditPageResponseDTO;
import cv.zeemsv.api.application.audit.dto.TransactionAuditRequestDTO;
import cv.zeemsv.api.application.audit.dto.TransactionAuditResponseDTO;
import cv.zeemsv.api.application.audit.entity.TransactionAuditLog;
import cv.zeemsv.api.config.AuditMongoProperties;
import cv.zeemsv.api.config.AuditProperties;
import cv.zeemsv.api.exceptions.BusinessException;
import cv.zeemsv.api.infrastructure.entity.ZeeTUserEntity;
import cv.zeemsv.api.infrastructure.repository.ZeeTUserRepository;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Log4j2
public class TransactionAuditService {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 200;
    private static final String ACTION_CREATE = "CREATE";
    private static final String ACTION_DELETE = "DELETE";
    private static final String ACTION_LIST = "LIST";
    private static final String ACTION_UPDATE = "UPDATE";
    private static final String ACTION_VIEW_DETAIL = "VIEW_DETAIL";
    private static final String ACTION_UNKNOWN = "UNKNOWN";
    private static final Map<String, String> RESOURCE_ALIASES = Map.ofEntries(
        Map.entry("atividades", "atividade"),
        Map.entry("cobrancas", "cobranca"),
        Map.entry("documentos", "documento"),
        Map.entry("investidors", "investidor"),
        Map.entry("investidores", "investidor"),
        Map.entry("lotes", "lote"),
        Map.entry("ordens", "ordem"),
        Map.entry("pagamentos", "pagamento"),
        Map.entry("projetos", "projeto"),
        Map.entry("projectos", "projeto"),
        Map.entry("servicos", "servico"),
        Map.entry("solicitacoes", "solicitacao"),
        Map.entry("utilizadores", "utilizador"),
        Map.entry("users", "user")
    );

    private final MongoTemplate mongoTemplate;
    private final ZeeTUserRepository userRepository;
    private final AuditProperties auditProperties;
    private final String collectionName;

    public TransactionAuditService(
        MongoTemplate mongoTemplate,
        ZeeTUserRepository userRepository,
        AuditMongoProperties mongoProperties,
        AuditProperties auditProperties
    ) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.auditProperties = auditProperties;
        this.collectionName = mongoProperties.getCollection().getTransactions();
    }

    @PostConstruct
    public void ensureIndexes() {
        try {
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("date", Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("userId", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("actionType", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("tableName", Sort.Direction.ASC).on("tableId", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("module", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("ip", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on("metadata.idInvestidor", Sort.Direction.ASC).on("date", Sort.Direction.DESC));
        } catch (Exception ex) {
            log.warn("Falha ao criar indices de auditoria transacional. collection={}", collectionName, ex);
        }
    }

    public TransactionAuditResponseDTO create(TransactionAuditRequestDTO dto) {
        TransactionAuditLog log = toEntity(dto);
        return toResponse(mongoTemplate.insert(log, collectionName));
    }

    @Async("auditTaskExecutor")
    public void createAsyncSafe(TransactionAuditRequestDTO dto) {
        try {
            create(dto);
        } catch (Exception ex) {
            log.warn("Falha ao gravar auditoria transacional. actionType={}, tableName={}, tableId={}",
                dto != null ? dto.getActionType() : null,
                dto != null ? dto.getTableName() : null,
                dto != null ? dto.getTableId() : null,
                ex
            );
        }
    }

    public TransactionAuditResponseDTO findById(String id) {
        if (!ObjectId.isValid(id)) {
            throw new BusinessException("Id de auditoria invalido.");
        }
        TransactionAuditLog log = mongoTemplate.findById(new ObjectId(id), TransactionAuditLog.class, collectionName);
        if (log == null) {
            throw new BusinessException("Registo de auditoria nao encontrado.");
        }
        return toResponse(log);
    }

    public TransactionAuditPageResponseDTO findAll(
        String userId,
        String actionType,
        LocalDate dateFrom,
        LocalDate dateTo,
        String tableName,
        String tableId,
        String module,
        Integer idInvestidor,
        String ip,
        Integer page,
        Integer size
    ) {
        int safePage = page != null && page >= 0 ? page : DEFAULT_PAGE;
        int safeSize = size != null && size > 0 ? Math.min(size, MAX_SIZE) : DEFAULT_SIZE;

        Query baseQuery = buildQuery(userId, actionType, dateFrom, dateTo, tableName, tableId, module, idInvestidor, ip);
        long total = mongoTemplate.count(baseQuery, TransactionAuditLog.class, collectionName);

        Query pageQuery = buildQuery(userId, actionType, dateFrom, dateTo, tableName, tableId, module, idInvestidor, ip)
            .with(Sort.by(Sort.Direction.DESC, "date"))
            .skip((long) safePage * safeSize)
            .limit(safeSize);

        List<TransactionAuditResponseDTO> items = mongoTemplate
            .find(pageQuery, TransactionAuditLog.class, collectionName)
            .stream()
            .map(this::toResponse)
            .toList();

        return new TransactionAuditPageResponseDTO(total, safePage, safeSize, items);
    }

    public List<String> findActionTypes() {
        return mongoTemplate.query(TransactionAuditLog.class)
            .inCollection(collectionName)
            .distinct("actionType")
            .as(String.class)
            .all()
            .stream()
            .filter(StringUtils::hasText)
            .sorted()
            .toList();
    }

    public List<String> findUsers() {
        return mongoTemplate.query(TransactionAuditLog.class)
            .inCollection(collectionName)
            .distinct("userName")
            .as(String.class)
            .all()
            .stream()
            .filter(StringUtils::hasText)
            .sorted()
            .toList();
    }

    private Query buildQuery(
        String userId,
        String actionType,
        LocalDate dateFrom,
        LocalDate dateTo,
        String tableName,
        String tableId,
        String module,
        Integer idInvestidor,
        String ip
    ) {
        List<Criteria> filters = new java.util.ArrayList<>();

        addEquals(filters, "userId", userId);
        addEquals(filters, "actionType", actionType);
        addEquals(filters, "tableName", tableName);
        addEquals(filters, "tableId", tableId);
        addEquals(filters, "module", module);
        addEquals(filters, "metadata.idInvestidor", idInvestidor);
        addEquals(filters, "ip", ip);

        Criteria dateCriteria = buildDateCriteria(dateFrom, dateTo);
        if (dateCriteria != null) {
            filters.add(dateCriteria);
        }

        if (filters.isEmpty()) {
            return new Query();
        }
        return new Query(new Criteria().andOperator(filters.toArray(Criteria[]::new)));
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

    private void addEquals(List<Criteria> filters, String field, Integer value) {
        if (value != null) {
            filters.add(Criteria.where(field).is(value));
        }
    }

    private TransactionAuditLog toEntity(TransactionAuditRequestDTO dto) {
        InferredAuditFields inferred = inferFields(dto.getRequestMethod(), dto.getRequestUri());
        UserAuditFields user = resolveUserFields(dto);
        TransactionAuditLog log = new TransactionAuditLog();
        log.setDate(LocalDateTime.now());
        log.setUserId(trim(dto.getUserId()));
        log.setUserName(firstText(dto.getUserName(), user.userName()));
        log.setUserEmail(firstText(dto.getUserEmail(), user.userEmail()));
        log.setActionType(firstText(dto.getActionType(), inferred.actionType(), ACTION_UNKNOWN));
        log.setActionLabel(firstText(dto.getActionLabel(), inferred.actionLabel()));
        log.setDescription(firstText(dto.getDescription(), inferred.description()));
        log.setTableName(firstText(dto.getTableName(), inferred.tableName()));
        log.setTableId(firstText(dto.getTableId(), inferred.tableId()));
        log.setModule(firstText(dto.getModule(), inferred.module()));
        log.setIp(trim(dto.getIp()));
        log.setUserAgent(trim(dto.getUserAgent()));
        log.setRequestMethod(trim(dto.getRequestMethod()));
        log.setRequestUri(trim(dto.getRequestUri()));
        log.setStatusCode(dto.getStatusCode());
        log.setMetadata(dto.getMetadata());
        log.setAppDad(auditProperties.getDefaultAppDad());
        log.setAppName(auditProperties.getDefaultAppName());
        return log;
    }

    private TransactionAuditResponseDTO toResponse(TransactionAuditLog log) {
        TransactionAuditResponseDTO dto = new TransactionAuditResponseDTO();
        dto.setId(log.getId() != null ? log.getId().toHexString() : null);
        dto.setDate(log.getDate());
        dto.setUserId(log.getUserId());
        dto.setUserName(log.getUserName());
        dto.setUserEmail(log.getUserEmail());
        dto.setActionType(log.getActionType());
        dto.setActionLabel(log.getActionLabel());
        dto.setDescription(log.getDescription());
        dto.setTableName(log.getTableName());
        dto.setTableId(log.getTableId());
        dto.setModule(log.getModule());
        dto.setIp(log.getIp());
        dto.setUserAgent(log.getUserAgent());
        dto.setRequestMethod(log.getRequestMethod());
        dto.setRequestUri(log.getRequestUri());
        dto.setStatusCode(log.getStatusCode());
        dto.setMetadata(log.getMetadata());
        dto.setAppDad(log.getAppDad());
        dto.setAppName(log.getAppName());
        return dto;
    }

    private String trim(String value) {
        return StringUtils.hasText(value) ? value.trim() : value;
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (StringUtils.hasText(value)) {
                return value.trim();
            }
        }
        return null;
    }

    private UserAuditFields resolveUserFields(TransactionAuditRequestDTO dto) {
        Integer id = parseInteger(dto.getUserId());
        if (id == null || (StringUtils.hasText(dto.getUserName()) && StringUtils.hasText(dto.getUserEmail()))) {
            return new UserAuditFields(null, null);
        }
        return userRepository.findById(id)
            .map(this::toUserAuditFields)
            .orElseGet(() -> new UserAuditFields(null, null));
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

    private InferredAuditFields inferFields(String requestMethod, String requestUri) {
        String actionType = inferActionType(requestMethod, requestUri);
        List<String> parts = splitPath(requestUri);
        String resource = resolveResource(parts);
        String tableId = resolveTableId(parts);
        String module = resource != null ? normalizeModule(resource) : null;
        String tableName = resource != null ? "zee_t_" + normalizeTableName(resource) : null;
        String actionLabel = actionLabel(actionType);
        String description = buildDescription(actionLabel, module, tableId);
        return new InferredAuditFields(actionType, actionLabel, description, tableName, tableId, module);
    }

    private String inferActionType(String requestMethod, String requestUri) {
        String method = StringUtils.hasText(requestMethod) ? requestMethod.trim().toUpperCase(Locale.ROOT) : "";
        return switch (method) {
            case "POST" -> ACTION_CREATE;
            case "PUT", "PATCH" -> ACTION_UPDATE;
            case "DELETE" -> ACTION_DELETE;
            case "GET" -> resolveTableId(splitPath(requestUri)) != null ? ACTION_VIEW_DETAIL : ACTION_LIST;
            default -> ACTION_UNKNOWN;
        };
    }

    private List<String> splitPath(String requestUri) {
        if (!StringUtils.hasText(requestUri)) {
            return List.of();
        }
        String path = requestUri.split("\\?", 2)[0];
        return Arrays.stream(path.split("/"))
            .filter(StringUtils::hasText)
            .toList();
    }

    private String resolveResource(List<String> parts) {
        int apiIndex = parts.indexOf("v1");
        if (apiIndex >= 0 && parts.size() > apiIndex + 1) {
            return normalizeResource(parts.get(apiIndex + 1));
        }
        return parts.isEmpty() ? null : normalizeResource(parts.get(0));
    }

    private String resolveTableId(List<String> parts) {
        return parts.stream()
            .filter(part -> part.matches("\\d+"))
            .findFirst()
            .orElse(null);
    }

    private String normalizeResource(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String clean = value.trim().replace("-", "_").toLowerCase(Locale.ROOT);
        String alias = RESOURCE_ALIASES.get(clean);
        if (alias != null) {
            return alias;
        }
        if (clean.endsWith("s") && clean.length() > 1) {
            return clean.substring(0, clean.length() - 1);
        }
        return clean;
    }

    private String normalizeModule(String resource) {
        return resource.replace("-", "_").toUpperCase(Locale.ROOT);
    }

    private String normalizeTableName(String resource) {
        return resource.replace("-", "_").toLowerCase(Locale.ROOT);
    }

    private String actionLabel(String actionType) {
        return switch (actionType) {
            case ACTION_CREATE -> "Submissao de formulario";
            case ACTION_UPDATE -> "Edicao";
            case ACTION_DELETE -> "Eliminacao";
            case ACTION_LIST -> "Listagem";
            case ACTION_VIEW_DETAIL -> "Acesso ao detalhe";
            default -> "Acao nao identificada";
        };
    }

    private String buildDescription(String actionLabel, String module, String tableId) {
        if (!StringUtils.hasText(module)) {
            return actionLabel;
        }
        String readableModule = module.toLowerCase(Locale.ROOT).replace("_", " ");
        if (StringUtils.hasText(tableId)) {
            return actionLabel + " de " + readableModule + " #" + tableId;
        }
        return actionLabel + " de " + readableModule;
    }

    private record InferredAuditFields(
        String actionType,
        String actionLabel,
        String description,
        String tableName,
        String tableId,
        String module
    ) {
    }

    private record UserAuditFields(String userName, String userEmail) {
    }
}
