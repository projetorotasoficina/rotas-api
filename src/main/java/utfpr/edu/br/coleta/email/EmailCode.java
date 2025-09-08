package utfpr.edu.br.coleta.email;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;
import utfpr.edu.br.coleta.generics.BaseEntity;

import java.time.LocalDateTime;

/**
 * Entidade que representa um código temporário enviado por e-mail,
 * utilizado em processos de autenticação, cadastro e recuperação de senha.
 *
 * Autor: Luiz Alberto
 */
@Entity
@Table(name = "tb_access_code")
@Getter
@Setter
@NoArgsConstructor
public class EmailCode extends BaseEntity {

  /** Endereço de e-mail do destinatário ao qual o código está vinculado. */
  @Column(nullable = false)
  @Email(message = "E-mail deve ter formato válido.")
  @NotBlank(message = "O e-mail é obrigatório.")
  private String email;

  /** Código de validação gerado e enviado ao usuário. */
  @NotBlank(message = "O código é obrigatório.")
  @Column(nullable = false, unique = true)
  private String code;

  /** Data e hora em que o código foi gerado. */
  @NotNull(message = "A data de geração é obrigatória.")
  @Column(name = "generated_at", nullable = false)
  private LocalDateTime generatedAt;

  /** Indica se o código já foi utilizado. */
  @Column(nullable = false)
  private boolean used = false;

  /** Data e hora de expiração do código. */
  @NotNull(message = "A data de expiração é obrigatória.")
  @Column(nullable = false)
  private LocalDateTime expiration;

  /** Tipo do código de validação (autenticação, cadastro, recuperação, etc). */
  @NotNull(message = "O tipo do código é obrigatório.")
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoCodigo type;

  /**
   * Construtor para instanciar um novo código de validação de e-mail.
   *
   * @param email endereço de e-mail do destinatário
   * @param code código de validação gerado
   * @param generatedAt data e hora da geração do código
   * @param expiration data e hora de expiração do código
   * @param type tipo do código de validação
   */
  public EmailCode(
          String email,
          String code,
          LocalDateTime generatedAt,
          LocalDateTime expiration,
          TipoCodigo type) {
    this.email = email;
    this.code = code;
    this.generatedAt = generatedAt;
    this.expiration = expiration;
    this.type = type;
    this.used = false;
  }
}