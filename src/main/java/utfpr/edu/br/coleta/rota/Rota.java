package utfpr.edu.br.coleta.rota;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;
import utfpr.edu.br.coleta.tipocoleta.TipoColeta;
import utfpr.edu.br.coleta.tiporesiduo.TipoResiduo;

/**
 * Entidade que representa uma Rota no sistema de coleta.
 *
 * Contém informações pessoais e operacionais necessárias
 * para vinculação em trajetos.
 *
 * Autor: Pedro Henrique Sauthier
 */
@Entity
@Table(name = "tb_rota")
@Getter
@Setter
@NoArgsConstructor
public class Rota extends BaseEntity {

    /** Nome/Descrição do tipo de coleta. */
    @NotBlank(message = "Um nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(nullable = false)
    private Boolean ativo;

    @Size(max = 400, message = "Observações podem ter no máximo 400 caracteres.")
    @Column(length = 400)
    private String observacoes;

    @NotNull(message = "O tipo de resíduo é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "tipo_residuo_id", nullable = false)
    private TipoResiduo tipoResiduo;

    @NotNull(message = "O tipo de coleta é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "tipo_coleta_id", nullable = false)
    private TipoColeta tipoColeta;
}