-- Adicionar coluna tipo_veiculo na tabela tb_caminhao
ALTER TABLE tb_caminhao 
ADD COLUMN tipo_veiculo VARCHAR(20);

-- Adicionar comentário explicativo
COMMENT ON COLUMN tb_caminhao.tipo_veiculo IS 'Tipo de veículo: VUC, CAMINHAO_LEVE, CAMINHAO_MEDIO, CAMINHAO_PESADO, CAMINHAO_CARRETA';

-- Atualizar registros existentes com valor padrão
UPDATE tb_caminhao 
SET tipo_veiculo = 'CAMINHAO_MEDIO' 
WHERE tipo_veiculo IS NULL;

-- Tornar a coluna obrigatória após popular os dados
ALTER TABLE tb_caminhao 
ALTER COLUMN tipo_veiculo SET NOT NULL;

-- Adicionar índice para melhorar performance em consultas por tipo
CREATE INDEX idx_caminhao_tipo_veiculo ON tb_caminhao(tipo_veiculo);

-- Comentário explicativo na tabela
COMMENT ON TABLE tb_caminhao IS 'Caminhões utilizados na coleta de resíduos, com tipo de veículo que determina a CNH mínima exigida';

