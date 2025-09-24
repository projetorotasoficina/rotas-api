CREATE TABLE trajeto (
                         id SERIAL PRIMARY KEY,
                         rota_id INT NOT NULL REFERENCES tb_rota(id),
                         caminhao_id INT NOT NULL REFERENCES tb_caminhao(id),
                         motorista_id INT NOT NULL REFERENCES tb_motorista(id),
                         data_inicio TIMESTAMP NOT NULL DEFAULT NOW(),
                         data_fim TIMESTAMP,
                         distancia_total DOUBLE PRECISION,
                         status VARCHAR(20) NOT NULL DEFAULT 'EM_ANDAMENTO',
                         caminho geometry(LineString, 4326)
);