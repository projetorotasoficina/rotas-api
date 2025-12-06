package utfpr.edu.br.coleta.incidente;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import utfpr.edu.br.coleta.caminhao.Caminhao;

import java.util.List;

public interface IncidenteRepository extends JpaRepository<Incidente, Long> , JpaSpecificationExecutor<Incidente> {
    List<Incidente> findByTrajetoId(Long trajetoId);
}
