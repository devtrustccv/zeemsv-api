package cv.zeemsv.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import cv.zeemsv.api.utils.FirebaseCredentialsHelper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config-path:}")
    private String firebaseConfig;

    @Value("${firebase.config-json:}")
    private String firebaseConfigJson;

    @Value("${firebase.config-base64:}")
    private String firebaseConfigBase64;

    @PostConstruct
    public void init() {
        if (!FirebaseCredentialsHelper.isConfigured(firebaseConfig, firebaseConfigJson, firebaseConfigBase64)) {
            log.info("Firebase config not configured; skipping Firebase initialization");
            return;
        }

        if (!FirebaseCredentialsHelper.canLoad(firebaseConfig, firebaseConfigJson, firebaseConfigBase64)) {
            log.warn("Firebase config path not found; skipping Firebase initialization");
            return;
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials credentials = FirebaseCredentialsHelper.load(
                    firebaseConfig,
                    firebaseConfigJson,
                    firebaseConfigBase64
                );
                FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
                FirebaseApp.initializeApp(options);
                log.info("FirebaseApp inicializado com sucesso");
            }
        } catch (IOException | IllegalArgumentException e) {
            log.warn("Firebase config invalid; skipping Firebase initialization: {}", e.getMessage());
        }
    }
}
