package cv.zeemsv.api.application.generic.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${application.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${application.mail.from:no-reply@zeemsv.cv}")
    private String from;

    public void sendText(String to, String subject, String body) {
        if (!mailEnabled) {
            log.info("Mail provider disabled. Email to {} with subject '{}' was not sent.", to, subject);
            return;
        }

        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("Email recipient is required.");
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (MailException e) {
            throw new IllegalStateException("Erro ao enviar email.", e);
        }
    }
}
