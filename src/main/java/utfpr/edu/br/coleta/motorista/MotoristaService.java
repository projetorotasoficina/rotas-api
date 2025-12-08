package utfpr.edu.br.coleta.motorista;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.caminhao.Caminhao;
import utfpr.edu.br.coleta.caminhao.CaminhaoRepository;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.motorista.validator.CNHVeiculoValidator;

import java.util.List;

/**
 * Serviço responsável pelas regras de negócio da entidade Motorista.
 *
 * Estende CrudServiceImpl e implementa o método getRepository()
 * para fornecer o MotoristaRepository ao CRUD genérico.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Service
public class MotoristaService extends CrudServiceImpl<Motorista, Long> {

    private final MotoristaRepository repository;
    private final CaminhaoRepository caminhaoRepository;
    private final CNHVeiculoValidator cnhVeiculoValidator;

    /**
     * Construtor do serviço de Motorista.
     *
     * @param repository           repositório de Motorista
     * @param caminhaoRepository   repositório de Caminhão
     * @param cnhVeiculoValidator  validador de compatibilidade CNH x veículo
     */
    public MotoristaService(MotoristaRepository repository,
                            CaminhaoRepository caminhaoRepository,
                            CNHVeiculoValidator cnhVeiculoValidator) {
        this.repository = repository;
        this.caminhaoRepository = caminhaoRepository;
        this.cnhVeiculoValidator = cnhVeiculoValidator;
    }

    @Override
    protected MotoristaRepository getRepository() {
        return repository;
    }

    /**
     * Busca paginada de motoristas com filtro opcional por nome.
     *
     * @param pageable informações de paginação
     * @param search   termo de busca (nome)
     * @return página de motoristas
     */
    @Override
    public Page<Motorista> findAll(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return findAll(pageable);
        }
        return repository.findByNomeContainingIgnoreCase(search, pageable);
    }

    /**
     * Lista os caminhões compatíveis com a CNH do motorista informado.
     *
     * Regras aplicadas:
     * - Motorista deve existir
     * - Caminhões devem estar ativos
     * - Compatibilidade verificada usando CNHVeiculoValidator
     *
     * @param motoristaId ID do motorista
     * @return lista de caminhões compatíveis
     */
    @Transactional(readOnly = true)
    public List<Caminhao> listarCaminhoesCompativeis(Long motoristaId) {
        // Busca o motorista
        Motorista motorista = repository.findById(motoristaId)
                .orElseThrow(() -> new IllegalArgumentException("Motorista não encontrado."));

        // Busca caminhões ativos
        List<Caminhao> caminhoesAtivos = caminhaoRepository.findByAtivoTrue();

        // Filtra caminhões que o motorista pode dirigir de acordo com a CNH
        return caminhoesAtivos.stream()
                .filter(c -> cnhVeiculoValidator.podeConduzir(motorista, c))
                .toList();
    }

    /**
     * Lista os motoristas compatíveis com o caminhão informado.
     *
     * Regras aplicadas:
     * - Caminhão deve existir
     * - Motoristas devem estar ativos
     * - Compatibilidade verificada usando CNHVeiculoValidator
     *
     * @param caminhaoId ID do caminhão
     * @return lista de motoristas compatíveis
     */
    @Transactional(readOnly = true)
    public List<Motorista> listarMotoristasCompativeis(Long caminhaoId) {
        // Busca caminhão
        Caminhao caminhao = caminhaoRepository.findById(caminhaoId)
                .orElseThrow(() -> new IllegalArgumentException("Caminhão não encontrado."));

        // Busca motoristas ativos
        List<Motorista> motoristasAtivos = repository.findByAtivoTrue();

        // Filtra motoristas que podem conduzir este caminhão
        return motoristasAtivos.stream()
                .filter(m -> cnhVeiculoValidator.podeConduzir(m, caminhao))
                .toList();
    }
}