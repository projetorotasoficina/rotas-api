package utfpr.edu.br.coleta.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import utfpr.edu.br.coleta.caminhao.Caminhao;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável por operações de persistência da entidade Usuário.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByCpf(String cpf);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findAllByEmailEndingWith(String dominioEmail);

    /**
     * Busca usuários cujo e-mail contém a substring informada.
     *
     * @param emailParcial parte do e-mail a ser pesquisada
     * @return lista de Optional contendo os usuários encontrados
     */
    List<Usuario> findByEmailContaining(String emailParcial);

    /**
     * Busca usuários cujo nome contém a substring informada (case insensitive).
     *
     * @param nomeParcial parte do nome a ser pesquisada
     * @return lista de Optional contendo os usuários encontrados
     */
    List<Usuario> findByNomeContainingIgnoreCase(String nomeParcial);

    /**
     * Busca usuários por nome ou email (case-insensitive).
     *
     * @param nome termo de busca para nome
     * @param email termo de busca para email
     * @param pageable informações de paginação
     * @return página de usuários que correspondem à busca
     */
    Page<Usuario> findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(String nome, String email, Pageable pageable);
}