package utfpr.edu.br.coleta.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para UsuarioService.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void deveSalvarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setNome("Carlos");
        usuario.setCpf("11122233344");
        usuario.setEmail("carlos@email.com");
        usuario.setAtivo(true);

        Usuario salvo = new Usuario();
        salvo.setId(1L);
        salvo.setNome("Carlos");
        salvo.setCpf("11122233344");
        salvo.setEmail("carlos@email.com");
        salvo.setAtivo(true);

        when(repository.save(any(Usuario.class))).thenReturn(salvo);

        Usuario resultado = service.save(usuario);

        assertNotNull(resultado.getId());
        assertEquals("Carlos", resultado.getNome());
        assertEquals("11122233344", resultado.getCpf());
        assertTrue(resultado.getAtivo());
    }

    @Test
    void deveBuscarUsuarioPorId() {
        Usuario usuario = new Usuario();
        usuario.setId(2L);
        usuario.setNome("Ana");
        usuario.setCpf("55566677788");
        usuario.setEmail("ana@email.com");
        usuario.setAtivo(true);

        when(repository.findById(2L)).thenReturn(Optional.of(usuario));

        Usuario resultado = service.findOne(2L);

        assertEquals("Ana", resultado.getNome());
        assertEquals("55566677788", resultado.getCpf());
        assertTrue(resultado.getAtivo());
    }

    @Test
    void deveListarUsuarios() {
        Usuario u1 = new Usuario();
        u1.setId(1L);
        u1.setNome("João");
        u1.setCpf("11111111111");
        u1.setEmail("joao@email.com");
        u1.setAtivo(true);

        Usuario u2 = new Usuario();
        u2.setId(2L);
        u2.setNome("Maria");
        u2.setCpf("22222222222");
        u2.setEmail("maria@email.com");
        u2.setAtivo(true);

        when(repository.findAll()).thenReturn(List.of(u1, u2));

        List<Usuario> usuarios = service.findAll();

        assertEquals(2, usuarios.size());
        assertEquals("João", usuarios.get(0).getNome());
    }

    @Test
    void deveDeletarUsuario() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}