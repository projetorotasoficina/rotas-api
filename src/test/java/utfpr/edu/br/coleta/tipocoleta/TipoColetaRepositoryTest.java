package utfpr.edu.br.coleta.tipocoleta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import utfpr.edu.br.coleta.config.AbstractIntegrationTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de persistência para a entidade TipoColeta.
 * Utiliza Testcontainers com PostgreSQL + PostGIS para garantir paridade com produção.
 */
class TipoColetaRepositoryTest extends AbstractIntegrationTest {

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