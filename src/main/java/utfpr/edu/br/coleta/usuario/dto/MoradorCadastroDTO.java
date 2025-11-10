package utfpr.edu.br.coleta.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO para cadastro de novo morador no sistema.
 * 
 * Autor: Sistema Rotas API
 */
@Getter
@Setter
@Schema(description = "Dados para cadastro de um novo morador")
public class MoradorCadastroDTO {

    @NotBlank(message = "O nome é obrigatório.")
    @Schema(description = "Nome completo do morador", example = "João Silva")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.")
    @Schema(description = "E-mail do morador", example = "joao.silva@email.com")
    private String email;

    @NotBlank(message = "O CPF é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "O CPF deve conter 11 dígitos numéricos.")
    @Schema(description = "CPF do morador (somente números)", example = "12345678901")
    private String cpf;

    @Schema(description = "Telefone do morador", example = "46999887766")
    private String telefone;

    @NotBlank(message = "O endereço é obrigatório.")
    @Schema(description = "Endereço (rua/avenida)", example = "Rua das Flores")
    private String endereco;

    @NotBlank(message = "O número é obrigatório.")
    @Schema(description = "Número do endereço", example = "123")
    private String numero;

    @NotBlank(message = "O bairro é obrigatório.")
    @Schema(description = "Bairro", example = "Centro")
    private String bairro;

    @NotBlank(message = "O CEP é obrigatório.")
    @Pattern(regexp = "\\d{8}", message = "O CEP deve conter 8 dígitos numéricos.")
    @Schema(description = "CEP (somente números)", example = "85503000")
    private String cep;

    @NotNull(message = "A latitude é obrigatória.")
    @Schema(description = "Latitude da localização", example = "-26.2289")
    private Double latitude;

    @NotNull(message = "A longitude é obrigatória.")
    @Schema(description = "Longitude da localização", example = "-52.6789")
    private Double longitude;
}
