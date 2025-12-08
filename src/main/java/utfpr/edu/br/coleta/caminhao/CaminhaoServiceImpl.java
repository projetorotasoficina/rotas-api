package utfpr.edu.br.coleta.caminhao;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.rota.Rota;
import utfpr.edu.br.coleta.rota.RotaRepository;
import utfpr.edu.br.coleta.rota.validator.RotaCaminhaoValidator;

import java.util.List;

@Service
public class CaminhaoServiceImpl extends CrudServiceImpl<Caminhao, Long> implements CaminhaoService {

    private final CaminhaoRepository repository;
    private final RotaRepository rotaRepository;
    private final RotaCaminhaoValidator rotaCaminhaoValidator;

    public CaminhaoServiceImpl(CaminhaoRepository repository,
                               RotaRepository rotaRepository,
                               RotaCaminhaoValidator rotaCaminhaoValidator) {
        this.repository = repository;
        this.rotaRepository = rotaRepository;
        this.rotaCaminhaoValidator = rotaCaminhaoValidator;
    }

    @Override
    protected CaminhaoRepository getRepository() {
        return repository;
    }

    @Override
    public void delete(Long id) {
        Caminhao caminhao = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caminhão não encontrado"));

        // if (caminhao.getTrajetos() != null && !caminhao.getTrajetos().isEmpty()) {
        //     throw new IllegalStateException("Não é possível excluir caminhão vinculado a trajetos.");
        // }

        repository.delete(caminhao);
    }

    @Override
    public Page<Caminhao> findAll(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return findAll(pageable);
        }
        return repository.findByModeloContainingIgnoreCaseOrPlacaContainingIgnoreCase(search, search, pageable);
    }

    /**
     * Lista as rotas compatíveis com o caminhão informado,
     * considerando tipo de coleta e tipo de resíduo.
     *
     * @param caminhaoId ID do caminhão
     * @return lista de rotas compatíveis
     */
    @Override
    public List<Rota> listarRotasCompativeis(Long caminhaoId) {

        Caminhao caminhao = repository.findById(caminhaoId)
                .orElseThrow(() -> new EntityNotFoundException("Caminhão não encontrado"));

        return rotaRepository.findByTipoColetaIdAndTipoResiduoIdAndAtivoTrue(
                caminhao.getTipoColeta().getId(),
                caminhao.getResiduo().getId()
        );
    }
}