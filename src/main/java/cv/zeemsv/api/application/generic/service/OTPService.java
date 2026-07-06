package cv.zeemsv.api.application.generic.service;

import cv.zeemsv.api.domain.generic.model.OtpModel;
import cv.zeemsv.api.utils.JwtUtil;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class OTPService {
    private final EmailService emailService;
    private final ConcurrentHashMap<String, OtpModel> otpStorage = new ConcurrentHashMap<>();

    @Value("${application.session.otp-expiration-in-minutes:5}")
    private int otpExpirationTime;

    @Value("${application.mail.otp-subject:Código de verificação ZEEMSV}")
    private String otpSubject;

    public OtpModel sendOTP(String email) {
        String otp = String.valueOf(new SecureRandom().nextInt(900000) + 100000);
        OtpModel otpEntry = new OtpModel(email, JwtUtil.generateSecureHash(otp), otpExpirationTime, otp.length());
        otpStorage.put(email, otpEntry);

        emailService.sendText(email, otpSubject, buildOtpMessage(otp));
        log.info("OTP generated for {}.", email);
        log.debug("OTP value for {} is {}", email, otp);
        return otpEntry;
    }

    public boolean validateOtp(String email, String otp) {
        OtpModel storedOtp = otpStorage.get(email);
        if (storedOtp == null || storedOtp.isExpired()) {
            otpStorage.remove(email);
            return false;
        }

        if (storedOtp.getOtpHash().equals(JwtUtil.generateSecureHash(otp))) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

    private String buildOtpMessage(String otp) {
        return "O seu código de verificação ZEEMSV é: " + otp
            + "\n\nEste código expira em " + otpExpirationTime + " minuto(s).";
    }
}
