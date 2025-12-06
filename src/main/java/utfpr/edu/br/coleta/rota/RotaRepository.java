package utfpr.edu.br.coleta.rota;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    boolean existsById(Long id);

    /**
     * Busca todas as rotas ativas.
     *
     * @return lista de rotas ativas
     */
    java.util.List<Rota> findByAtivoTrue();

    /**
     * Busca rotas por nome (case-insensitive).
     *
     * @param nome termo de busca
     * @param pageable informações de paginação
     * @return página de rotas que correspondem à busca
     */
    Page<Rota> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
    /**
     * Calcula as áreas não percorridas de uma rota específica.
     *
     * Utiliza função PostGIS para comparar a área geográfica da rota
     * com os trajetos realizados, aplicando um buffer configurável.
     *
     * @param rotaId ID da rota a ser analisada
     * @param bufferMetros Raio do buffer em metros (padrão: 20m)
     * @return GeoJSON com as áreas não cobertas
     */
    @Query(value = """
    SELECT ST_AsGeoJSON(
        calcular_areas_nao_percorridas(:rotaId, :bufferMetros, :trajetoId)
    )::text as areas_nao_cobertas
    """, nativeQuery = true)
    Optional<String> calcularAreasNaoPercorridas(
            @Param("rotaId") Long rotaId,
            @Param("bufferMetros") Double bufferMetros,
            @Param("trajetoId") Long trajetoId
    );

    /**
     * Obtém estatísticas de cobertura de uma rota.
     *
     * @param rotaId ID da rota a ser analisada
     * @param bufferMetros Raio do buffer em metros
     * @return JSON com estatísticas de cobertura
     */
    @Query(value = """
    SELECT obter_estatisticas_cobertura(:rotaId, :bufferMetros, :trajetoId)::text as estatisticas
    """, nativeQuery = true)
    Optional<String> obterEstatisticasCobertura(
            @Param("rotaId") Long rotaId,
            @Param("bufferMetros") Double bufferMetros,
            @Param("trajetoId") Long trajetoId
    );

    /**
     * Verifica se a rota possui área geográfica definida.
     *
     * @param rotaId ID da rota
     * @return true se possui área geográfica, false caso contrário
     */
    @Query(value = """
    SELECT CASE 
        WHEN area_geografica IS NOT NULL THEN true 
        ELSE false 
    END
    FROM tb_rota 
    WHERE id = :rotaId
    """, nativeQuery = true)
    Optional<Boolean> possuiAreaGeografica(@Param("rotaId") Long rotaId);
}