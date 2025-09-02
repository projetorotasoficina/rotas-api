package utfpr.edu.br.coleta.motorista;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MotoristaRepositoryTest {

    @Autowired
    private MotoristaRepository repository;

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