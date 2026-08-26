package cv.zeemsv.api.application.audit.service;

import cv.zeemsv.api.application.audit.entity.AppLogs;
import cv.zeemsv.api.config.AuditMongoProperties;
import cv.zeemsv.api.infrastructure.mongodb.MongodbLogsRepository;
import jakarta.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class AppLogsService extends MongodbLogsRepository<AppLogs> {
    private static final int MAX_STACK_TRACE_LENGTH = 8000;

    public AppLogsService(MongoTemplate mongoTemplate, AuditMongoProperties mongoProperties) {
        super(mongoTemplate);
        setCollection(mongoProperties.getCollection().getApp());
    }

    public List<ObjectId> saveAppLog(List<AppLogs> appLogs) {
        return saveLogs(appLogs);
    }

    public List<AppLogs> filterLogs(Bson filter) {
        return loadLogs(50, filter, AppLogs.class);
    }

    public AppLogs findById(ObjectId id) {
        return findById(id, AppLogs.class);
    }

    @Async("auditTaskExecutor")
    public void saveErrorAsyncSafe(Throwable error, Integer status, HttpServletRequest request) {
        try {
            AppLogs appLog = new AppLogs();
            appLog.setDate(LocalDateTime.now());
            appLog.setLevel(resolveLevel(status));
            appLog.setMessage(error.getMessage());
            appLog.setExceptionClass(error.getClass().getName());
            appLog.setStackTrace(stackTrace(error));
            appLog.setStatus(status);
            appLog.setMethod(request != null ? request.getMethod() : null);
            appLog.setPath(request != null ? request.getRequestURI() : null);
            appLog.setQueryString(request != null ? request.getQueryString() : null);
            appLog.setRemoteAddr(request != null ? request.getRemoteAddr() : null);
            appLog.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
            saveLogs(List.of(appLog));
        } catch (Exception ex) {
            log.warn("Falha ao gravar log de erro da aplicacao.", ex);
        }
    }

    private String resolveLevel(Integer status) {
        if (status != null && status >= 500) {
            return "ERROR";
        }
        return "WARN";
    }

    private String stackTrace(Throwable error) {
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        String stackTrace = writer.toString();
        return stackTrace.length() <= MAX_STACK_TRACE_LENGTH
            ? stackTrace
            : stackTrace.substring(0, MAX_STACK_TRACE_LENGTH);
    }
}
