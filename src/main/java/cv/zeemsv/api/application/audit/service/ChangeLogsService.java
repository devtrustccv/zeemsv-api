package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.dto.AuditContext;
import cv.zeemsv.api.application.audit.entity.ChangeLogs;
import cv.zeemsv.api.application.audit.entity.ChangeLogsItem;
import cv.zeemsv.api.config.AuditMongoProperties;
import cv.zeemsv.api.config.AuditProperties;
import cv.zeemsv.api.infrastructure.mongodb.MongodbLogsRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Log4j2
public class ChangeLogsService extends MongodbLogsRepository<ChangeLogs> {
    private final AuditProperties auditProperties;

    public ChangeLogsService(
        MongoTemplate mongoTemplate,
        AuditMongoProperties mongoProperties,
        AuditProperties auditProperties
    ) {
        super(mongoTemplate);
        this.auditProperties = auditProperties;
        setCollection(mongoProperties.getCollection().getChanges());
    }

    public List<ObjectId> saveChangeLog(List<ChangeLogs> changeLogsList) {
        applyDefaultAppInfo(changeLogsList);
        return saveLogs(changeLogsList);
    }

    public List<ObjectId> saveChangeLog(List<ChangeLogs> changeLogsList, AuditContext context) {
        applyContext(changeLogsList, context);
        applyDefaultAppInfo(changeLogsList);
        return saveLogs(changeLogsList);
    }

    public List<ObjectId> saveChangeLog(
        LocalDateTime date,
        String action,
        String tableName,
        String tableId,
        List<ChangeLogsItem> logsItems,
        String obs,
        AuditContext context
    ) {
        ChangeLogs changeLogs = new ChangeLogs();
        changeLogs.setDate(date == null ? LocalDateTime.now() : date);
        changeLogs.setAction(action);
        changeLogs.setTableName(tableName);
        changeLogs.setTableId(tableId);
        changeLogs.setLogsItems(logsItems);
        changeLogs.setObs(obs);

        return saveChangeLog(List.of(changeLogs), context);
    }

    public List<ChangeLogs> filterLogs(Bson filter) {
        return loadLogs(50, filter, ChangeLogs.class);
    }

    public List<ChangeLogs> filterLogs(int limit, Bson filter) {
        return loadLogs(limit, filter, ChangeLogs.class);
    }

    public List<ChangeLogs> filterLogs(int limit, Bson filter, Bson sort) {
        return loadLogs(limit, filter, sort, ChangeLogs.class);
    }

    public ChangeLogs findById(ObjectId id) {
        return findById(id, ChangeLogs.class);
    }

    public List<ObjectId> createLog(
        String action,
        String tableName,
        String tableId,
        String obs,
        AuditContext context
    ) {
        ChangeLogs changeLogs = new ChangeLogs();
        changeLogs.setAction(action);
        changeLogs.setTableName(tableName);
        changeLogs.setTableId(tableId);
        changeLogs.setDate(LocalDateTime.now());
        changeLogs.setObs(obs);
        return saveChangeLog(List.of(changeLogs), context);
    }

    public List<ObjectId> createLogs(
        List<ChangeLogsItem> logsItems,
        String action,
        String tableName,
        String tableId,
        String obs,
        AuditContext context
    ) {
        ChangeLogs changeLogs = new ChangeLogs();
        changeLogs.setAction(action);
        changeLogs.setTableName(tableName);
        changeLogs.setTableId(tableId);
        changeLogs.setDate(LocalDateTime.now());
        changeLogs.setObs(obs);
        changeLogs.setLogsItems(logsItems == null ? new ArrayList<>() : logsItems);
        return saveChangeLog(List.of(changeLogs), context);
    }

    @Async("auditTaskExecutor")
    public void createLogsAsyncSafe(
        List<ChangeLogsItem> logsItems,
        String action,
        String tableName,
        String tableId,
        String obs,
        AuditContext context
    ) {
        try {
            createLogs(logsItems, action, tableName, tableId, obs, context);
        } catch (Exception ex) {
            log.warn("Falha ao gravar log de auditoria. action={}, tableName={}, tableId={}", action, tableName, tableId, ex);
        }
    }

    private void applyContext(List<ChangeLogs> changeLogsList, AuditContext context) {
        if (context == null || changeLogsList == null) {
            return;
        }

        changeLogsList.forEach(changeLogs -> {
            changeLogs.setUserId(context.getUserId());
            changeLogs.setUserEmail(context.getUserEmail());
            changeLogs.setProfileId(context.getProfileId());
            changeLogs.setProfileName(context.getProfileName());
            changeLogs.setOrgId(context.getOrgId());
            changeLogs.setOrgName(context.getOrgName());
            changeLogs.setAppDad(context.getAppDad());
            changeLogs.setAppName(context.getAppName());
        });
    }

    private void applyDefaultAppInfo(List<ChangeLogs> changeLogsList) {
        if (changeLogsList == null) {
            return;
        }

        changeLogsList.forEach(changeLogs -> {
            if (!StringUtils.hasText(changeLogs.getAppDad())) {
                changeLogs.setAppDad(auditProperties.getDefaultAppDad());
            }
            if (!StringUtils.hasText(changeLogs.getAppName())) {
                changeLogs.setAppName(auditProperties.getDefaultAppName());
            }
        });
    }
}
