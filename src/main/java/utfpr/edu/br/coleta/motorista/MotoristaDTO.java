package utfpr.edu.br.coleta.motorista.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) utilizado para transferir
 * informações de Motorista entre camadas do sistema.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Data
public class MotoristaDTO {
    private Long id;
    private String nome;
    private String cpf;
    private String cnhCategoria;
    private LocalDate cnhValidade;
    private Boolean ativo;
}