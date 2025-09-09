package utfpr.edu.br.coleta.auth.otp;

import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
public class EmailOtpAuthenticationToken extends AbstractAuthenticationToken {
  private final transient Object principal;
  private transient Object credentials;

  /**
   * Cria um token de autenticação OTP por e-mail não autenticado com o principal e as credenciais
   * informados.
   *
   * @param principal identidade do usuário, geralmente o e-mail
   * @param credentials código OTP ou senha temporária utilizada para autenticação
   */
  public EmailOtpAuthenticationToken(Object principal, Object credentials) {
    super(null);
    this.principal = principal;
    this.credentials = credentials;
    setAuthenticated(false);
  }

  /**
   * Inicializa um token de autenticação autenticado para autenticação via OTP por e-mail.
   *
   * @param principal identidade do usuário, como e-mail ou nome de usuário
   * @param credentials código OTP ou senha temporária fornecida pelo usuário
   * @param authorities coleções de permissões concedidas ao usuário autenticado
   */
  public EmailOtpAuthenticationToken(
      Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.credentials = credentials;
    super.setAuthenticated(true);
  }

  /**
   * Retorna as credenciais associadas a este token de autenticação, como o código OTP ou senha
   * temporária.
   *
   * @return as credenciais fornecidas para autenticação
   */
  @Override
  public Object getCredentials() {
    return this.credentials;
  }

  /**
   * Retorna a identidade do usuário associada a este token de autenticação.
   *
   * @return o principal, normalmente o e-mail do usuário
   */
  @Override
  public Object getPrincipal() {
    return this.principal;
  }

  /**
   * Remove as credenciais sensíveis deste token, definindo-as como nulas.
   *
   * <p>Garante que informações confidenciais, como senhas ou OTPs, sejam eliminadas da memória após
   * a autenticação.
   */
  @Override
  public void eraseCredentials() {
    super.eraseCredentials();
    this.credentials = null;
  }
}
