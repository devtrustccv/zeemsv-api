package cv.zeemsv.api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.config-path:}")
    private String firebaseConfig;

    @PostConstruct
    public void init() {
        if (firebaseConfig == null || firebaseConfig.isBlank()) {
            log.info("Firebase config path not configured; skipping Firebase initialization");
            return;
        }

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FileInputStream serviceAccount = new FileInputStream(firebaseConfig);
                FirebaseOptions options = new FirebaseOptions.Builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
                FirebaseApp.initializeApp(options);
                log.info("✅ FirebaseApp inicializado com sucesso");
            }
        } catch (IOException e) {
            log.error("❌ Erro ao carregar ficheiro de configuração do Firebase", e);
        }
    }
}
