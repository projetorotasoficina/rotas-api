-- V1_19__update_geo_functions.sql

-- 1. DROP da função antiga (com 2 parâmetros)
DROP FUNCTION IF EXISTS calcular_areas_nao_percorridas(bigint, double precision);

-- 2. CRIAÇÃO da nova função (com 3 parâmetros)
CREATE OR REPLACE FUNCTION calcular_areas_nao_percorridas(
    rota_id_param BIGINT,
    buffer_metros_param DOUBLE PRECISION,
    trajeto_id_param BIGINT DEFAULT NULL -- NOVO PARÂMETRO OPCIONAL
)
RETURNS geometry AS $$
DECLARE
area_planejada geometry;
    geometria_trajetos_cobertos geometry;
    areas_nao_cobertas geometry;
BEGIN
    -- 1. Obter a área geográfica planejada da rota
SELECT area_geografica INTO area_planejada
FROM tb_rota
WHERE id = rota_id_param;

IF area_planejada IS NULL THEN
        RETURN NULL;
END IF;

    -- 2. Calcular a união dos buffers dos trajetos
    -- A CHAVE ESTÁ AQUI: FILTRAR PELO trajeto_id_param SE FOR FORNECIDO
SELECT ST_Union(ST_Buffer(t.geometria, buffer_metros_param))
INTO geometria_trajetos_cobertos
FROM tb_trajeto t
WHERE t.rota_id = rota_id_param
  AND (trajeto_id_param IS NULL OR t.id = trajeto_id_param) -- FILTRO DE TRAJETO
  -- Se trajeto_id_param for NULL, só considera trajetos FINALIZADOS (comportamento padrão)
  -- Se trajeto_id_param for NOT NULL, considera o trajeto, mesmo que EM_ANDAMENTO
  AND (trajeto_id_param IS NOT NULL OR t.status = 'FINALIZADO');

-- 3. Calcular a diferença (áreas não percorridas)
IF geometria_trajetos_cobertos IS NULL THEN
        areas_nao_cobertas := area_planejada;
ELSE
        areas_nao_cobertas := ST_Difference(area_planejada, geometria_trajetos_cobertos);
END IF;

RETURN areas_nao_cobertas;
END;
$$ LANGUAGE plpgsql;
-- 3. DROP da função antiga (com 2 parâmetros)
DROP FUNCTION IF EXISTS obter_estatisticas_cobertura(bigint, double precision);

-- 4. CRIAÇÃO da nova função (com 3 parâmetros)
CREATE OR REPLACE FUNCTION obter_estatisticas_cobertura(
    rota_id_param BIGINT,
    buffer_metros_param DOUBLE PRECISION,
    trajeto_id_param BIGINT DEFAULT NULL -- NOVO PARÂMETRO OPCIONAL
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
    -- 1. Obter a área geográfica planejada da rota
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

    area_total_m2 := ST_Area(area_planejada);

    -- 2. Calcular a união dos buffers dos trajetos
    -- A CHAVE ESTÁ AQUI: FILTRAR PELO trajeto_id_param SE FOR FORNECIDO
SELECT ST_Union(ST_Buffer(t.geometria, buffer_metros_param))
INTO geometria_trajetos_cobertos
FROM tb_trajeto t
WHERE t.rota_id = rota_id_param
  AND (trajeto_id_param IS NULL OR t.id = trajeto_id_param) -- FILTRO DE TRAJETO
  AND (trajeto_id_param IS NOT NULL OR t.status = 'FINALIZADO');

-- 3. Calcular a área coberta (interseção)
IF geometria_trajetos_cobertos IS NULL THEN
        area_coberta_m2 := 0;
ELSE
        area_coberta_m2 := ST_Area(ST_Intersection(area_planejada, geometria_trajetos_cobertos));
END IF;

    -- 4. Calcular as demais estatísticas
    area_nao_coberta_m2 := area_total_m2 - area_coberta_m2;
    percentual_cobertura := (area_coberta_m2 / area_total_m2) * 100;

    -- 5. Contar a quantidade de trajetos usados no cálculo
SELECT COUNT(t.id)
INTO quantidade_trajetos
FROM tb_trajeto t
WHERE t.rota_id = rota_id_param
  AND (trajeto_id_param IS NULL OR t.id = trajeto_id_param)
  AND (trajeto_id_param IS NOT NULL OR t.status = 'FINALIZADO');

-- 6. Retornar JSON
RETURN json_build_object(
        'area_total_m2', area_total_m2,
        'area_coberta_m2', area_coberta_m2,
        'area_nao_coberta_m2', area_nao_coberta_m2,
        'percentual_cobertura', percentual_cobertura,
        'quantidade_trajetos', quantidade_trajetos
       );
END;
$$ LANGUAGE plpgsql;