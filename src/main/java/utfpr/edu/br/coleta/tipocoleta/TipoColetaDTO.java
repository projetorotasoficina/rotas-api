package utfpr.edu.br.coleta.tipocoleta;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO (Data Transfer Object) utilizado para transferir
 * informações de Tipo de coleta entre camadas do sistema.
 *
 * Autor: Pedro Henrique Sauthiier
 */
@Data
public class TipoColetaDTO {

    private Long id;

    /** Nome do tipo de coleta. */
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

}