package utfpr.edu.br.coleta.rota.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import utfpr.edu.br.coleta.rota.enums.DiaSemana;
import utfpr.edu.br.coleta.rota.enums.Periodo;

/**
 * DTO que representa a frequência de uma rota em um dia específico.
 *
 * Autor: Sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Frequência da rota em um dia específico da semana")
public class FrequenciaRotaDTO {

    @NotNull(message = "O dia da semana é obrigatório.")
    @Schema(description = "Dia da semana", 
            example = "SEGUNDA",
            allowableValues = {"DOMINGO", "SEGUNDA", "TERCA", "QUARTA", "QUINTA", "SEXTA", "SABADO"})
    private DiaSemana diaSemana;

    @NotNull(message = "O período é obrigatório.")
    @Schema(description = "Período do dia", 
            example = "MANHA",
            allowableValues = {"MANHA", "TARDE", "NOITE"})
    private Periodo periodo;
}

