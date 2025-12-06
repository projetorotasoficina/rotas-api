package utfpr.edu.br.coleta.tipocoleta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import utfpr.edu.br.coleta.caminhao.Caminhao;

import java.util.Optional;

/**
 * Repositório responsável pelo acesso a dados de Tipo coleta.
 *
 * Permite operações CRUD e consultas personalizadas.
 *
 * Autor: Pedro Henrique Sauthier
 */
public interface TipoColetaRepository extends JpaRepository<TipoColeta, Long> , JpaSpecificationExecutor<TipoColeta> {

    /**
     * Busca um tipo coleta pelo Id.
     *
     * @param id ID do tipo coleta
     * @return Optional contendo o tipo de coleta, se encontrado
     */
    Optional<TipoColeta> findById(Long id);

    /**
     * Verifica se existe um tipo de coleta com o ID informado.
     *
     * @param id id do tipo de coleta
     * @return true se já existir, false caso contrário
     */
    boolean existsById(Long cpf);

    /**
     * Busca tipos de coleta por nome (case-insensitive).
     *
     * @param nome termo de busca
     * @param pageable informações de paginação
     * @return página de tipos de coleta que correspondem à busca
     */
    Page<TipoColeta> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}