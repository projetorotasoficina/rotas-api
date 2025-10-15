package utfpr.edu.br.coleta.aplicativoandroid.apptoken;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementação do serviço de tokens de aplicativos Android.
 * 
 * Estende CrudServiceImpl para herdar operações CRUD básicas e implementa
 * métodos específicos para gerenciamento de tokens.
 * 
 * @author Luiz Alberto dos Passos
 */
@Service
@RequiredArgsConstructor
public class AppTokenServiceImpl extends CrudServiceImpl<AppToken, Long> 
        implements IAppTokenService {

    private final AppTokenRepository repository;

    @Override
    protected JpaRepository<AppToken, Long> getRepository() {
        return repository;
    }

    @Override
    @Transactional
    public AppToken createOrGetToken(String deviceId) {
        // Verifica se já existe token para este dispositivo
        return repository.findByDeviceId(deviceId)
                .orElseGet(() -> {
                    // Cria novo token
                    AppToken token = AppToken.builder()
                            .token(UUID.randomUUID().toString())
                            .deviceId(deviceId)
                            .build();
                    return repository.save(token);
                });
    }

    @Override
    public boolean isValidToken(String token) {
        return repository.findByTokenAndAtivoTrue(token).isPresent();
    }

    @Override
    @Transactional
    public void updateLastAccess(String token) {
        repository.findByTokenAndAtivoTrue(token).ifPresent(appToken -> {
            appToken.setUltimoAcesso(LocalDateTime.now());
            appToken.setTotalAcessos(appToken.getTotalAcessos() + 1);
            repository.save(appToken);
        });
    }

    @Override
    @Transactional
    public void revokeToken(Long id) {
        AppToken token = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Token não encontrado"));
        token.setAtivo(false);
        repository.save(token);
    }

    @Override
    @Transactional
    public void reactivateToken(Long id) {
        AppToken token = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Token não encontrado"));
        token.setAtivo(true);
        repository.save(token);
    }
}

