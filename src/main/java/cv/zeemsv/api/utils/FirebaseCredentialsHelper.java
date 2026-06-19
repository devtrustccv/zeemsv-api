package cv.zeemsv.api.utils;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class FirebaseCredentialsHelper {
    private FirebaseCredentialsHelper() {
    }

    public static GoogleCredentials load(String configPath, String configJson, String configBase64) throws IOException {
        try (InputStream inputStream = open(configPath, configJson, configBase64)) {
            return GoogleCredentials.fromStream(inputStream);
        }
    }

    public static boolean isConfigured(String configPath, String configJson, String configBase64) {
        return hasText(configPath) || hasText(configJson) || hasText(configBase64);
    }

    public static boolean canLoad(String configPath, String configJson, String configBase64) {
        if (hasText(configJson) || hasText(configBase64)) {
            return true;
        }
        if (!hasText(configPath)) {
            return false;
        }
        if (configPath.startsWith("classpath:")) {
            String resourcePath = configPath.substring("classpath:".length());
            return new ClassPathResource(resourcePath).exists();
        }
        String filePath = configPath.startsWith("file:") ? configPath.substring("file:".length()) : configPath;
        return new File(filePath).isFile();
    }

    private static InputStream open(String configPath, String configJson, String configBase64) throws IOException {
        if (hasText(configJson)) {
            return new ByteArrayInputStream(configJson.getBytes(StandardCharsets.UTF_8));
        }
        if (hasText(configBase64)) {
            return new ByteArrayInputStream(Base64.getDecoder().decode(configBase64));
        }
        if (hasText(configPath)) {
            if (configPath.startsWith("classpath:")) {
                String resourcePath = configPath.substring("classpath:".length());
                ClassPathResource resource = new ClassPathResource(resourcePath);
                if (!resource.exists()) {
                    throw new IOException("Firebase classpath resource not found: " + resourcePath);
                }
                return resource.getInputStream();
            }
            String filePath = configPath.startsWith("file:") ? configPath.substring("file:".length()) : configPath;
            return new FileInputStream(filePath);
        }
        throw new IOException("Firebase credentials are not configured.");
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
