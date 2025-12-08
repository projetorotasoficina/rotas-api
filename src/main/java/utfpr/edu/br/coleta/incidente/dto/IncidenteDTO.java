package utfpr.edu.br.coleta.incidente.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IncidenteDTO {
    private Long id;
    private Long trajetoId;
    private String nome;
    private String observacoes;
    private LocalDateTime ts;
    private Double lat;
    private Double lng;
    private String fotoUrl;
    private String rotaNome;
}
