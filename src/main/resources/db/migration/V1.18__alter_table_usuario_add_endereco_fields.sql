-- Adiciona campos de endereço para usuários do tipo MORADOR
ALTER TABLE tb_usuario
    ADD COLUMN endereco VARCHAR(255),
    ADD COLUMN numero VARCHAR(20),
    ADD COLUMN bairro VARCHAR(100),
    ADD COLUMN cep VARCHAR(8),
    ADD COLUMN latitude DECIMAL(10, 8),
    ADD COLUMN longitude DECIMAL(11, 8);

-- Adiciona comentários para documentação
COMMENT ON COLUMN tb_usuario.endereco IS 'Endereço do morador (rua/avenida)';
COMMENT ON COLUMN tb_usuario.numero IS 'Número do endereço';
COMMENT ON COLUMN tb_usuario.bairro IS 'Bairro do morador';
COMMENT ON COLUMN tb_usuario.cep IS 'CEP do endereço (8 dígitos)';
COMMENT ON COLUMN tb_usuario.latitude IS 'Latitude da localização do morador';
COMMENT ON COLUMN tb_usuario.longitude IS 'Longitude da localização do morador';
