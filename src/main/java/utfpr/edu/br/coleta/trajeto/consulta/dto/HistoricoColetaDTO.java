package utfpr.edu.br.coleta.trajeto.consulta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO que representa um registro do histórico de coleta.
 * 
 * Autor: Sistema Rotas API
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Registro do histórico de coleta")
public class HistoricoColetaDTO {

    @Schema(description = "ID do trajeto")
    private Long trajetoId;

    @Schema(description = "Nome da rota")
    private String nomeRota;

    @Schema(description = "Tipo de resíduo coletado")
    private String tipoResiduo;

    @Schema(description = "Tipo de coleta")
    private String tipoColeta;

    @Schema(description = "Data e hora de início do trajeto")
    private LocalDateTime dataInicio;

    @Schema(description = "Data e hora de fim do trajeto")
    private LocalDateTime dataFim;

    @Schema(description = "Nome do motorista")
    private String nomeMotorista;

    @Schema(description = "Placa do caminhão")
    private String placaCaminhao;

    @Schema(description = "Distância total percorrida (em km)")
    private Double distanciaTotal;

    @Schema(description = "Status do trajeto")
    private String status;
}
