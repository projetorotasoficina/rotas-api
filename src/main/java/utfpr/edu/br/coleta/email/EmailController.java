package utfpr.edu.br.coleta.email;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;
import utfpr.edu.br.coleta.email.impl.EmailServiceImpl;

import java.io.IOException;
import java.util.Map;

/**
 * Controlador responsável por expor endpoints REST para envio e validação
 * de códigos de verificação por e-mail.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Tag(name = "Email", description = "API para envio e validação de códigos por e-mail")
@RestController
@RequestMapping("/api/email")
@Validated
public class EmailController {

  private final EmailServiceImpl emailService;
  private final EmailCodeValidationService validationService;

  /**
   * Construtor que cria o controlador de e-mail com os serviços necessários.
   *
   * @param emailService serviço de envio de e-mails
   * @param validationService serviço de validação de códigos
   */
  public EmailController(
          EmailServiceImpl emailService, EmailCodeValidationService validationService) {
    this.emailService = emailService;
    this.validationService = validationService;
  }

  /**
   * Envia um código de verificação para o e-mail informado.
   *
   * @param email endereço de e-mail do destinatário
   * @param type tipo do código (valor do enum {@link TipoCodigo})
   * @return resposta com mensagem de sucesso, e-mail e tipo do código
   * @throws IllegalArgumentException se os parâmetros forem inválidos
   * @throws IOException se ocorrer falha no envio do e-mail
   */
  @Operation(summary = "Envia código de verificação por e-mail")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Código enviado com sucesso"),
          @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou limite excedido"),
          @ApiResponse(responseCode = "500", description = "Erro ao enviar e-mail")
  })
  @PostMapping("/enviar")
  public ResponseEntity<Map<String, String>> enviar(
          @RequestParam String email, @RequestParam String type) throws IOException {
    validarEmail(email);
    TipoCodigo tipoCodigo = converterParaTipoCodigo(type);
    validarTipo(tipoCodigo);

    emailService.generateAndSendCode(email, tipoCodigo);

    return ResponseEntity.ok(
            Map.of(
                    "mensagem", "Código enviado com sucesso", "email", email, "tipo", tipoCodigo.name()));
  }

  /**
   * Valida um código de verificação enviado ao e-mail informado.
   *
   * @param email endereço de e-mail
   * @param type tipo do código
   * @param code código recebido pelo usuário
   * @return true se válido, false caso contrário
   */
  @Operation(summary = "Valida o código de verificação enviado")
  @ApiResponse(responseCode = "200", description = "Validação realizada com sucesso")
  @PostMapping("/validar")
  public ResponseEntity<Boolean> validar(
          @RequestParam @NotBlank @Email String email,
          @RequestParam @NotBlank String type,
          @RequestParam @NotBlank String code) {
    TipoCodigo tipoCodigo = converterParaTipoCodigo(type);
    boolean valido = validationService.validateCode(email, tipoCodigo, code);
    return ResponseEntity.ok(valido);
  }

  /**
   * Trata exceções de parâmetros inválidos e retorna HTTP 400.
   *
   * @param e exceção lançada
   * @return resposta com mensagem de erro
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
          IllegalArgumentException e) {
    return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
  }

  /**
   * Trata falhas no envio de e-mails e retorna HTTP 500.
   *
   * @param e exceção lançada
   * @return resposta com mensagem de erro
   */
  @ExceptionHandler(IOException.class)
  public ResponseEntity<Map<String, String>> handleIOException(IOException e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("erro", "Falha ao enviar e-mail: " + e.getMessage()));
  }

  /**
   * Valida se o e-mail informado é válido.
   *
   * @param email e-mail a validar
   */
  private void validarEmail(String email) {
    if (email == null || email.isBlank() || !email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
      throw new IllegalArgumentException("Email inválido");
    }
  }

  /**
   * Valida se o tipo do código foi informado.
   *
   * @param type tipo a validar
   */
  private void validarTipo(TipoCodigo type) {
    if (type == null) {
      throw new IllegalArgumentException("Tipo de código não informado");
    }
  }

  /**
   * Converte uma string para {@link TipoCodigo}.
   *
   * @param type valor em string
   * @return enum correspondente
   */
  private TipoCodigo converterParaTipoCodigo(String type) {
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("Tipo de código não informado");
    }

    try {
      return TipoCodigo.valueOf(type.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Tipo de código inválido: " + type);
    }
  }
}