CREATE TABLE ponto_trajeto (
                               id BIGSERIAL PRIMARY KEY,
                               trajeto_id BIGINT NOT NULL,
                               localizacao geometry(Point, 4326) NOT NULL,
                               horario TIMESTAMP NOT NULL,
                               observacao TEXT,

                               CONSTRAINT fk_ponto_trajeto_trajeto
                                   FOREIGN KEY (trajeto_id)
                                       REFERENCES trajeto (id)
                                       ON DELETE CASCADE
);
