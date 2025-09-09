package utfpr.edu.br.coleta.motorista;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class MotoristaControllerTest {

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock private MotoristaService service;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private MotoristaController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void deveListarMotoristas() throws Exception {
        Motorista motorista = new Motorista();
        motorista.setId(1L);
        motorista.setNome("João");
        motorista.setCpf("12345678901");
        motorista.setAtivo(true);

        MotoristaDTO dto = new MotoristaDTO();
        dto.setId(1L);
        dto.setNome("João");
        dto.setCpf("12345678901");
        dto.setAtivo(true);

        when(service.findAll()).thenReturn(List.of(motorista));
        when(modelMapper.map(motorista, MotoristaDTO.class)).thenReturn(dto);

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

        MotoristaDTO salvoDto = new MotoristaDTO();
        salvoDto.setId(1L);
        salvoDto.setNome("Maria");
        salvoDto.setCpf("98765432100");
        salvoDto.setAtivo(true);

        when(modelMapper.map(any(MotoristaDTO.class), any())).thenReturn(salvo);
        when(service.save(any(Motorista.class))).thenReturn(salvo);
        when(modelMapper.map(salvo, MotoristaDTO.class)).thenReturn(salvoDto);

        mockMvc.perform(post("/api/motoristas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Maria"));
    }

    @Test
    void deveAtualizarMotorista() throws Exception {
        Motorista existente = new Motorista();
        existente.setId(1L);
        existente.setNome("Carlos");
        existente.setCpf("11122233344");
        existente.setAtivo(true);

        Motorista atualizado = new Motorista();
        atualizado.setId(1L);
        atualizado.setNome("Carlos Silva");
        atualizado.setCpf("11122233344");
        atualizado.setAtivo(true);

        MotoristaDTO entrada = new MotoristaDTO();
        entrada.setId(1L);
        entrada.setNome("Carlos Silva");
        entrada.setCpf("11122233344");
        entrada.setAtivo(true);

        MotoristaDTO atualizadoDto = new MotoristaDTO();
        atualizadoDto.setId(1L);
        atualizadoDto.setNome("Carlos Silva");
        atualizadoDto.setCpf("11122233344");
        atualizadoDto.setAtivo(true);

        when(service.save(any(Motorista.class))).thenReturn(atualizado);
        when(modelMapper.map(any(MotoristaDTO.class), any())).thenReturn(atualizado);
        when(modelMapper.map(atualizado, MotoristaDTO.class)).thenReturn(atualizadoDto);

        mockMvc.perform(put("/api/motoristas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Carlos Silva"));
    }

    @Test
    void deveDeletarMotorista() throws Exception {
        doNothing().when(service).delete(1L);
        mockMvc.perform(delete("/api/motoristas/1"))
                .andExpect(status().isNoContent());
    }
}