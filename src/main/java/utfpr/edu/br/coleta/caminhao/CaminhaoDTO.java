package utfpr.edu.br.coleta.caminhao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import utfpr.edu.br.coleta.caminhao.enums.TipoVeiculo;

@Data
public class CaminhaoDTO {
    private Long id;
    private String modelo;
    private String placa;
    private Long tipoColetaId;
    private Long residuoId;
    
    @Schema(description = "Tipo de veículo (categoria de caminhão)",
            example = "CAMINHAO_MEDIO",
            allowableValues = {"VUC", "CAMINHAO_LEVE", "CAMINHAO_MEDIO", "CAMINHAO_PESADO", "CAMINHAO_CARRETA"})
    private TipoVeiculo tipoVeiculo;
    
    private Boolean ativo;
}