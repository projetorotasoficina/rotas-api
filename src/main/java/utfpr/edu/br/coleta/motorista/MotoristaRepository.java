package utfpr.edu.br.coleta.motorista;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório responsável pelo acesso a dados de Motorista.
 *
 * Permite operações CRUD e consultas personalizadas.
 *
 * Autor: Luiz Alberto dos Passos
 */
public interface MotoristaRepository extends JpaRepository<Motorista, Long> {

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
}