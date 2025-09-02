package utfpr.edu.br.coleta.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;

import java.time.LocalDateTime;

/**
 * DTO para transferência de dados da entidade {@link EmailCode}.
 * Representa as informações necessárias para criação, validação e uso de códigos de e-mail (OTP).
 *
 * Autor: Luiz Alberto dos Passos
 */
public class EmailCodeDto {

  /** Identificador único do código de e-mail. */
  private Long id;

  /** Endereço de e-mail associado ao código. */
  @NotBlank(message = "O e-mail é obrigatório.")
  @Email(message = "E-mail inválido.")
  private String email;

  /** Código gerado para validação. */
  @NotBlank(message = "O código é obrigatório.")
  private String code;

  /** Tipo do código (cadastro, autenticação, recuperação, etc.). */
  @NotNull(message = "O tipo do código é obrigatório.")
  private TipoCodigo type;

  /** Indica se o código já foi utilizado. */
  @NotNull(message = "O status de uso é obrigatório.")
  private Boolean used;

  /** Data e hora em que o código foi gerado. */
  @NotNull(message = "A data de geração é obrigatória.")
  private LocalDateTime generatedAt;

  /** Data e hora em que o código expira. */
  @NotNull(message = "A data de expiração é obrigatória.")
  private LocalDateTime expiration;

  /** Retorna o identificador único do código de e-mail. */
  public Long getId() {
    return id;
  }

  /** Define o identificador do código de e-mail. */
  public void setId(Long id) {
    this.id = id;
  }

  /** Retorna o endereço de e-mail associado ao código. */
  public String getEmail() {
    return email;
  }

  /** Define o endereço de e-mail associado ao código. */
  public void setEmail(String email) {
    this.email = email;
  }

  /** Retorna o código associado ao e-mail. */
  public String getCode() {
    return code;
  }

  /** Define o valor do código associado ao e-mail. */
  public void setCode(String code) {
    this.code = code;
  }

  /** Obtém o tipo ou categoria do código de e-mail. */
  public TipoCodigo getType() {
    return type;
  }

  /** Define o tipo ou categoria associado ao código de e-mail. */
  public void setType(TipoCodigo type) {
    this.type = type;
  }

  /** Retorna se o código já foi utilizado. */
  public Boolean getUsed() {
    return used;
  }

  /** Define se o código foi utilizado. */
  public void setUsed(Boolean used) {
    this.used = used;
  }

  /** Retorna a data e hora em que o código foi gerado. */
  public LocalDateTime getGeneratedAt() {
    return generatedAt;
  }

  /** Define a data e hora em que o código foi gerado. */
  public void setGeneratedAt(LocalDateTime generatedAt) {
    this.generatedAt = generatedAt;
  }

  /** Retorna a data e hora de expiração do código de e-mail. */
  public LocalDateTime getExpiration() {
    return expiration;
  }

  /** Define a data e hora de expiração do código. */
  public void setExpiration(LocalDateTime expiration) {
    this.expiration = expiration;
  }
}