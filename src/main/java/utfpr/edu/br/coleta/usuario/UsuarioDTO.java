package utfpr.edu.br.coleta.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import utfpr.edu.br.coleta.usuario.role.Role;


import java.util.Set;

/**
 * DTO para transferência de dados de Usuário.
 */
@Getter
@Setter
public class UsuarioDTO {

    private Long id;

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos.")
    private String cpf;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    private String email;

    private String telefone;

    @NotNull(message = "O campo ativo é obrigatório.")
    private Boolean ativo;

    private Set<Role> roles;

    // Campos específicos para ROLE_MORADOR
    private String endereco;

    private String numero;

    private String bairro;

    @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 dígitos numéricos.")
    private String cep;

    private Double latitude;

    private Double longitude;
}