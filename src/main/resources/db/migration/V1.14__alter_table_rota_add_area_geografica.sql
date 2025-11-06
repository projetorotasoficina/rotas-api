-- Adiciona coluna area_geografica na tabela tb_rota
-- Utiliza PostGIS para armazenar polígonos com SRID 4326 (WGS 84)
ALTER TABLE tb_rota ADD COLUMN area_geografica geometry(Polygon, 4326);

-- Adiciona índice espacial para otimizar consultas geográficas
CREATE INDEX idx_rota_area_geografica ON tb_rota USING GIST (area_geografica);

-- Adiciona comentário explicativo na coluna
COMMENT ON COLUMN tb_rota.area_geografica IS 'Área geográfica (polígono) que representa a região de cobertura da rota. SRID 4326 (WGS 84).';

