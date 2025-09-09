package utfpr.edu.br.coleta.tipocoleta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class TipoColetaRepositoryTest {

    @Autowired
    private TipoColetaRepository repository;

    @Test
    void deveSalvarEBuscarTipoColeta() {
        TipoColeta coleta = new TipoColeta();
        coleta.setNome("Reciclável");
        repository.save(coleta);

        List<TipoColeta> todos = repository.findAll();

        assertFalse(todos.isEmpty());
        assertEquals("Reciclável", todos.get(0).getNome());
    }
}
