package utfpr.edu.br.coleta.storage;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
        // se não estiver configurado (ex: em teste), só devolve um path fake e não quebra o contexto
        if (minioUrl == null || minioUrl.isBlank() || bucket == null || bucket.isBlank()) {
            // opcional: logar
            // log.warn("MinIO não configurado, retornando URL fake para testes");
            return "minio-disabled/" + folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        }

        try {
            String name = folder + "/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();

            client().putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(name)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            return minioUrl + "/" + bucket + "/" + name;

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload no MinIO", e);
        }
    }
}