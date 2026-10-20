package cv.zeemsv.api.application.audit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "application.audit", name = "backfill-context-enabled", havingValue = "true")
public class TransactionAuditContextBackfill implements ApplicationRunner {
    private final TransactionAuditService service;

    @Override
    public void run(ApplicationArguments args) {
        service.backfillContext();
    }
}
