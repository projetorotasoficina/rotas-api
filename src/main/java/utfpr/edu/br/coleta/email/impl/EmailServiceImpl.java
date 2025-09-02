package utfpr.edu.br.coleta.email.impl;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import utfpr.edu.br.coleta.email.EmailCode;
import utfpr.edu.br.coleta.email.EmailCodeRepository;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Serviço responsável por gerar códigos OTP, enviar e-mails utilizando SendGrid
 * e registrar as informações no banco de dados.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Service
public class EmailServiceImpl {

  /** Tempo de expiração de um código OTP em minutos. */
  private static final int CODE_EXPIRATION_MINUTES = 10;

  /** Limite máximo de códigos permitidos por e-mail em 24 horas. */
  private static final int MAX_CODES_PER_DAY = 30;

  /** Limite máximo de códigos permitidos em curto período. */
  private static final int MAX_CODES_IN_SHORT_PERIOD = 5;

  /** Intervalo de tempo em minutos para o limite de curto período. */
  private static final int SHORT_PERIOD_REST_IN_MINUTES = 15;

  /** Expressão regular para validação de formato de e-mail. */
  private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");

  /** Mensagem de erro quando o limite diário de códigos é atingido. */
  public static final String ERRO_LIMITE_DIARIO =
          "Quantidade de solicitações ultrapassa o limite das últimas 24 horas.";

  /** Mensagem de erro quando o limite de curto período é atingido. */
  public static final String ERRO_LIMITE_CURTO =
          "Limite de solicitações atingido, tente novamente em %d minutos.";

  /** Logger para registro de eventos e erros no serviço. */
  private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

  /** Repositório responsável pela persistência de códigos de e-mail. */
  private final EmailCodeRepository repository;

  /** Cliente SendGrid utilizado para envio de e-mails. */
  private final SendGrid sendGrid;

  /** Motor de templates Thymeleaf para geração de mensagens HTML. */
  private final SpringTemplateEngine springTemplateEngine;

  /**
   * Construtor do serviço de e-mail.
   *
   * @param repository repositório para persistência de códigos de e-mail
   * @param sendGrid cliente SendGrid configurado
   * @param springTemplateEngine motor de templates Thymeleaf
   */
  public EmailServiceImpl(
          EmailCodeRepository repository,
          SendGrid sendGrid,
          SpringTemplateEngine springTemplateEngine) {
    this.repository = repository;
    this.sendGrid = sendGrid;
    this.springTemplateEngine = springTemplateEngine;
  }

  /**
   * Gera e envia um código de verificação para o e-mail informado, registrando o
   * código no banco de dados.
   *
   * @param email endereço de e-mail do destinatário
   * @param type tipo do código de verificação
   * @return resposta da API do SendGrid referente ao envio do e-mail
   * @throws IllegalArgumentException se tipo for nulo, e-mail inválido ou limites de envio excedidos
   * @throws IOException se ocorrer falha no envio do e-mail
   */
  public Response generateAndSendCode(String email, TipoCodigo type) throws IOException {
    if (type == null) {
      throw new IllegalArgumentException("O tipo do código é obrigatório.");
    }
    validarEmail(email);
    verificarLimiteEnvio(email, type);

    String code = gerarCodigoAleatorio();
    Response response = enviarEmailDeVerificacao(email, code, type);
    salvarCodigo(email, code, type);
    return response;
  }

  /** Valida se o endereço de e-mail fornecido está no formato correto. */
  private void validarEmail(String email) {
    if (email == null || !EMAIL_REGEX.matcher(email).matches()) {
      throw new IllegalArgumentException("Endereço de e-mail inválido.");
    }
  }

