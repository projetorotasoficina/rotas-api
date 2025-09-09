package utfpr.edu.br.coleta.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável por operações de persistência da entidade Usuário.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

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
}