package utfpr.edu.br.coleta.auth;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import utfpr.edu.br.coleta.auth.dto.EmailOtpAuthRequestDTO;
import utfpr.edu.br.coleta.auth.otp.EmailOtpAuthenticationProvider;
import utfpr.edu.br.coleta.auth.otp.EmailOtpAuthenticationToken;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;
import utfpr.edu.br.coleta.email.impl.EmailServiceImpl;
import utfpr.edu.br.coleta.usuario.Usuario;
import utfpr.edu.br.coleta.usuario.UsuarioRepository;


@Service
@RequiredArgsConstructor
public class AuthService {
  private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
  public static final String EMAIL_NAO_CADASTRADO = "Email não cadastrado";
  private final UsuarioRepository usuarioRepository;
  private final EmailServiceImpl emailService;
  private final EmailOtpAuthenticationProvider emailOtpAuthenticationProvider;



  /**
   * Envia um código OTP para o email informado, caso o usuário esteja cadastrado.
   *
   * @param email endereço de email do usuário que receberá o código OTP
   * @throws ResponseStatusException com status 404 se o email não estiver cadastrado, ou 500 em
   *     caso de falha no envio do código
   */
  @Operation(summary = "Solicita um código OTP para autenticação via email")
  public void solicitarCodigoOtp(String email) {
    logger.info("Solicitação de código para validação de email");
    try {
      // Verificar se o usuário existe
      usuarioRepository
          .findByEmail(email)
          .orElseThrow(() -> new EntityNotFoundException(EMAIL_NAO_CADASTRADO));
      emailService.generateAndSendCode(email, TipoCodigo.OTP_AUTENTICACAO);
      logger.info("Código de verificação enviado");
    } catch (EntityNotFoundException e) {
      logger.error(EMAIL_NAO_CADASTRADO, e);
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, EMAIL_NAO_CADASTRADO);
    } catch (Exception e) {
      logger.error("Erro ao enviar código de verificação", e);
      throw new ResponseStatusException(
          HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar código de verificação");
    }
  }

  /**
   * Autentica um usuário utilizando um código OTP enviado por email.
   *
   * <p>Autentica o usuário utilizando o email e o código OTP fornecidos, definindo o contexto de
   * segurança ao autenticar com sucesso.
   *
   * @param dto Objeto com o email do usuário e o código OTP recebido.
   * @return O usuário autenticado correspondente ao email informado.
   * @throws ResponseStatusException Se o código OTP for inválido ou expirado (HTTP 422).
   * @throws UsernameNotFoundException Se o email informado não estiver cadastrado.
   */
  @Operation(summary = "Autentica um usuário usando o código OTP")
  public Usuario autenticacaoOtp(EmailOtpAuthRequestDTO dto) {
    try {
      EmailOtpAuthenticationToken authToken =
          new EmailOtpAuthenticationToken(dto.getEmail(), dto.getCode());
      Authentication authentication = emailOtpAuthenticationProvider.authenticate(authToken);
      SecurityContextHolder.getContext().setAuthentication(authentication);
      return usuarioRepository
          .findByEmail(dto.getEmail())
          .orElseThrow(() -> new UsernameNotFoundException(EMAIL_NAO_CADASTRADO));
    } catch (BadCredentialsException ex) {
      throw new ResponseStatusException(
          HttpStatus.UNPROCESSABLE_ENTITY, "Código inválido ou expirado");
    }
  }
}
