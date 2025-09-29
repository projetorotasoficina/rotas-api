-- Cria tabela incidente
CREATE TABLE incidente (
                           id BIGSERIAL PRIMARY KEY,
                           trajeto_id BIGINT NOT NULL,
                           nome VARCHAR(255) NOT NULL,
                           observacoes VARCHAR(400),
                           ts TIMESTAMP NOT NULL,
                           longitude DOUBLE PRECISION,
                           latitude DOUBLE PRECISION,
                           foto_url VARCHAR(255),

                           CONSTRAINT fk_incidente_trajeto
                               FOREIGN KEY (trajeto_id)
                                   REFERENCES trajeto (id)
                                   ON DELETE CASCADE,

    -- garante que nome não seja só espaços (equivalente mínimo ao @NotBlank)
                           CONSTRAINT chk_incidente_nome_not_blank
                               CHECK (char_length(trim(nome)) > 0),

    -- valida latitude/longitude no nível do banco (opcional, mas recomendado)
                           CONSTRAINT chk_incidente_latitude_range
                               CHECK (latitude IS NULL OR (latitude >= -90 AND latitude <= 90)),

                           CONSTRAINT chk_incidente_longitude_range
                               CHECK (longitude IS NULL OR (longitude >= -180 AND longitude <= 180))
);

-- Índices úteis
CREATE INDEX idx_incidente_trajeto_id ON incidente (trajeto_id);
CREATE INDEX idx_incidente_ts ON incidente (ts);
