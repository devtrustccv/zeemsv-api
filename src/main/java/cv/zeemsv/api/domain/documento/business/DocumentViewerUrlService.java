package cv.zeemsv.api.domain.documento.business;

import cv.zeemsv.api.infrastructure.repository.ZeeTDocRelacaoRepository;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DocumentViewerUrlService {
    private static final String DEFAULT_MIMETYPE = "application/octet-stream";
    private static final String ALGO = "AES/ECB/PKCS5Padding";
    private static final String SECRET_KEY_SPEC = "AES";
    private static final String SECRET_KEY_ALGO = "SHA-1";
    private static final String SECRET_KEY_ENCRYPT_DB = "igrp.conf.db";

    private final ZeeTDocRelacaoRepository docRelacaoRepository;

    @Value("${application.docs.public-viewer-url:}")
    private String publicViewerUrl;

    public String toViewerUrl(String path) {
        if (!StringUtils.hasText(path)) {
            return null;
        }
        String mimetype = docRelacaoRepository.findFirstByPath(path)
            .map(doc -> doc.getMimetype())
            .filter(StringUtils::hasText)
            .orElse(DEFAULT_MIMETYPE);
        return toViewerUrl(path, mimetype);
    }

    public String toViewerUrl(String path, String mimetype) {
        if (!StringUtils.hasText(path) || !StringUtils.hasText(publicViewerUrl)) {
            return null;
        }
        String fileName = DocumentoBus.getFileNameWithExtensionByPath(path);
        String encryptedPath = encrypt(path);
        String separator = publicViewerUrl.contains("?") ? "&" : "?";
        return publicViewerUrl
            + separator
            + "path=" + encryptedPath
            + "&type=" + (StringUtils.hasText(mimetype) ? mimetype : DEFAULT_MIMETYPE)
            + "&download_name=" + fileName;
    }

    private String encrypt(String content) {
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(getSecretKey(), SECRET_KEY_SPEC));
            return new String(
                Base64.getUrlEncoder().encode(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8))),
                StandardCharsets.UTF_8
            );
        } catch (Exception ignored) {
            return content;
        }
    }

    private byte[] getSecretKey() throws Exception {
        MessageDigest sha = MessageDigest.getInstance(SECRET_KEY_ALGO);
        byte[] key = sha.digest(SECRET_KEY_ENCRYPT_DB.getBytes(StandardCharsets.UTF_8));
        return Arrays.copyOf(key, 16);
    }
}
