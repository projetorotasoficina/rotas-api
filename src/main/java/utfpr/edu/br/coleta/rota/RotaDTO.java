package utfpr.edu.br.coleta.rota;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

/**
 * DTO (Data Transfer Object) utilizado para transferir
 * informações de Rota entre camadas do sistema.
 *
 * Autor: Pedro Henrique Sauthiier
 */
@Data
public class RotaDTO {

    private Long id;

    /** Nome do tipo de coleta. */
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotNull(message = "O campo ativo é obrigatório.")
    private Boolean ativo;

    @Size(max = 400, message = "Observações podem ter no máximo 400 caracteres.")
    private String observacoes;

    @NotNull(message = "O tipo de resíduo é obrigatório.")
    private Long tipoResiduoId;

    @NotNull(message = "O tipo de coleta é obrigatório.")
    private Long tipoColetaId;

    /** Lista de dias da semana em que a rota deve ser realizada. */
    @Schema(description = "Lista de dias da semana em que a rota deve ser realizada", 
            example = "[SEGUNDA, QUARTA, SEXTA]")
    private List<DiaSemana> diasSemana;

    /** Período do dia em que a rota deve ser realizada. */
    @Schema(description = "Período do dia em que a rota deve ser realizada", 
            example = "MANHA", 
            allowableValues = {"MANHA", "TARDE", "NOITE"})
    private Periodo periodo;

}