package utfpr.edu.br.coleta.tiporesiduo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;

@Entity
@Table(
        name = "tb_tipo_residuo",
        uniqueConstraints = @UniqueConstraint(columnNames = "nome")
)
@Getter
@Setter
@NoArgsConstructor
public class TipoResiduo extends BaseEntity {

    @NotBlank(message = "Um nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    /**
     * Cor no formato #RRGGBB (ex: #1A2B3C)
     */
    @NotBlank(message = "A cor (cor_hex) é obrigatória.")
    @Pattern(regexp = "^#[A-Fa-f0-9]{6}$", message = "A cor deve estar no formato #RRGGBB.")
    @Column(name = "cor_hex", nullable = false, length = 7)
    private String corHex;
}
