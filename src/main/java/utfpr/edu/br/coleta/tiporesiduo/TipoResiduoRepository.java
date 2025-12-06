package utfpr.edu.br.coleta.tiporesiduo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import utfpr.edu.br.coleta.caminhao.Caminhao;

import java.util.Optional;

public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long>, JpaSpecificationExecutor<TipoResiduo> {

    Optional<TipoResiduo> findByNome(String nome);

    boolean existsByNome(String nome);

    @Override
    Optional<TipoResiduo> findById(Long id);

    /**
     * Busca tipos de resíduo por nome (case-insensitive).
     *
     * @param nome termo de busca
     * @param pageable informações de paginação
     * @return página de tipos de resíduo que correspondem à busca
     */
    Page<TipoResiduo> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
