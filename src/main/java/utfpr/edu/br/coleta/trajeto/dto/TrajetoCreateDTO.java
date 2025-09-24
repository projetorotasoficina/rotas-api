package utfpr.edu.br.coleta.trajeto.dto;

import lombok.Data;

@Data
public class TrajetoCreateDTO {
    private Long rotaId;
    private Long caminhaoId;
    private Long motoristaId;
}