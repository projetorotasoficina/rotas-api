package utfpr.edu.br.coleta.tiporesiduo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TipoResiduoDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "A cor (cor_hex) é obrigatória.")
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "A cor deve estar no formato #RRGGBB.")
    private String corHex;
}
