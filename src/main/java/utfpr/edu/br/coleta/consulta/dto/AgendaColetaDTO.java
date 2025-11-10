package utfpr.edu.br.coleta.consulta.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.rota.enums.DiaSemana;
import utfpr.edu.br.coleta.rota.enums.Periodo;

/**
 * DTO que representa um item da agenda de coleta para um endereço.
 * 
 * Autor: Sistema Rotas API
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Item da agenda de coleta")
public class AgendaColetaDTO {

    @Schema(description = "Nome da rota")
    private String nomeRota;

    @Schema(description = "Tipo de resíduo coletado")
    private String tipoResiduo;

    @Schema(description = "Tipo de coleta")
    private String tipoColeta;

    @Schema(description = "Dia da semana da coleta")
    private DiaSemana diaSemana;

    @Schema(description = "Período do dia da coleta")
    private Periodo periodo;

    @Schema(description = "Descrição do período (ex: 'Manhã: 06:00 - 12:00')")
    private String descricaoPeriodo;

    @Schema(description = "Observações sobre a rota")
    private String observacoes;
}
