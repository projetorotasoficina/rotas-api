package utfpr.edu.br.coleta.motorista;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import utfpr.edu.br.coleta.motorista.enums.CategoriaCNH;

import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) utilizado para transferir
 * informações de Motorista entre camadas do sistema.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Data
public class MotoristaDTO {

    private Long id;

    /** Nome completo do motorista. */
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    /** CPF do motorista, único e válido (11 dígitos). */
    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos.")
    private String cpf;

    /** Categoria da CNH do motorista (A, B, C, D ou E). Opcional. */
    @Schema(description = "Categoria da CNH do motorista",
            example = "C",
            allowableValues = {"A", "B", "C", "D", "E", "AB", "AC", "AD", "AE"})
    private CategoriaCNH cnhCategoria;

    /** Data de validade da CNH. Deve ser futura. Opcional. */
    @Future(message = "A validade da CNH deve ser uma data futura.")
    private LocalDate cnhValidade;

    /** Define se o motorista está ativo no sistema. */
    @NotNull(message = "O campo ativo é obrigatório.")
    private Boolean ativo;
}