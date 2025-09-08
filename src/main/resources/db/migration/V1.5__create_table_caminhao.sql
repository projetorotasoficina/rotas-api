CREATE TABLE tb_caminhao (
    id BIGSERIAL PRIMARY KEY,
    modelo VARCHAR(255) NOT NULL,
    placa VARCHAR(8) NOT NULL UNIQUE,
    tipo_coleta_id BIGINT NOT NULL,
    residuo_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL,
    CONSTRAINT fk_tipo_coleta FOREIGN KEY (tipo_coleta_id) REFERENCES tb_tipo_coleta(id),
    CONSTRAINT fk_residuo FOREIGN KEY (residuo_id) REFERENCES tb_tipo_residuo(id)
);