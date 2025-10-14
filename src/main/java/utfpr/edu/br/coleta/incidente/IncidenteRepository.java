package utfpr.edu.br.coleta.incidente;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> {
    List<Incidente> findByTrajetoId(Long trajetoId);
}
