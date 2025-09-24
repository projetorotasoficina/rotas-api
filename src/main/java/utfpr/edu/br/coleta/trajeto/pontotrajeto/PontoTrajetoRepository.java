package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PontoTrajetoRepository extends JpaRepository<PontoTrajeto, Long> {
    List<PontoTrajeto> findByTrajetoId(Long trajetoId);
}