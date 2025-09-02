CREATE TABLE tb_usuario (
                            id BIGSERIAL PRIMARY KEY,
                            nome VARCHAR(255) NOT NULL,
                            cpf VARCHAR(11) NOT NULL UNIQUE,
                            email VARCHAR(255) NOT NULL UNIQUE,
                            telefone VARCHAR(20),
                            ativo BOOLEAN NOT NULL,
                            created_at TIMESTAMP DEFAULT NOW(),
                            updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE tb_usuario_roles (
                                  usuario_id BIGINT NOT NULL,
                                  role VARCHAR(50) NOT NULL,
                                  CONSTRAINT fk_usuario_roles FOREIGN KEY (usuario_id) REFERENCES tb_usuario (id) ON DELETE CASCADE
);