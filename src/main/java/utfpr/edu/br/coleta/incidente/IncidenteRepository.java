package utfpr.edu.br.coleta.incidente;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long>, JpaSpecificationExecutor<Incidente> {

    List<Incidente> findByTrajetoId(Long trajetoId);

    @EntityGraph(attributePaths = {"trajeto", "trajeto.rota"})
    List<Incidente> findByTsBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    @EntityGraph(attributePaths = {"trajeto", "trajeto.rota"})
    List<Incidente> findByTrajetoRotaId(Long rotaId);

    @EntityGraph(attributePaths = {"trajeto", "trajeto.rota"})
    List<Incidente> findByTsBetweenAndTrajetoRotaId(LocalDateTime dataInicio, LocalDateTime dataFim, Long rotaId);
}