package utfpr.edu.br.coleta.caminhao;


import lombok.Data;

@Data
public class CaminhaoDTO {
    private Long id;
    private String modelo;
    private String placa;
    private Long tipoColetaId;
    private Long residuoId;
    private Boolean ativo;
}