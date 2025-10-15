package utfpr.edu.br.coleta.motorista;

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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do MotoristaController.
 * Usa @SpringBootTest para carregar o contexto completo e suportar herança de CrudController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = "SUPER_ADMIN")
class MotoristaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private MotoristaService service;

    @Test
    void deveListarMotoristas() throws Exception {
        Motorista motorista = new Motorista();
        motorista.setId(1L);
        motorista.setNome("João");
        motorista.setCpf("12345678901");
        motorista.setAtivo(true);

        when(service.findAll()).thenReturn(List.of(motorista));

        mockMvc.perform(get("/api/motoristas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("João"));
    }

    @Test
    void deveCriarMotorista() throws Exception {
        MotoristaDTO dto = new MotoristaDTO();
        dto.setNome("Maria");
        dto.setCpf("98765432100");
        dto.setAtivo(true);

        Motorista salvo = new Motorista();
        salvo.setId(1L);
        salvo.setNome("Maria");
        salvo.setCpf("98765432100");
        salvo.setAtivo(true);

        when(service.save(any(Motorista.class))).thenReturn(salvo);

        mockMvc.perform(post("/api/motoristas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void deveAtualizarMotorista() throws Exception {
        MotoristaDTO entrada = new MotoristaDTO();
        entrada.setId(1L);
        entrada.setNome("Carlos Silva");
        entrada.setCpf("11122233344");
        entrada.setAtivo(true);

        Motorista atualizado = new Motorista();
        atualizado.setId(1L);
        atualizado.setNome("Carlos Silva");
        atualizado.setCpf("11122233344");
        atualizado.setAtivo(true);

        when(service.save(any(Motorista.class))).thenReturn(atualizado);

        mockMvc.perform(put("/api/motoristas/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Silva"));
    }

    @Test
    void deveDeletarMotorista() throws Exception {
        doNothing().when(service).delete(1L);
        mockMvc.perform(delete("/api/motoristas/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
