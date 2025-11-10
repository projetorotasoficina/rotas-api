package utfpr.edu.br.coleta.trajeto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TrajetoRepository extends JpaRepository<Trajeto, Long> {

    /**
     * Busca todos os trajetos de uma rota específica, ordenados por data de início (mais recente primeiro).
     *
     * @param rotaId ID da rota
     * @return lista de trajetos da rota
     */
    java.util.List<Trajeto> findByRotaIdOrderByDataInicioDesc(Long rotaId);
}