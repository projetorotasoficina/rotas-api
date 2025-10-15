package utfpr.edu.br.coleta.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do UsuarioController.
 * Usa @SpringBootTest para carregar o contexto completo e suportar herança de CrudController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "SUPER_ADMIN")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private IUsuarioService usuarioService;

    @Test
    void deveCriarUsuario() throws Exception {
        Usuario entrada = new Usuario();
        entrada.setNome("Teste");
        entrada.setCpf("12345678901");
        entrada.setEmail("teste@email.com");
        entrada.setAtivo(true);

        Usuario salvo = new Usuario();
        salvo.setId(1L);
        salvo.setNome("Teste");
        salvo.setCpf("12345678901");
        salvo.setEmail("teste@email.com");
        salvo.setAtivo(true);

        when(usuarioService.save(any(Usuario.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/usuarios")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Teste")));
    }

    @Test
    void deveListarUsuarios() throws Exception {
        Usuario u = new Usuario();
        u.setId(10L);
        u.setNome("Maria");
        u.setCpf("11122233344");
        u.setEmail("maria@email.com");
        u.setAtivo(true);

        when(usuarioService.findAll()).thenReturn(List.of(u));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Maria")));
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        mockMvc.perform(delete("/api/usuarios/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
