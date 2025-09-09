package utfpr.edu.br.coleta.email.enums;

import lombok.Getter;

/**
 * Enum que representa os diferentes tipos de códigos utilizados no sistema
 * para operações de autenticação, cadastro e recuperação de senha.
 *
 * <p>
 * Cada constante possui um valor associado em {@link #tipo}, que descreve
 * o contexto de uso do código.
 * </p>
 *
 * Autor Luiz Alberto dos Passos
 */
@Getter
public enum TipoCodigo {

  /**
   * Código OTP gerado para autenticação de login.
   */
  OTP_AUTENTICACAO("Autenticação"),

  /**
   * Código OTP gerado para confirmação de cadastro.
   */
  OTP_CADASTRO("Cadastro"),

  /**
   * Código OTP gerado para recuperação de senha.
   */
  OTP_RECUPERACAO("Recuperação de Senha");

  /**
   * Descrição textual do tipo de código.
   */
  private final String tipo;

  /**
   * Construtor que associa uma descrição textual ao tipo de código.
   *
   * @param tipo descrição do código (ex: "Autenticação", "Cadastro")
   */
  TipoCodigo(String tipo) {
    this.tipo = tipo;
  }

  /**
   * Retorna a descrição textual do tipo de código.
   *
   * @return string representando o tipo do código
   */
  public String getTipo() {
    return tipo;
  }
}