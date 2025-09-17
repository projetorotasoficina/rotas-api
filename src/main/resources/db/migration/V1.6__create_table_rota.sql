CREATE TABLE tb_rota (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL,
    observacoes VARCHAR(400),
    residuo_id BIGINT NOT NULL,
    tipo_coleta_id BIGINT NOT NULL,
    CONSTRAINT fk_residuo FOREIGN KEY (residuo_id) REFERENCES tb_tipo_residuo(id),
    CONSTRAINT fk_tipo_coleta FOREIGN KEY (tipo_coleta_id) REFERENCES tb_tipo_coleta(id)
);