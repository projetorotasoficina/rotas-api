package utfpr.edu.br.coleta.email;

import com.sendgrid.SendGrid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração responsável por expor o cliente {@link SendGrid}
 * como um bean gerenciado pelo Spring.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Configuration
public class EmailConfig {

  /**
   * Chave de API utilizada para autenticação no serviço SendGrid.
   * O valor é obtido das propriedades da aplicação.
   */
  @Value("${spring.sendgrid.api-key:sendgridkey}")
  private String apiKey;

  /**
   * Cria um bean do cliente SendGrid configurado com a chave de API definida.
   *
   * @return instância configurada de {@link SendGrid}
   */
  @Bean
  public SendGrid sendGrid() {
    return new SendGrid(apiKey);
  }
}