package utfpr.edu.br.coleta.aplicativoandroid.apptoken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para transferência de dados de AppToken.
 * 
 * @author Luiz Alberto dos Passos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppTokenDTO {

    private Long id;
    private String token;
    private String deviceId;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime ultimoAcesso;
    private Long totalAcessos;
}

