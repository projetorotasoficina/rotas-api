package utfpr.edu.br.coleta.aplicativoandroid.codigoativacao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Implementação do serviço de códigos de ativação.
 * 
 * Estende CrudServiceImpl para herdar operações CRUD básicas e implementa
 * métodos específicos para gerenciamento de códigos de ativação.
 * 
 * @author Luiz Alberto dos Passos
 */
@Service
@RequiredArgsConstructor
public class CodigoAtivacaoServiceImpl extends CrudServiceImpl<CodigoAtivacao, Long> 
        implements ICodigoAtivacaoService {

    private final CodigoAtivacaoRepository repository;
    
    private static final String CARACTERES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int TAMANHO_CODIGO = 24;
    private static final SecureRandom random = new SecureRandom();

    @Override
    protected JpaRepository<CodigoAtivacao, Long> getRepository() {
        return repository;
    }

    @Override
    @Transactional
    public CodigoAtivacao gerarNovoCodigo() {
        String codigo;
        
        // Gera código único (verifica se já existe)
        do {
            codigo = gerarCodigoAleatorio();
        } while (repository.existsByCodigo(codigo));

        CodigoAtivacao codigoAtivacao = CodigoAtivacao.builder()
                .codigo(codigo)
                .build();

        return repository.save(codigoAtivacao);
    }

    @Override
    public CodigoAtivacao buscarPorCodigo(String codigo) {
        return repository.findByCodigoAndAtivoTrue(codigo).orElse(null);
    }

    @Override
    @Transactional
    public void marcarComoUsado(String codigo, String deviceId) {
        repository.findByCodigoAndAtivoTrue(codigo).ifPresent(codigoAtivacao -> {
            codigoAtivacao.setUsado(true);
            codigoAtivacao.setDataUso(LocalDateTime.now());
            codigoAtivacao.setDeviceId(deviceId);
            repository.save(codigoAtivacao);
        });
    }

    /**
     * Gera um código aleatório de 24 caracteres.
     * 
     * @return código aleatório
     */
    private String gerarCodigoAleatorio() {
        StringBuilder codigo = new StringBuilder(TAMANHO_CODIGO);
        for (int i = 0; i < TAMANHO_CODIGO; i++) {
            codigo.append(CARACTERES.charAt(random.nextInt(CARACTERES.length())));
        }
        return codigo.toString();
    }
}

