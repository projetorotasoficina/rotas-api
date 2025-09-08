package utfpr.edu.br.coleta.motorista;

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
 * Testes unitários para MotoristaService.
 * Utiliza Mockito para simular o comportamento do repositório.
 *
 * Autor: Luiz Alberto dos Passos
 */
@ExtendWith(MockitoExtension.class)
class MotoristaServiceTest {

    @Mock
    private MotoristaRepository repository;

    @InjectMocks
    private MotoristaService service;

    /**
     * Deve salvar um motorista e retornar a entidade persistida.
     * Verifica se os dados retornados correspondem aos informados.
     */
    @Test
    void deveSalvarMotorista() {
        Motorista motorista = new Motorista();
        motorista.setNome("Maria");
        motorista.setCpf("12345678900");
        motorista.setAtivo(true);

        Motorista salvo = new Motorista();
        salvo.setId(1L);
        salvo.setNome("Maria");
        salvo.setCpf("12345678900");
        salvo.setAtivo(true);

        when(repository.save(any(Motorista.class))).thenReturn(salvo);

        Motorista resultado = service.save(motorista);

        assertNotNull(resultado.getId());
        assertEquals("Maria", resultado.getNome());
        assertEquals("12345678900", resultado.getCpf());
        assertTrue(resultado.getAtivo());
    }

    /**
     * Deve buscar um motorista pelo ID.
     * Espera encontrar a entidade e validar os dados.
     */
    @Test
    void deveBuscarMotoristaPorId() {
        Motorista motorista = new Motorista();
        motorista.setId(1L);
        motorista.setNome("João");
        motorista.setCpf("98765432100");
        motorista.setAtivo(true);

        when(repository.findById(1L)).thenReturn(Optional.of(motorista));

        Motorista resultado = service.findOne(1L);

        assertEquals("João", resultado.getNome());
        assertTrue(resultado.getAtivo());
    }

    /**
     * Deve listar todos os motoristas.
     * Espera uma lista com 2 registros.
     */
    @Test
    void deveListarMotoristas() {
        Motorista m1 = new Motorista();
        m1.setId(1L);
        m1.setNome("Ana");
        m1.setCpf("111");
        m1.setAtivo(true);

        Motorista m2 = new Motorista();
        m2.setId(2L);
        m2.setNome("Carlos");
        m2.setCpf("222");
        m2.setAtivo(true);

        when(repository.findAll()).thenReturn(List.of(m1, m2));

        List<Motorista> motoristas = service.findAll();

        assertEquals(2, motoristas.size());
        assertEquals("Ana", motoristas.get(0).getNome());
    }

    /**
     * Deve deletar um motorista pelo ID.
     * Verifica se o repositório foi chamado corretamente.
     */
    @Test
    void deveDeletarMotorista() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }
}