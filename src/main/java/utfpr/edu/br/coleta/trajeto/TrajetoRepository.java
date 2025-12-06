package utfpr.edu.br.coleta.trajeto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import utfpr.edu.br.coleta.caminhao.Caminhao;

public interface TrajetoRepository extends JpaRepository<Trajeto, Long> , JpaSpecificationExecutor<Trajeto> {

    /**
     * Busca todos os trajetos de uma rota específica, ordenados por data de início (mais recente primeiro).
     *
     * @param rotaId ID da rota
     * @return lista de trajetos da rota
     */
    java.util.List<Trajeto> findByRotaIdOrderByDataInicioDesc(Long rotaId);
}