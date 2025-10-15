package utfpr.edu.br.coleta.aplicativoandroid.codigoativacao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de CodigoAtivacao.
 * 
 * @author Luiz Alberto dos Passos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodigoAtivacaoDTO {

    private Long id;
    private String codigo;
    private Boolean usado;
    private LocalDateTime dataGeracao;
    private LocalDateTime dataUso;
    private String deviceId;
    private Boolean ativo;
}

