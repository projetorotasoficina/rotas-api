package utfpr.edu.br.coleta.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MinioService {

    @Value("${minio.url:}")
    private String minioUrl;

    @Value("${minio.bucket:}")
    private String bucket;

    @Value("${minio.access-key:}")
    private String access;

    @Value("${minio.secret-key:}")
    private String secret;

    private MinioClient client() {
        if (minioUrl == null || minioUrl.isBlank()) {
            throw new IllegalStateException("MinIO não configurado (minio.url vazio).");
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
                c.makeBucket(
                        MakeBucketArgs.builder().bucket(bucket).build()
                );
            }

            String objectName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            c.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioUrl + "/" + bucket + "/" + objectName;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload do arquivo para o MinIO", e);
        }
    }
}