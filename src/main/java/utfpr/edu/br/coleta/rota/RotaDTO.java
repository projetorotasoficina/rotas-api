package utfpr.edu.br.coleta.rota;

import jakarta.validation.constraints.*;
import lombok.Data;

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

}