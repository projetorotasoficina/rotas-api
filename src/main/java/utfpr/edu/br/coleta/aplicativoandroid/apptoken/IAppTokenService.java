package utfpr.edu.br.coleta.aplicativoandroid.apptoken;

import utfpr.edu.br.coleta.generics.ICrudService;

/**
 * Interface de serviço para operações com tokens de aplicativos Android.
 * 
 * Estende ICrudService para herdar operações CRUD básicas e adiciona
 * métodos específicos para gerenciamento de tokens.
 * 
 * @author Luiz Alberto dos Passos
 */
public interface IAppTokenService extends ICrudService<AppToken, Long> {

    /**
     * Cria ou retorna um token existente para um dispositivo.
     * 
     * @param deviceId ID do dispositivo
     * @return token criado ou existente
     */
    AppToken createOrGetToken(String deviceId);

    /**
     * Valida se um token é válido e está ativo.
     * 
     * @param token token a ser validado
     * @return true se válido, false caso contrário
     */
    boolean isValidToken(String token);

    /**
     * Atualiza a data do último acesso de um token.
     * 
     * @param token token a ser atualizado
     */
    void updateLastAccess(String token);

    /**
     * Revoga um token (desativa).
     * 
     * @param id ID do token
     */
    void revokeToken(Long id);

    /**
     * Reativa um token.
     * 
     * @param id ID do token
     */
    void reactivateToken(Long id);
}

