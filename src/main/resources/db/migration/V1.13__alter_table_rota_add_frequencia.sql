-- Adiciona coluna periodo na tabela tb_rota
ALTER TABLE tb_rota ADD COLUMN periodo VARCHAR(10);

-- Cria tabela para armazenar os dias da semana de cada rota
CREATE TABLE tb_rota_dias_semana (
    rota_id BIGINT NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,
    CONSTRAINT fk_rota_dias_semana FOREIGN KEY (rota_id) REFERENCES tb_rota(id) ON DELETE CASCADE
);

-- Adiciona índice para melhorar performance nas consultas
CREATE INDEX idx_rota_dias_semana_rota_id ON tb_rota_dias_semana(rota_id);

