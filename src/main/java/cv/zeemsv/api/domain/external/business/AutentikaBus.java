package cv.zeemsv.api.domain.external.business;

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

@Component
@RequiredArgsConstructor
public class AutentikaBus {
    private final RestClient.Builder restClientBuilder;

    @Value("${application.autentika.user-info-url:}")
    private String userInfoUrl;

    public AuthenticatedUserInfo getUserInfo(String accessToken, LoginProvider loginProvider) {
        if (userInfoUrl == null || userInfoUrl.isBlank()) {
            throw new ExternalApiException("application.autentika.user-info-url is not configured.", HttpStatus.BAD_GATEWAY);
        }

        AuthenticatedUserInfo userInfo = restClientBuilder.build()
            .get()
            .uri(userInfoUrl)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .body(AuthenticatedUserInfo.class);

        if (userInfo == null) {
            throw new ExternalApiException("Empty Autentika user-info response.", HttpStatus.BAD_GATEWAY);
        }
        if (userInfo.getName() == null || userInfo.getName().isBlank()) {
            userInfo.setName(userInfo.getSub());
        }
        if (userInfo.getSub() != null && userInfo.getSub().matches(Constants.EMAIL_REGEX)) {
            userInfo.setEmail(userInfo.getSub());
            userInfo.setSub(null);
        }
        return userInfo;
    }

    public CniResponseModel searchPersonByCNIorTRE(String nrDocument) {
        throw new ExternalApiException("CNI lookup is not configured in zeemsv-api.", HttpStatus.NOT_IMPLEMENTED);
    }
}
