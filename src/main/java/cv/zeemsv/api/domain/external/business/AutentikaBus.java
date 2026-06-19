package cv.zeemsv.api.domain.external.business;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import cv.zeemsv.api.domain.external.model.AuthenticatedUserInfo;
import cv.zeemsv.api.domain.external.model.CniResponseModel;
import cv.zeemsv.api.exceptions.ExternalApiException;
import cv.zeemsv.api.utils.Constants;
import cv.zeemsv.api.utils.enums.LoginProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.io.FileInputStream;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AutentikaBus {
    private final RestClient.Builder restClientBuilder;

    @Value("${application.autentika.user-info-url:}")
    private String userInfoUrl;

    @Value("${firebase.config-path:}")
    private String firebaseConfig;

    public AuthenticatedUserInfo getUserInfo(String accessToken, LoginProvider loginProvider) {
        if (LoginProvider.GOOGLE.equals(loginProvider)) {
            return getGoogleUserInfo(accessToken);
        }
        return getAutentikaUserInfo(accessToken);
    }

    private AuthenticatedUserInfo getAutentikaUserInfo(String accessToken) {
        if (userInfoUrl == null || userInfoUrl.isBlank()) {
            throw new ExternalApiException("application.autentika.user-info-url is not configured.", HttpStatus.BAD_GATEWAY);
        }

        AuthenticatedUserInfo userInfo;
        try {
            userInfo = restClientBuilder.build()
                .get()
                .uri(userInfoUrl)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(AuthenticatedUserInfo.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == HttpStatus.UNAUTHORIZED.value()) {
                throw new ExternalApiException("Token Autentika invalido ou expirado.", HttpStatus.UNAUTHORIZED);
            }
            throw new ExternalApiException("Falha ao obter informacao do utilizador no Autentika.", HttpStatus.BAD_GATEWAY);
        }

        if (userInfo == null) {
            throw new ExternalApiException("Empty Autentika user-info response.", HttpStatus.BAD_GATEWAY);
        }
        normalizeAutentikaUserInfo(userInfo);
        return userInfo;
    }

    private AuthenticatedUserInfo getGoogleUserInfo(String accessToken) {
        try {
            initializeFirebaseIfNecessary();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(accessToken);
            return AuthenticatedUserInfo.builder()
                .sub(decodedToken.getUid())
                .email(decodedToken.getEmail())
                .name(decodedToken.getName())
                .build();
        } catch (FirebaseAuthException e) {
            throw new ExternalApiException("Token Google invalido ou expirado.", HttpStatus.UNAUTHORIZED);
        }
    }

    private void initializeFirebaseIfNecessary() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        if (firebaseConfig == null || firebaseConfig.isBlank()) {
            throw new ExternalApiException("firebase.config-path is not configured.", HttpStatus.BAD_GATEWAY);
        }
        try (FileInputStream serviceAccount = new FileInputStream(firebaseConfig)) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            throw new ExternalApiException("Arquivo de configuracao Firebase nao encontrado ou invalido.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void normalizeAutentikaUserInfo(AuthenticatedUserInfo userInfo) {
        if (userInfo.getName() == null || userInfo.getName().isBlank()) {
            userInfo.setName(userInfo.getSub());
        }
        if (userInfo.getSub() != null && userInfo.getSub().matches(Constants.EMAIL_REGEX)) {
            userInfo.setEmail(userInfo.getSub());
            userInfo.setSub(null);
        }
    }

    public CniResponseModel searchPersonByCNIorTRE(String nrDocument) {
        throw new ExternalApiException("CNI lookup is not configured in zeemsv-api.", HttpStatus.NOT_IMPLEMENTED);
    }
}
