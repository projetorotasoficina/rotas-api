package utfpr.edu.br.coleta.tipocoleta;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TipoColetaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "teste", roles = {"USER"})
    void fluxoCompletoCrud() throws Exception {
        // Criar
        String novoJson = "{\"nome\":\"Metal\"}";

        String response = mockMvc.perform(post("/tipocoleta")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(novoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nome").value("Metal"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrair ID criado
        Long id = com.jayway.jsonpath.JsonPath.parse(response).read("$.id", Long.class);

        // Buscar
        mockMvc.perform(get("/tipocoleta/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Metal"));

        // Listar
        mockMvc.perform(get("/tipocoleta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Metal"));

        // Deletar
        mockMvc.perform(delete("/tipocoleta/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // Verificar que não existe mais
        mockMvc.perform(get("/tipocoleta/{id}", id))
                .andExpect(status().isNotFound());
    }
}
