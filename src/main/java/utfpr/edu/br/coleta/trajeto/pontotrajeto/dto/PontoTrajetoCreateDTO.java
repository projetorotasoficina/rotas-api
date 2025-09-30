package utfpr.edu.br.coleta.trajeto.pontotrajeto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PontoTrajetoCreateDTO {
    private Long trajetoId;
    private double latitude;
    private double longitude;
    private LocalDateTime horario;
    private String observacao;
}