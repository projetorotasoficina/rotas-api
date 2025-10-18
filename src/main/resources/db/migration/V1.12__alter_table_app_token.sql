-- Adiciona campos faltantes e corrige inconsistências
-- NOTA: A V1.11 já ajustou o tamanho do campo 'codigo' para 6 caracteres

-- ============================================================================
-- PARTE 1: Correções na tabela tb_codigo_ativacao
-- ============================================================================

-- 1. Adicionar campo 'usado' (CRÍTICO - campo não existe)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_codigo_ativacao'
          AND column_name = 'usado'
    ) THEN
ALTER TABLE tb_codigo_ativacao
    ADD COLUMN usado BOOLEAN NOT NULL DEFAULT FALSE;

RAISE NOTICE '[tb_codigo_ativacao] Campo usado adicionado com sucesso';
ELSE
        RAISE NOTICE '[tb_codigo_ativacao] Campo usado já existe';
END IF;
END $$;

-- 2. Renomear campo usado_por_device_id para device_id (CRÍTICO - nome errado)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_codigo_ativacao'
          AND column_name = 'usado_por_device_id'
    ) THEN
ALTER TABLE tb_codigo_ativacao
    RENAME COLUMN usado_por_device_id TO device_id;

RAISE NOTICE '[tb_codigo_ativacao] Campo usado_por_device_id renomeado para device_id';
ELSE
        RAISE NOTICE '[tb_codigo_ativacao] Campo device_id já existe ou usado_por_device_id não existe';
END IF;
END $$;

-- 3. Ajustar tamanho do campo device_id para 255 caracteres
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_codigo_ativacao'
          AND column_name = 'device_id'
    ) THEN
ALTER TABLE tb_codigo_ativacao
ALTER COLUMN device_id TYPE VARCHAR(255);

        RAISE NOTICE '[tb_codigo_ativacao] Tamanho do campo device_id ajustado para 255';
END IF;
END $$;

-- 4. Adicionar comentários explicativos
COMMENT ON COLUMN tb_codigo_ativacao.usado IS 'Indica se o código já foi utilizado';
COMMENT ON COLUMN tb_codigo_ativacao.device_id IS 'ID do dispositivo que usou este código';

-- ============================================================================
-- PARTE 2: Correções na tabela tb_app_token
-- ============================================================================

-- 1. Adicionar campo 'data_criacao' (CRÍTICO - campo não existe)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_app_token'
          AND column_name = 'data_criacao'
    ) THEN
ALTER TABLE tb_app_token
    ADD COLUMN data_criacao TIMESTAMP NOT NULL DEFAULT NOW();

RAISE NOTICE '[tb_app_token] Campo data_criacao adicionado com sucesso';
ELSE
        RAISE NOTICE '[tb_app_token] Campo data_criacao já existe';
END IF;
END $$;

-- 2. Adicionar campo 'ultimo_acesso' (CRÍTICO - campo não existe)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_app_token'
          AND column_name = 'ultimo_acesso'
    ) THEN
ALTER TABLE tb_app_token
    ADD COLUMN ultimo_acesso TIMESTAMP NULL;

RAISE NOTICE '[tb_app_token] Campo ultimo_acesso adicionado com sucesso';
ELSE
        RAISE NOTICE '[tb_app_token] Campo ultimo_acesso já existe';
END IF;
END $$;

-- 3. Adicionar campo 'total_acessos' (CRÍTICO - campo não existe)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'tb_app_token'
          AND column_name = 'total_acessos'
    ) THEN
ALTER TABLE tb_app_token
    ADD COLUMN total_acessos BIGINT NOT NULL DEFAULT 0;

RAISE NOTICE '[tb_app_token] Campo total_acessos adicionado com sucesso';
ELSE
        RAISE NOTICE '[tb_app_token] Campo total_acessos já existe';
END IF;
END $$;

-- 4. Ajustar tamanho do campo token para 255 caracteres
ALTER TABLE tb_app_token
ALTER COLUMN token TYPE VARCHAR(255);

-- 5. Ajustar tamanho do campo device_id para 255 caracteres
ALTER TABLE tb_app_token
ALTER COLUMN device_id TYPE VARCHAR(255);

-- 6. Adicionar constraint UNIQUE no campo device_id (se não existir)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'tb_app_token_device_id_unique'
    ) THEN
ALTER TABLE tb_app_token
    ADD CONSTRAINT tb_app_token_device_id_unique UNIQUE (device_id);

RAISE NOTICE '[tb_app_token] Constraint UNIQUE adicionada ao campo device_id';
ELSE
        RAISE NOTICE '[tb_app_token] Constraint UNIQUE já existe no campo device_id';
END IF;
END $$;

-- 7. Adicionar comentários explicativos
COMMENT ON COLUMN tb_app_token.data_criacao IS 'Data e hora de criação do token';
COMMENT ON COLUMN tb_app_token.ultimo_acesso IS 'Data e hora do último acesso usando este token';
COMMENT ON COLUMN tb_app_token.total_acessos IS 'Contador de acessos realizados com este token';
COMMENT ON COLUMN tb_app_token.device_id IS 'ID único do dispositivo Android (deve ser único)';

-- ============================================================================
-- FIM DA MIGRATION
-- ============================================================================
