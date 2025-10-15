package utfpr.edu.br.coleta.aplicativoandroid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.aplicativoandroid.apptoken.AppToken;
import utfpr.edu.br.coleta.aplicativoandroid.apptoken.IAppTokenService;
import utfpr.edu.br.coleta.aplicativoandroid.codigoativacao.CodigoAtivacao;
import utfpr.edu.br.coleta.aplicativoandroid.codigoativacao.ICodigoAtivacaoService;

import java.util.Map;

/**
 * Controller para ativação de aplicativos Android.
 * 
 * Endpoint público que permite que um dispositivo Android use um código de ativação
 * para obter um token permanente de acesso à API.
 * 
 * @author Luiz Alberto dos Passos
 */
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppActivationController {

    private final ICodigoAtivacaoService codigoService;
    private final IAppTokenService appTokenService;

    /**
     * Ativa um dispositivo Android usando um código de ativação.
     * 
     * POST /api/app/activate
     * 
     * Body:
     * {
     *   "codigo": "codigo-de-ativacao",
     *   "deviceId": "id-unico-do-dispositivo"
     * }
     * 
     * Resposta de sucesso:
     * {
     *   "status": "autorizado",
     *   "appToken": "token-permanente"
     * }
     * 
     * @param req requisição contendo código e deviceId
     * @return ResponseEntity com o token ou erro
     */
    @PostMapping("/activate")
    public ResponseEntity<?> activate(@RequestBody ActivationRequest req) {
        // Validação de parâmetros
        if (req.getCodigo() == null || req.getCodigo().isBlank() ||
            req.getDeviceId() == null || req.getDeviceId().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "erro",
                "mensagem", "Parâmetros obrigatórios ausentes (codigo, deviceId)"
            ));
        }

        // Busca código de ativação
        CodigoAtivacao codigo = codigoService.buscarPorCodigo(req.getCodigo());
        if (codigo == null || codigo.getUsado()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "status", "erro",
                "mensagem", "Código inválido ou já utilizado"
            ));
        }

        // Gera/retorna token permanente vinculado ao device
        AppToken appToken = appTokenService.createOrGetToken(req.getDeviceId());

        // Marca o código como usado
        codigoService.marcarComoUsado(req.getCodigo(), req.getDeviceId());

        return ResponseEntity.ok(Map.of(
            "status", "autorizado",
            "appToken", appToken.getToken()
        ));
    }
}

