package cv.zeemsv.api.infrastructure.minio;

import cv.zeemsv.api.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Log4j2
public class MinioStoreEngine {
    private final MinioProperties properties;

    public void saveFiles(List<DocumentMinioDTO> files) {
        if (!StringUtils.hasText(properties.getBucketName())) {
            throw new IllegalStateException("Bucket MinIO nao configurado.");
        }

        try {
            MinioClient client = client();
            initBucket(client);
            for (DocumentMinioDTO file : files) {
                log.info(
                    "A guardar documento no MinIO. Bucket: {}. Object: {}. Content-Type: {}. Bytes: {}",
                    properties.getBucketName(),
                    file.getName(),
                    file.getContentType(),
                    file.getFileSize()
                );
                client.putObject(PutObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(file.getName())
                    .stream(new ByteArrayInputStream(file.getByteContent()), file.getFileSize(), -1)
                    .contentType(file.getContentType())
                    .build());
                client.statObject(StatObjectArgs.builder()
                    .bucket(properties.getBucketName())
                    .object(file.getName())
                    .build());
                log.info(
                    "Documento confirmado no MinIO. Bucket: {}. Object: {}",
                    properties.getBucketName(),
                    file.getName()
                );
            }
        } catch (Exception ex) {
            throw new IllegalArgumentException("Falha ao tentar guardar documento(s).", ex);
        }
    }

    public byte[] getFileByPath(String objectPath) {
        if (!StringUtils.hasText(objectPath)) {
            return new byte[0];
        }

        try (InputStream stream = client().getObject(GetObjectArgs.builder()
            .bucket(properties.getBucketName())
            .object(objectPath)
            .build())) {
            return stream.readAllBytes();
        } catch (Exception ex) {
            throw new IllegalArgumentException("Falha ao tentar obter documento.", ex);
        }
    }

    private void initBucket(MinioClient client) throws Exception {
        boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(properties.getBucketName()).build());
        if (!found) {
            client.makeBucket(MakeBucketArgs.builder().bucket(properties.getBucketName()).build());
        }
    }

    private MinioClient client() {
        if (!StringUtils.hasText(properties.getUrl())
            || !StringUtils.hasText(properties.getAccessKey())
            || !StringUtils.hasText(properties.getSecretKey())) {
            throw new IllegalStateException("Configuracao MinIO incompleta.");
        }
        return MinioClient.builder()
            .endpoint(properties.getUrl())
            .credentials(properties.getAccessKey(), properties.getSecretKey())
            .build();
    }
}
