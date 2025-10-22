-- Criar nova tabela de frequência que associa dia da semana e período
CREATE TABLE tb_frequencia_rota (
    id BIGSERIAL PRIMARY KEY,
    rota_id BIGINT NOT NULL,
    dia_semana VARCHAR(10) NOT NULL,
    periodo VARCHAR(10) NOT NULL,
    CONSTRAINT fk_frequencia_rota FOREIGN KEY (rota_id) REFERENCES tb_rota(id) ON DELETE CASCADE,
    CONSTRAINT uk_rota_dia_semana UNIQUE (rota_id, dia_semana)
);

-- Adiciona índice para melhorar performance nas consultas
CREATE INDEX idx_frequencia_rota_rota_id ON tb_frequencia_rota(rota_id);

-- Migrar dados existentes da estrutura antiga para a nova
-- Se uma rota tinha diasSemana e periodo, criar uma frequência para cada dia com o mesmo período
INSERT INTO tb_frequencia_rota (rota_id, dia_semana, periodo)
SELECT r.id, d.dia_semana, r.periodo
FROM tb_rota r
INNER JOIN tb_rota_dias_semana d ON r.id = d.rota_id
WHERE r.periodo IS NOT NULL;

-- Remover tabela antiga de dias da semana
DROP TABLE IF EXISTS tb_rota_dias_semana;

-- Remover coluna periodo da tabela rota (agora está na tb_frequencia_rota)
ALTER TABLE tb_rota DROP COLUMN IF EXISTS periodo;

-- Adiciona comentário explicativo
COMMENT ON TABLE tb_frequencia_rota IS 'Frequência de rotas: associa cada dia da semana a um período específico (manhã, tarde ou noite)';

