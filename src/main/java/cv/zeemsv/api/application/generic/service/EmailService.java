package cv.zeemsv.api.application.generic.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Log4j2
public class EmailService {
    private final JavaMailSender mailSender;
    private final TaskExecutor emailTaskExecutor;

    @Value("${application.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${application.mail.from:no-reply@zeemsv.cv}")
    private String from;

    public EmailService(JavaMailSender mailSender, @Qualifier("emailTaskExecutor") TaskExecutor emailTaskExecutor) {
        this.mailSender = mailSender;
        this.emailTaskExecutor = emailTaskExecutor;
    }

    public void sendText(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("Mail provider disabled. Email to {} with subject '{}' was not sent.", to, subject);
            return;
        }

        if (!StringUtils.hasText(to)) {
            log.warn("Email recipient is required. Email with subject '{}' was not queued.", subject);
            return;
        }

        try {
            emailTaskExecutor.execute(() -> sendTextNow(to, subject, body));
        } catch (TaskRejectedException e) {
            log.error("Email queue rejected message to {} with subject '{}'.", to, subject, e);
        }
    }

    private void sendTextNow(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} with subject '{}'.", to, subject);
        } catch (MailException e) {
            log.error("Erro ao enviar email para {} com assunto '{}'.", to, subject, e);
        }
    }
}
