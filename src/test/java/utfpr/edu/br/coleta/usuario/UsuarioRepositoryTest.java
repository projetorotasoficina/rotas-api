package utfpr.edu.br.coleta.usuario;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para UsuarioRepository garantindo persistência e consultas básicas.
 */
@DataJpaTest
class UsuarioRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void deveSalvarEBuscarUsuarioPorCpf() {
        Usuario usuario = new Usuario();
        usuario.setNome("Luiz");
        usuario.setCpf("12345678901");
        usuario.setEmail("luiz@email.com");
        usuario.setAtivo(true);

        Usuario salvo = repository.save(usuario);
        Optional<Usuario> encontrado = repository.findByCpf("12345678901");

        assertTrue(encontrado.isPresent());
        assertEquals("Luiz", encontrado.get().getNome());
        assertEquals("luiz@email.com", encontrado.get().getEmail());
        assertEquals(salvo.getId(), encontrado.get().getId());
    }

    @Test
    void deveSalvarEBuscarUsuarioPorEmail() {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setCpf("98765432100");
        usuario.setEmail("maria@email.com");
        usuario.setAtivo(true);

        repository.save(usuario);

        Optional<Usuario> encontrado = repository.findByEmail("maria@email.com");
        assertTrue(encontrado.isPresent());
        assertEquals("Maria", encontrado.get().getNome());
    }
}