package utfpr.edu.br.coleta.aplicativoandroid.codigoativacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para operações de banco de dados com CodigoAtivacao.
 * 
 * @author Luiz Alberto dos Passos
 */
@Repository
public interface CodigoAtivacaoRepository extends JpaRepository<CodigoAtivacao, Long> {

    /**
     * Busca um código de ativação pelo código e que esteja ativo.
     * 
     * @param codigo o código de ativação
     * @return Optional contendo o código se encontrado e ativo
     */
    Optional<CodigoAtivacao> findByCodigoAndAtivoTrue(String codigo);

    /**
     * Verifica se existe um código de ativação com o código informado.
     *
     * @param codigo o código de ativação
     * @return true se existe, false caso contrário
     */
    boolean existsByCodigo(String codigo);

    /**
     * Busca códigos de ativação que contenham o texto informado.
     *
     * @param codigo termo de busca
     * @param pageable informações de paginação
     * @return página de códigos que correspondem à busca
     */
    Page<CodigoAtivacao> findByCodigoContaining(String codigo, Pageable pageable);
}

