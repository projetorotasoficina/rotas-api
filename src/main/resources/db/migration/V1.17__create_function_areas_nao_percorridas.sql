-- =====================================================================
-- Migration V1.17: Funções PostGIS para Análise de Áreas Não Percorridas
-- =====================================================================
-- Descrição:
--   Cria funções SQL para calcular áreas da rota que não foram cobertas
--   pelos trajetos realizados, comparando area_geografica (Polygon) com
--   caminho dos trajetos (LineString).
--
-- Autor: Luiz Alberto dos Passos
-- Data: 2025-11-02
-- =====================================================================

-- =====================================================================
-- Função: calcular_areas_nao_percorridas
-- =====================================================================
CREATE OR REPLACE FUNCTION calcular_areas_nao_percorridas(
    p_rota_id BIGINT,
    p_buffer_metros DOUBLE PRECISION DEFAULT 20.0
)
RETURNS geometry
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
v_area_geografica geometry;
    v_trajetos_agregados geometry;
    v_area_coberta geometry;
    v_areas_nao_cobertas geometry;
    v_count_trajetos INTEGER;
BEGIN
    -- 1. Buscar a área geográfica planejada da rota
SELECT area_geografica
INTO v_area_geografica
FROM tb_rota
WHERE id = p_rota_id;

-- Validar se a rota existe
IF NOT FOUND THEN
        RAISE EXCEPTION 'Rota com ID % não encontrada', p_rota_id;
END IF;
    
    -- Validar se a área geográfica está definida
    IF v_area_geografica IS NULL THEN
        RAISE EXCEPTION 'Rota ID % não possui área geográfica definida', p_rota_id;
END IF;
    
    -- 2. Agregar todos os trajetos concluídos desta rota
SELECT
    COUNT(*),
    ST_Union(caminho)
INTO
    v_count_trajetos,
    v_trajetos_agregados
FROM trajeto
WHERE rota_id = p_rota_id
  AND caminho IS NOT NULL
  AND status IN ('CONCLUIDO', 'FINALIZADO');

-- Se não houver trajetos, toda a área geográfica está não coberta
IF v_count_trajetos = 0 OR v_trajetos_agregados IS NULL THEN
        RETURN v_area_geografica;
END IF;
    
    -- 3. Criar buffer ao redor dos trajetos (convertendo para geografia para buffer em metros)
    v_area_coberta := ST_Transform(
        ST_Buffer(
            ST_Transform(v_trajetos_agregados, 3857), -- Converter para Web Mercator
            p_buffer_metros
        ),
        4326 -- Converter de volta para WGS84
    );
    
    -- 4. Calcular diferença: área geográfica - área coberta
    v_areas_nao_cobertas := ST_Difference(
        v_area_geografica,
        v_area_coberta
    );
    
    -- 5. Validar e limpar geometria resultante
    v_areas_nao_cobertas := ST_MakeValid(v_areas_nao_cobertas);
    
    -- Extrair apenas polígonos (remover pontos e linhas)
    IF ST_GeometryType(v_areas_nao_cobertas) IN ('ST_GeometryCollection', 'ST_MultiPolygon', 'ST_Polygon') THEN
        v_areas_nao_cobertas := ST_CollectionExtract(v_areas_nao_cobertas, 3);
END IF;
    
    -- 6. Filtrar áreas muito pequenas (ruído) - áreas menores que 100m²
    IF v_areas_nao_cobertas IS NOT NULL THEN
        IF ST_GeometryType(v_areas_nao_cobertas) = 'ST_MultiPolygon' THEN
            v_areas_nao_cobertas := (
                SELECT ST_Union(geom)
                FROM (
                    SELECT (ST_Dump(v_areas_nao_cobertas)).geom
                ) AS parts
                WHERE ST_Area(ST_Transform(geom, 3857)) > 100 -- Área > 100m²
            );
        ELSIF ST_GeometryType(v_areas_nao_cobertas) = 'ST_Polygon' THEN
            IF ST_Area(ST_Transform(v_areas_nao_cobertas, 3857)) <= 100 THEN
                RETURN NULL; -- Área muito pequena, considerar como totalmente coberta
END IF;
END IF;
END IF;

RETURN v_areas_nao_cobertas;

EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Erro ao calcular áreas não percorridas: %', SQLERRM;
END;
$$;

COMMENT ON FUNCTION calcular_areas_nao_percorridas(BIGINT, DOUBLE PRECISION) IS 
'Calcula as áreas da rota planejada que não foram cobertas pelos trajetos realizados, aplicando buffer configurável';


-- =====================================================================
-- Função: obter_estatisticas_cobertura
-- =====================================================================
CREATE OR REPLACE FUNCTION obter_estatisticas_cobertura(
    p_rota_id BIGINT,
    p_buffer_metros DOUBLE PRECISION DEFAULT 20.0
)
RETURNS JSON
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
v_area_geografica geometry;
    v_areas_nao_cobertas geometry;
    v_area_total_m2 DOUBLE PRECISION;
    v_area_nao_coberta_m2 DOUBLE PRECISION;
    v_area_coberta_m2 DOUBLE PRECISION;
    v_percentual_cobertura DOUBLE PRECISION;
    v_count_trajetos INTEGER;
    v_resultado JSON;
BEGIN
    -- Buscar área geográfica
SELECT area_geografica
INTO v_area_geografica
FROM tb_rota
WHERE id = p_rota_id;

IF v_area_geografica IS NULL THEN
        RAISE EXCEPTION 'Rota ID % não possui área geográfica definida', p_rota_id;
END IF;
    
    -- Calcular área total planejada em m²
    v_area_total_m2 := ST_Area(ST_Transform(v_area_geografica, 3857));
    
    -- Contar trajetos
SELECT COUNT(*)
INTO v_count_trajetos
FROM trajeto
WHERE rota_id = p_rota_id
  AND caminho IS NOT NULL
  AND status IN ('CONCLUIDO', 'FINALIZADO');

-- Calcular áreas não cobertas
v_areas_nao_cobertas := calcular_areas_nao_percorridas(p_rota_id, p_buffer_metros);
    
    -- Calcular área não coberta em m²
    IF v_areas_nao_cobertas IS NULL THEN
        v_area_nao_coberta_m2 := 0;
ELSE
        v_area_nao_coberta_m2 := ST_Area(ST_Transform(v_areas_nao_cobertas, 3857));
END IF;
    
    -- Calcular área coberta e percentual
    v_area_coberta_m2 := v_area_total_m2 - v_area_nao_coberta_m2;
    v_percentual_cobertura := (v_area_coberta_m2 / NULLIF(v_area_total_m2, 0)) * 100;
    
    -- Montar JSON de resultado
    v_resultado := json_build_object(
        'rota_id', p_rota_id,
        'area_total_m2', ROUND(v_area_total_m2::numeric, 2),
        'area_coberta_m2', ROUND(v_area_coberta_m2::numeric, 2),
        'area_nao_coberta_m2', ROUND(v_area_nao_coberta_m2::numeric, 2),
        'percentual_cobertura', ROUND(v_percentual_cobertura::numeric, 2),
        'quantidade_trajetos', v_count_trajetos,
        'buffer_metros', p_buffer_metros
    );

RETURN v_resultado;

EXCEPTION
    WHEN OTHERS THEN
        RAISE EXCEPTION 'Erro ao obter estatísticas de cobertura: %', SQLERRM;
END;
$$;

COMMENT ON FUNCTION obter_estatisticas_cobertura(BIGINT, DOUBLE PRECISION) IS 
'Retorna estatísticas detalhadas sobre a cobertura da rota em formato JSON';
