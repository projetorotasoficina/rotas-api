package utfpr.edu.br.coleta.motorista;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import utfpr.edu.br.coleta.config.AbstractIntegrationTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de persistência para a entidade Motorista.
 * Utiliza Testcontainers com PostgreSQL + PostGIS para garantir paridade com produção.
 *
 * Autor: Luiz Alberto dos Passos
 */
class MotoristaRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private MotoristaRepository repository;

    /**
     * Deve salvar um motorista e recuperá-lo em seguida pelo ID.
     * Verifica se os dados persistidos correspondem aos informados.
     */
    @Test
    void deveSalvarEBuscarMotorista() {
        Motorista motorista = new Motorista();
        motorista.setNome("João da Silva");
        motorista.setCpf("12345678900");
        motorista.setAtivo(true); // campo obrigatório

        Motorista salvo = repository.save(motorista);
        Optional<Motorista> encontrado = repository.findById(salvo.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("João da Silva", encontrado.get().getNome());
        assertEquals("12345678900", encontrado.get().getCpf());
        assertTrue(encontrado.get().getAtivo());
    }
}
