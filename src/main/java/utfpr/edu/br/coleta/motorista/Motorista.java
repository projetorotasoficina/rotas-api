package utfpr.edu.br.coleta.motorista;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;

import java.time.LocalDate;

/**
 * Entidade que representa um Motorista no sistema de coleta.
 *
 * Contém informações pessoais e operacionais necessárias
 * para vinculação em trajetos e caminhões.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Entity
@Table(name = "tb_motorista")
@Getter
@Setter
@NoArgsConstructor
public class Motorista extends BaseEntity {

    /** Nome completo do motorista. */
    @NotBlank(message = "O nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    /** CPF do motorista, único e válido (11 dígitos). */
    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos numéricos.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /** Categoria da CNH do motorista (A, B, C, D ou E). Opcional. */
    @Pattern(regexp = "A|B|C|D|E", message = "Categoria da CNH inválida.")
    @Column(name = "cnh_categoria", length = 2)
    private String cnhCategoria;

    /** Data de validade da CNH. Deve ser futura. Opcional. */
    @Future(message = "A validade da CNH deve ser uma data futura.")
    @Column(name = "cnh_validade")
    private LocalDate cnhValidade;

    /** Define se o motorista está ativo no sistema. */
    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(nullable = false)
    private Boolean ativo;
}