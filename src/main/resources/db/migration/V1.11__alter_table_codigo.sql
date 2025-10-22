-- Migration V1.12: Ajustar tamanho do campo codigo para 6 caracteres
-- Reduz o tamanho do campo de 32 para 6 caracteres (apenas dígitos numéricos)

-- Ajustar o tamanho do campo codigo
ALTER TABLE tb_codigo_ativacao
ALTER COLUMN codigo TYPE VARCHAR(6);

-- Comentário explicativo
COMMENT ON COLUMN tb_codigo_ativacao.codigo IS 'Código de ativação de 6 dígitos numéricos';