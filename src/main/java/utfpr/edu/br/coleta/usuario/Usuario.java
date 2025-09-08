package utfpr.edu.br.coleta.usuario;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;
import utfpr.edu.br.coleta.usuario.role.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Entidade que representa um usuário do sistema (Administrador ou Cliente).
 *
 * Login sempre feito via OTP enviado por e-mail.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Entity
@Table(
        name = "tb_usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_cpf", columnNames = "cpf"),
                @UniqueConstraint(name = "uk_usuario_email", columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseEntity {

    @NotBlank(message = "O nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Column(nullable = false, unique = true)
    private String email;

    private String telefone;

    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(nullable = false)
    private Boolean ativo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "tb_usuario_roles",
            joinColumns = @JoinColumn(name = "usuario_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Set<Role> roles = new HashSet<>();
}