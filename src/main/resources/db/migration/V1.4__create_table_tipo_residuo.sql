CREATE TABLE IF NOT EXISTS tb_tipo_residuo (
                                               id BIGSERIAL PRIMARY KEY,
                                               nome VARCHAR(255) NOT NULL,
    cor_hex VARCHAR(7) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
    );

CREATE UNIQUE INDEX IF NOT EXISTS uq_tb_tipo_residuo_nome ON tb_tipo_residuo (nome);
