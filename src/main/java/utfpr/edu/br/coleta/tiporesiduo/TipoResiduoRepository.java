package utfpr.edu.br.coleta.tiporesiduo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long> {

    Optional<TipoResiduo> findByNome(String nome);

    boolean existsByNome(String nome);

    @Override
    Optional<TipoResiduo> findById(Long id);
}
