package utfpr.edu.br.coleta.caminhao;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

@Service
public class CaminhaoServiceImpl extends CrudServiceImpl<Caminhao, Long> implements CaminhaoService {

    private final CaminhaoRepository repository;

    public CaminhaoServiceImpl(CaminhaoRepository repository) {
        this.repository = repository;
    }

    @Override
    protected CaminhaoRepository getRepository() {
        return repository;
    }

    @Override
    public void delete(Long id) {
        Caminhao caminhao = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caminhão não encontrado"));

        //if (caminhao.getTrajetos() != null && !caminhao.getTrajetos().isEmpty()) {
       //     throw new IllegalStateException("Não é possível excluir caminhão vinculado a trajetos.");
      //  }

        repository.delete(caminhao);
    }
}