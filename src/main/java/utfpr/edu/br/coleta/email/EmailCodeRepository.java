package utfpr.edu.br.coleta.email;

import org.springframework.data.jpa.repository.JpaRepository;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repositório responsável pelas operações de persistência e consulta
 * da entidade {@link EmailCode}.
 *
 * Autor: Luiz Alberto dos Passos
 */
public interface EmailCodeRepository extends JpaRepository<EmailCode, Long> {

  /**
   * Busca o código mais recente para o e-mail e tipo informados.
   *
   * @param email endereço de e-mail
   * @param type tipo do código
   * @return código mais recente, se existir
   */
  Optional<EmailCode> findTopByEmailAndTypeOrderByGeneratedAtDesc(String email, TipoCodigo type);

  /**
   * Retorna um código válido que ainda não expirou e não foi utilizado.
   *
   * @param code valor do código
   * @param now data e hora limite para expiração
   * @return código válido, se encontrado
   */
  Optional<EmailCode> findByCodeAndExpirationAfterAndUsedFalse(String code, LocalDateTime now);

  /**
   * Conta quantos códigos foram gerados após a data especificada.
   *
   * @param email endereço de e-mail
   * @param type tipo do código
   * @param generatedAt data e hora mínima
   * @return quantidade de códigos encontrados
   */
  Long countByEmailAndTypeAndGeneratedAtAfter(
          String email, TipoCodigo type, LocalDateTime generatedAt);
}