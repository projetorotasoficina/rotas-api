package utfpr.edu.br.coleta.incidente.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncidenteCreateDTO {
    private Long trajetoId;
    private String nome;
    private String observacoes;
    private Double lat;
    private Double lng;
}