package utfpr.edu.br.coleta.rota;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório responsável pelo acesso a dados de Rota.
 *
 * Permite operações CRUD e consultas personalizadas.
 *
 * Autor: Pedro Henrique Sauthier
 */
public interface RotaRepository extends JpaRepository<Rota, Long> {

    /**
     * Busca uma rota pelo Id.
     *
     * @param id ID do rota
     * @return Optional contendo a rota, se encontrada
     */
    Optional<Rota> findById(Long id);

    /**
     * Verifica se existe uma rota com o ID informado.
     *
     * @param id id da rota
     * @return true se já existir, false caso contrário
     */
    boolean existsById(Long cpf);
}