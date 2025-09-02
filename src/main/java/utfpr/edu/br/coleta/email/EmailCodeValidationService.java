package utfpr.edu.br.coleta.email;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.email.enums.TipoCodigo;

import java.time.LocalDateTime;

/**
 * Serviço responsável por validar códigos enviados por e-mail.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Service
public class EmailCodeValidationService {

    /** Repositório para acesso e manipulação de códigos de e-mail. */
    private final EmailCodeRepository repository;

    /**
     * Construtor que cria uma instância do serviço de validação.
     *
     * @param repository repositório de códigos de e-mail
     */
    public EmailCodeValidationService(EmailCodeRepository repository) {
        this.repository = repository;
    }

    /**
     * Valida se o código informado é o mais recente para o e-mail e tipo especificados.
     *
     * Verifica:
     * - Se o código corresponde ao último gerado.
     * - Se ainda não foi utilizado.
     * - Se está dentro do prazo de validade.
     *
     * Caso válido, o código é marcado como utilizado e salvo.
     *
     * @param email e-mail associado ao código
     * @param type tipo do código (ex: cadastro, recuperação)
     * @param code valor do código informado
     * @return true se válido e marcado como usado, false caso contrário
     */
    @Transactional
    public boolean validateCode(String email, TipoCodigo type, String code) {
        return repository
                .findTopByEmailAndTypeOrderByGeneratedAtDesc(email, type)
                .filter(ec -> ec.getCode().equals(code))
                .filter(ec -> !ec.isUsed())
                .filter(ec -> ec.getExpiration().isAfter(LocalDateTime.now()))
                .map(
                        ec -> {
                            ec.setUsed(true);
                            repository.save(ec);
                            return true;
                        })
                .orElse(false);
    }
}