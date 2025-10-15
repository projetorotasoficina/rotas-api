package utfpr.edu.br.coleta.aplicativoandroid;

import lombok.Data;

/**
 * DTO para requisição de ativação de aplicativo Android.
 * 
 * Contém o código de ativação e o ID do dispositivo.
 * 
 * @author Luiz Alberto dos Passos
 */
@Data
public class ActivationRequest {
    private String codigo;
    private String deviceId;
}