  /**
   * Verifica se o envio de códigos para o e-mail excede os limites diário e de curto prazo.
   *
   * @param email endereço de e-mail a ser verificado
   * @param type tipo de código relacionado
   * @throws IllegalArgumentException se algum limite de envio for atingido
   */
  private void verificarLimiteEnvio(String email, TipoCodigo type) {
    LocalDateTime limiteDiario = LocalDateTime.now().minusHours(24);
    Long codigos = repository.countByEmailAndTypeAndGeneratedAtAfter(email, type, limiteDiario);
    if (codigos > MAX_CODES_PER_DAY) {
      throw new IllegalArgumentException(ERRO_LIMITE_DIARIO);
    }

    LocalDateTime limiteCurto = LocalDateTime.now().minusMinutes(SHORT_PERIOD_REST_IN_MINUTES);
    codigos = repository.countByEmailAndTypeAndGeneratedAtAfter(email, type, limiteCurto);

    if (codigos > MAX_CODES_IN_SHORT_PERIOD) {
      throw new IllegalArgumentException(ERRO_LIMITE_CURTO.formatted(SHORT_PERIOD_REST_IN_MINUTES));
    }
  }

  /**
   * Gera um código aleatório de 4 caracteres alfanuméricos em maiúsculas.
   *
   * @return código OTP aleatório
   */
  private String gerarCodigoAleatorio() {
    return UUID.randomUUID().toString().substring(0, 4).toUpperCase();
  }

  /**
   * Salva um código de verificação de e-mail no banco de dados com informações de
   * validade e status.
   *
   * @param email endereço de e-mail vinculado ao código
   * @param code código gerado
   * @param type tipo de código
   */
  private void salvarCodigo(String email, String code, TipoCodigo type) {
    EmailCode ec = new EmailCode();
    ec.setEmail(email);
    ec.setCode(code);
    ec.setType(type);
    ec.setGeneratedAt(LocalDateTime.now());
    ec.setExpiration(LocalDateTime.now().plusMinutes(CODE_EXPIRATION_MINUTES));
    ec.setUsed(false);
    repository.save(ec);
  }

  /**
   * Envia um e-mail de verificação utilizando template HTML e inclui código e tempo de expiração.
   *
   * @param email destinatário
   * @param code código de verificação
   * @param type tipo de verificação
   * @return resposta da API SendGrid
   * @throws IOException se ocorrer falha ao processar o template ou enviar e-mail
   */
  private Response enviarEmailDeVerificacao(String email, String code, TipoCodigo type)
          throws IOException {
    String assunto = "Código de Verificação - " + type.getTipo();

    Context context = new Context();
    context.setVariable("otpCode", code);
    context.setVariable("expirationMinutes", CODE_EXPIRATION_MINUTES);
    try {
      String mensagemHtml = springTemplateEngine.process("otp-template", context);
      return sendEmail(email, assunto, mensagemHtml, "text/html");
    } catch (Exception e) {
      throw new IOException("Erro ao processar o template do e-mail: " + e.getMessage(), e);
    }
  }

  /**
   * Envia um e-mail via SendGrid.
   *
   * @param to destinatário
   * @param subject assunto do e-mail
   * @param contentText conteúdo (texto ou HTML)
   * @param tipo tipo do conteúdo ("text/plain" ou "text/html")
   * @return resposta da API SendGrid
   * @throws IOException se envio falhar ou status != 202
   */
  public Response sendEmail(String to, String subject, String contentText, String tipo)
          throws IOException {
    Email from = new Email("webprojeto2@gmail.com");
    Email toEmail = new Email(to);
    Content content = new Content(tipo, contentText);
    Mail mail = new Mail(from, subject, toEmail, content);

    Request request = new Request();
    request.setMethod(Method.POST);
    request.setEndpoint("mail/send");
    request.setBody(mail.build());

    Response response = sendGrid.api(request);

    if (response == null || response.getStatusCode() != 202) {
      logger.error(
              "Erro ao enviar e-mail por sendgrid, status code: {}",
              response != null ? response.getStatusCode() : 0);
      throw new IOException("Erro ao tentar enviar e-mail, tente novamente mais tarde");
    }

    return response;
  }
}