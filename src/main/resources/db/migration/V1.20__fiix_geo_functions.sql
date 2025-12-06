-- V1_20__fix_geo_functions.sql

-- 1. Limpar funções antigas para evitar conflitos (incluindo as de 3 parâmetros que podem ter falhado)
DROP FUNCTION IF EXISTS calcular_areas_nao_percorridas(bigint, double precision);
DROP FUNCTION IF EXISTS calcular_areas_nao_percorridas(bigint, double precision, bigint);

-- 2. Função Corrigida: calcular_areas_nao_percorridas
CREATE OR REPLACE FUNCTION calcular_areas_nao_percorridas(
    rota_id_param BIGINT,
    buffer_metros_param DOUBLE PRECISION,
    trajeto_id_param BIGINT DEFAULT NULL
)
RETURNS geometry AS $$
DECLARE
area_planejada geometry;
    geometria_trajetos_cobertos geometry;
    areas_nao_cobertas geometry;
BEGIN
    -- Obter a área geográfica planejada da rota
SELECT area_geografica INTO area_planejada
FROM tb_rota
WHERE id = rota_id_param;

IF area_planejada IS NULL THEN
        RETURN NULL;
END IF;

    -- Calcular a união dos buffers dos trajetos
    -- CORREÇÃO 1: Usando nomes corretos: tabela 'trajeto' e coluna 'caminho'
    -- CORREÇÃO 2: Usando ST_Transform para calcular buffer em metros (3857)
SELECT ST_Union(
               ST_Transform(
                       ST_Buffer(
                               ST_Transform(t.caminho, 3857),
                               buffer_metros_param
                       ),
                       4326
               )
       )
INTO geometria_trajetos_cobertos
FROM trajeto t
WHERE t.rota_id = rota_id_param
  AND (trajeto_id_param IS NULL OR t.id = trajeto_id_param) -- FILTRO DE TRAJETO
  AND (trajeto_id_param IS NOT NULL OR t.status = 'FINALIZADO'); -- LÓGICA DE STATUS

-- Calcular a diferença
IF geometria_trajetos_cobertos IS NULL THEN
        areas_nao_cobertas := area_planejada;
ELSE
        areas_nao_cobertas := ST_Difference(area_planejada, geometria_trajetos_cobertos);
END IF;

RETURN areas_nao_cobertas;
END;
$$ LANGUAGE plpgsql;

-- 3. Limpar funções antigas para evitar conflitos (incluindo as de 3 parâmetros que podem ter falhado)
DROP FUNCTION IF EXISTS obter_estatisticas_cobertura(bigint, double precision);
DROP FUNCTION IF EXISTS obter_estatisticas_cobertura(bigint, double precision, bigint);

-- 4. Função Corrigida: obter_estatisticas_cobertura
CREATE OR REPLACE FUNCTION obter_estatisticas_cobertura(
    rota_id_param BIGINT,
    buffer_metros_param DOUBLE PRECISION,
    trajeto_id_param BIGINT DEFAULT NULL
)
RETURNS json AS $$
DECLARE
area_planejada geometry;
    geometria_trajetos_cobertos geometry;
    area_total_m2 DOUBLE PRECISION;
    area_coberta_m2 DOUBLE PRECISION;
    area_nao_coberta_m2 DOUBLE PRECISION;
    percentual_cobertura DOUBLE PRECISION;
    quantidade_trajetos INTEGER;
BEGIN
SELECT area_geografica INTO area_planejada
FROM tb_rota
WHERE id = rota_id_param;

IF area_planejada IS NULL THEN
        RETURN json_build_object(
            'area_total_m2', 0,
            'area_coberta_m2', 0,
            'area_nao_coberta_m2', 0,
            'percentual_cobertura', 0,
            'quantidade_trajetos', 0
        );
END IF;

    -- CORREÇÃO: ST_Area deve ser calculado após a transformação para 3857 (metros)
    area_total_m2 := ST_Area(ST_Transform(area_planejada, 3857));

    -- CORREÇÃO: Usando nomes corretos: tabela 'trajeto', coluna 'caminho', ST_Transform
SELECT ST_Union(
               ST_Transform(
                       ST_Buffer(
                               ST_Transform(t.caminho, 3857),
                               buffer_metros_param
                       ),
                       4326
               )
       )
INTO geometria_trajetos_cobertos
FROM trajeto t
WHERE t.rota_id = rota_id_param
  AND (trajeto_id_param IS NULL OR t.id = trajeto_id_param) -- FILTRO DE TRAJETO
  AND (trajeto_id_param IS NOT NULL OR t.status = 'FINALIZADO'); -- LÓGICA DE STATUS

IF geometria_trajetos_cobertos IS NULL THEN
        area_coberta_m2 := 0;
ELSE
        -- CORREÇÃO: ST_Area da interseção deve ser calculado após a transformação para 3857 (metros)
        area_coberta_m2 := ST_Area(ST_Intersection(
            ST_Transform(area_planejada, 3857),
            ST_Transform(geometria_trajetos_cobertos, 3857)
        ));
END IF;

    area_nao_coberta_m2 := area_total_m2 - area_coberta_m2;
    percentual_cobertura := (area_coberta_m2 / area_total_m2) * 100;

    -- Contar a quantidade de trajetos usados no cálculo
SELECT COUNT(t.id) INTO quantidade_trajetos
FROM trajeto t
WHERE t.rota_id = rota_id_param
  AND (trajeto_id_param IS NULL OR t.id = trajeto_id_param)
  AND (trajeto_id_param IS NOT NULL OR t.status = 'FINALIZADO');

RETURN json_build_object(
        'area_total_m2', area_total_m2,
        'area_coberta_m2', area_coberta_m2,
        'area_nao_coberta_m2', area_nao_coberta_m2,
        'percentual_cobertura', percentual_cobertura,
        'quantidade_trajetos', quantidade_trajetos
       );
END;
$$ LANGUAGE plpgsql;
