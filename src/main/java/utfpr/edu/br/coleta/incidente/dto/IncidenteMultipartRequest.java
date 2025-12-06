package utfpr.edu.br.coleta.incidente.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

// Este DTO é usado APENAS para a requisição multipart/form-data
public class IncidenteMultipartRequest {

    // Campos do IncidenteDTO (ajuste os tipos conforme o seu DTO real)
    @Schema(description = "ID do Trajeto ao qual o incidente pertence", example = "1")
    private Long trajetoId;

    @Schema(description = "Nome/Título do incidente", example = "Buraco na Pista")
    private String nome;

    @Schema(description = "Observações detalhadas sobre o incidente", example = "Buraco grande na faixa da direita.")
    private String observacoes;

    @Schema(description = "Timestamp do incidente (ISO 8601)", example = "2025-12-06T00:00:00Z")
    private String ts; // Ou Instant/LocalDateTime, dependendo do seu DTO

    @Schema(description = "Latitude do incidente", example = "-25.4284")
    private Double lat;

    @Schema(description = "Longitude do incidente", example = "-49.2733")
    private Double lng;

    // Campo da Foto - Crucial para o Swagger
    @Schema(description = "Arquivo da foto do incidente", type = "string", format = "binary")
    private MultipartFile foto;

    // Getters e Setters (Obrigatórios)
    // ...
}
