package utfpr.edu.br.coleta.usuario;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import utfpr.edu.br.coleta.generics.ICrudService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes do UsuarioController usando MockMvc standalone.
 * O Controller estende CrudController<Usuario, Usuario, Long>,
 * então o ModelMapper é chamado mas mapeia Usuario -> Usuario (identidade).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // O controller espera IUsuarioService no construtor → mockamos a interface
    @Mock private IUsuarioService usuarioService;

    // ModelMapper é usado pelo CrudController para mapear Entidade <-> DTO
    @Mock private org.modelmapper.ModelMapper modelMapper;

    @InjectMocks private UsuarioController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

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

        // Como D = Usuario e T = Usuario, tratamos o map como identidade
        when(modelMapper.map(any(Usuario.class), eq(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        when(usuarioService.save(any(Usuario.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/usuarios")
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

        // Identidade no mapeamento entidade -> DTO
        when(modelMapper.map(any(Usuario.class), eq(Usuario.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome", is("Maria")));
    }

    @Test
    void deveDeletarUsuario() throws Exception {
        // Nenhum stub extra — delete não usa o ModelMapper
        mockMvc.perform(delete("/api/usuarios/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}