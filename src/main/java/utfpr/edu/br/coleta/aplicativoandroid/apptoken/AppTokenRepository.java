package utfpr.edu.br.coleta.aplicativoandroid.apptoken;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository para operações de banco de dados com AppToken.
 * 
 * @author Luiz Alberto dos Passos
 */
@Repository
public interface AppTokenRepository extends JpaRepository<AppToken, Long> {

    /**
     * Busca um token pelo valor do token e que esteja ativo.
     * 
     * @param token valor do token
     * @return Optional contendo o token se encontrado e ativo
     */
    Optional<AppToken> findByTokenAndAtivoTrue(String token);

    /**
     * Busca um token pelo ID do dispositivo.
     * 
     * @param deviceId ID do dispositivo
     * @return Optional contendo o token se encontrado
     */
    Optional<AppToken> findByDeviceId(String deviceId);

    /**
     * Lista todos os tokens ativos.
     * 
     * @return lista de tokens ativos
     */
    List<AppToken> findByAtivoTrue();

    /**
     * Lista todos os tokens inativos.
     *
     * @return lista de tokens inativos
     */
    List<AppToken> findByAtivoFalse();

    /**
     * Busca tokens que contenham o deviceId informado (busca parcial).
     *
     * @param deviceId termo de busca
     * @param pageable informações de paginação
     * @return página de tokens que correspondem à busca
     */
    Page<AppToken> findByDeviceIdContaining(String deviceId, Pageable pageable);
}

