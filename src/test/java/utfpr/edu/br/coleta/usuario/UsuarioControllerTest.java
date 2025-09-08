package utfpr.edu.br.coleta.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para UsuarioController usando MockMvc.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // Desabilita filtros de segurança no MockMvc

class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void deveCriarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Teste");
        usuario.setCpf("12345678901");
        usuario.setEmail("teste@email.com");
        usuario.setAtivo(true);

        mockMvc.perform(
                        post("/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nome", is("Teste")));
    }

    @Test
    void deveListarUsuarios() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Maria");
        usuario.setCpf("11122233344");
        usuario.setEmail("maria@email.com");
        usuario.setAtivo(true);
        repository.save(usuario);

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Maria")));
    }

    @Test
    void deveAtualizarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("Carlos");
        usuario.setCpf("99988877766");
        usuario.setEmail("carlos@email.com");
        usuario.setAtivo(true);
        Usuario salvo = repository.save(usuario);

        salvo.setNome("Carlos Atualizado");

        mockMvc.perform(
                        put("/usuarios/{id}", salvo.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(salvo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome", is("Carlos Atualizado")));
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setNome("João");
        usuario.setCpf("77766655544");
        usuario.setEmail("joao@email.com");
        usuario.setAtivo(true);
        Usuario salvo = repository.save(usuario);

        mockMvc.perform(delete("/usuarios/{id}", salvo.getId()))
                .andExpect(status().isNoContent());
    }
}