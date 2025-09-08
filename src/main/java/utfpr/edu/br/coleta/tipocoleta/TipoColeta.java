package utfpr.edu.br.coleta.tipocoleta;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;

/**
 * Entidade que representa um Tipo de coleta no sistema de coleta.
 *
 * Contém informações pessoais e operacionais necessárias
 * para vinculação em trajetos e caminhões.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Entity
@Table(name = "tb_tipo_coleta")
@Getter
@Setter
@NoArgsConstructor
public class TipoColeta extends BaseEntity {

    /** Nome/Descrição do tipo de coleta. */
    @NotBlank(message = "Um nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

}