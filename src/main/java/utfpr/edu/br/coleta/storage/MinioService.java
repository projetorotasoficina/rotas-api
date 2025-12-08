package utfpr.edu.br.coleta.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MinioService {

    private static final Logger log = LoggerFactory.getLogger(MinioService.class);

    @Value("${MINIO_ENDPOINT}")
    private String minioUrl;

    @Value("${MINIO_BUCKET}")
    private String bucket;

    @Value("${MINIO_ACCESS_KEY}")
    private String access;

    @Value("${MINIO_SECRET_KEY}")
    private String secret;

    private MinioClient client() {
        log.info("Inicializando MinIO. endpoint={}, bucket={}", minioUrl, bucket);

        if (minioUrl == null || minioUrl.isBlank()) {
            throw new IllegalStateException("MinIO não configurado (MINIO_ENDPOINT vazio).");
        }

        return MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(access, secret)
                .build();
    }

    public String uploadFile(MultipartFile file, String folder) {
        try {
            MinioClient c = client();

            boolean exists = c.bucketExists(
                    BucketExistsArgs.builder().bucket(bucket).build()
            );
            if (!exists) {
                log.info("Bucket {} não existe, criando...", bucket);
                c.makeBucket(
                        MakeBucketArgs.builder().bucket(bucket).build()
                );
            }

            String objectName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            log.info("Enviando objeto para MinIO. bucket={}, object={}", bucket, objectName);

            c.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            String url = minioUrl + "/" + bucket + "/" + objectName;
            log.info("Upload MinIO OK. URL={}", url);

            return url;

        } catch (Exception e) {
            log.error("Erro ao fazer upload do arquivo para o MinIO", e);
            throw new RuntimeException("Erro ao fazer upload do arquivo para o MinIO", e);
        }
    }
}
