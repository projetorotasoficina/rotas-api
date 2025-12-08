package utfpr.edu.br.coleta.motorista;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

/**
 * Repositório responsável pelo acesso a dados de Motorista.
 *
 * Permite operações CRUD e consultas personalizadas.
 *
 * Autor: Luiz Alberto dos Passos
 */
public interface MotoristaRepository extends JpaRepository<Motorista, Long>,
        JpaSpecificationExecutor<Motorista> {

    /**
     * Busca um motorista pelo CPF.
     *
     * @param cpf CPF do motorista
     * @return Optional contendo o motorista, se encontrado
     */
    Optional<Motorista> findByCpf(String cpf);

    /**
     * Verifica se existe um motorista com o CPF informado.
     *
     * @param cpf CPF do motorista
     * @return true se já existir, false caso contrário
     */
    boolean existsByCpf(String cpf);

    /**
     * Busca motoristas por nome (case-insensitive).
     *
     * @param nome termo de busca
     * @param pageable informações de paginação
     * @return página de motoristas que correspondem à busca
     */
    Page<Motorista> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /**
     * Retorna todos os motoristas ativos.
     *
     * @return lista de motoristas com flag ativo = true
     */
    List<Motorista> findByAtivoTrue();
}