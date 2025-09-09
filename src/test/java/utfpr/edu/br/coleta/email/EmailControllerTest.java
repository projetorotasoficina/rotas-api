package utfpr.edu.br.coleta.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;
import utfpr.edu.br.coleta.email.impl.EmailServiceImpl;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayName("EmailController")
class EmailControllerTest {

  @Mock private EmailServiceImpl emailService;
  @Mock private EmailCodeValidationService validationService;
  @InjectMocks private EmailController controller;

  private static final String EMAIL_OK = "teste@utfpr.edu.br";
  private static final String CODIGO_OK = "ABC123";

  @Nested
  @DisplayName("Enviar código")
  class EnviarCodigo {

    @Test
    @DisplayName("deve retornar 200 e mensagem de sucesso")
    void enviar_Sucesso() throws IOException {
      when(emailService.generateAndSendCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO)).thenReturn(null);

      ResponseEntity<?> response = controller.enviar(EMAIL_OK, "OTP_CADASTRO");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().toString().contains("Código enviado com sucesso"));
      verify(emailService).generateAndSendCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO);
      verifyNoInteractions(validationService);
    }

    @Test
    @DisplayName("deve aceitar tipo case-insensitive (otp_cadastro)")
    void enviar_TipoCaseInsensitive() throws IOException {
      when(emailService.generateAndSendCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO)).thenReturn(null);

      ResponseEntity<?> response = controller.enviar(EMAIL_OK, "otp_cadastro");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      verify(emailService).generateAndSendCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO);
    }

    @Test
    @DisplayName("deve mapear IllegalArgumentException para 400")
    void enviar_IllegalArgumentException() throws IOException {
      when(emailService.generateAndSendCode(eq(EMAIL_OK), any(TipoCodigo.class)))
              .thenThrow(new IllegalArgumentException("Limite atingido"));

      IllegalArgumentException ex =
              assertThrows(IllegalArgumentException.class, () -> controller.enviar(EMAIL_OK, "OTP_CADASTRO"));

      ResponseEntity<?> response = controller.handleIllegalArgumentException(ex);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().toString().contains("Limite atingido"));
    }

    @Test
    @DisplayName("deve mapear IOException para 500")
    void enviar_IOException() throws IOException {
      when(emailService.generateAndSendCode(eq(EMAIL_OK), any(TipoCodigo.class)))
              .thenThrow(new IOException("Erro na API SendGrid"));

      IOException ex = assertThrows(IOException.class, () -> controller.enviar(EMAIL_OK, "OTP_CADASTRO"));

      ResponseEntity<?> response = controller.handleIOException(ex);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().toString().contains("Falha ao enviar e-mail"));
    }

    @ParameterizedTest(name = "[{index}] tipo inválido: \"{0}\" -> BAD_REQUEST")
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n", "TIPO_INEXISTENTE"})
    void enviar_TipoInvalidoOuVazio(String tipo) {
      IllegalArgumentException ex =
              assertThrows(IllegalArgumentException.class, () -> controller.enviar(EMAIL_OK, tipo));

      ResponseEntity<?> response = controller.handleIllegalArgumentException(ex);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().toString().matches(".*(Tipo de código inválido|Tipo de código não informado).*"));
      verifyNoInteractions(emailService, validationService);
    }

    @ParameterizedTest(name = "[{index}] email inválido: \"{0}\" -> BAD_REQUEST")
    @NullAndEmptySource
    @ValueSource(strings = {"email-invalido", "sem-arroba.com", "a@b", " a@b.com "})
    void enviar_EmailInvalido(String email) {
      IllegalArgumentException ex =
              assertThrows(IllegalArgumentException.class, () -> controller.enviar(email, "OTP_CADASTRO"));

      ResponseEntity<?> response = controller.handleIllegalArgumentException(ex);
      assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
      assertNotNull(response.getBody());
      assertTrue(response.getBody().toString().contains("Email inválido"));
      verifyNoInteractions(emailService, validationService);
    }
  }

  @Nested
  @DisplayName("Validar código")
  class ValidarCodigo {

    @Test
    @DisplayName("deve retornar true quando o código confere")
    void validar_Sucesso() {
      when(validationService.validateCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO, CODIGO_OK)).thenReturn(true);

      ResponseEntity<Boolean> response = controller.validar(EMAIL_OK, "OTP_CADASTRO", CODIGO_OK);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertTrue(Boolean.TRUE.equals(response.getBody()));
      verify(validationService).validateCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO, CODIGO_OK);
      verifyNoInteractions(emailService);
    }

    @Test
    @DisplayName("deve retornar false quando o código não confere")
    void validar_CodigoInvalido() {
      when(validationService.validateCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO, "XYZ999")).thenReturn(false);

      ResponseEntity<Boolean> response = controller.validar(EMAIL_OK, "OTP_CADASTRO", "XYZ999");

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertFalse(Boolean.TRUE.equals(response.getBody()));
      verify(validationService).validateCode(EMAIL_OK, TipoCodigo.OTP_CADASTRO, "XYZ999");
    }

    @ParameterizedTest(name = "[{index}] email formato inválido em validar: \"{0}\" -> OK/false")
    @ValueSource(strings = {"email-invalido", "x@", "@x.com"})
    void validar_EmailFormatoInvalido_RetornaFalse(String email) {
      when(validationService.validateCode(eq(email), eq(TipoCodigo.OTP_CADASTRO), eq(CODIGO_OK)))
              .thenReturn(false);

      ResponseEntity<Boolean> response = controller.validar(email, "OTP_CADASTRO", CODIGO_OK);

      assertEquals(HttpStatus.OK, response.getStatusCode());
      assertFalse(Boolean.TRUE.equals(response.getBody()));
      verify(validationService).validateCode(email, TipoCodigo.OTP_CADASTRO, CODIGO_OK);
      verifyNoInteractions(emailService);
    }
  }
}