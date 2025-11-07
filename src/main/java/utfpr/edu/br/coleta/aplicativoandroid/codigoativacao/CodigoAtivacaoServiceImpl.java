package utfpr.edu.br.coleta.aplicativoandroid.codigoativacao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * Inclui geração de códigos únicos e marcação de uso de códigos existentes.
 * 
 * Autor: Luiz Alberto dos Passos
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CodigoAtivacaoServiceImpl 
        extends CrudServiceImpl<CodigoAtivacao, Long> 
        implements ICodigoAtivacaoService {

    private final CodigoAtivacaoRepository repository;

    private static final String CARACTERES = "0123456789";  // ✅ Apenas números
    private static final int TAMANHO_CODIGO = 6;
    private static final SecureRandom random = new SecureRandom();

    @Override
    protected JpaRepository<CodigoAtivacao, Long> getRepository() {
        return repository;
    }

    /**
     * Retorna uma página de códigos de ativação filtrados por busca textual.
     * Se search for null ou vazio, retorna todos os códigos.
     *
     * @param pageable objeto com paginação e ordenação
     * @param search termo de busca (opcional)
     * @return página de códigos filtrados
     */
    @Override
    public Page<CodigoAtivacao> findAll(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return findAll(pageable);
        }
        return repository.findByCodigoContaining(search, pageable);
    }

    /**
     * Gera um novo código de ativação único e o salva no banco de dados.
     * 
     * @return entidade CodigoAtivacao salva
     */
    @Override
    public CodigoAtivacao gerarNovoCodigo() {
        String codigo;

        System.out.println("[CodigoAtivacaoService] Iniciando geração de novo código...");

        // Gera código único (garante que não exista outro igual)
        do {
            codigo = gerarCodigoAleatorio();
        } while (repository.existsByCodigo(codigo));

        CodigoAtivacao codigoAtivacao = CodigoAtivacao.builder()
                .codigo(codigo)
                .dataGeracao(LocalDateTime.now())
                .ativo(true)
                .usado(false)
                .build();

        System.out.println("[CodigoAtivacaoService] Salvando código: " + codigo);

        CodigoAtivacao salvo = repository.save(codigoAtivacao);

        System.out.println("[CodigoAtivacaoService] Código salvo com ID: " + salvo.getId());
        return salvo;
    }

    /**
     * Busca um código de ativação ativo pelo valor do código.
     *
     * @param codigo valor do código
     * @return código ativo ou null se não encontrado
     */
    @Override
    public CodigoAtivacao buscarPorCodigo(String codigo) {
        System.out.println("[CodigoAtivacaoService] Buscando código: " + codigo);
        return repository.findByCodigoAndAtivoTrue(codigo).orElse(null);
    }

    /**
     * Marca o código como usado e associa o ID do dispositivo.
     *
     * @param codigo   código a ser marcado como usado
     * @param deviceId identificador do dispositivo
     */
    @Override
    public void marcarComoUsado(String codigo, String deviceId) {
        repository.findByCodigoAndAtivoTrue(codigo).ifPresent(codigoAtivacao -> {
            System.out.println("[CodigoAtivacaoService] Marcando código como usado: " + codigo);
            codigoAtivacao.setUsado(true);
            codigoAtivacao.setDataUso(LocalDateTime.now());
            codigoAtivacao.setDeviceId(deviceId);
            repository.save(codigoAtivacao);
        });
    }

    /**
     * Gera um código aleatório de 24 caracteres alfanuméricos.
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
