package utfpr.edu.br.coleta.rota;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

/**
 * Serviço responsável pelas regras de negócio da entidade Rota.
 *
 * Estende CrudServiceImpl e implementa o método getRepository()
 * para fornecer o RotaRepository ao CRUD genérico.
 *
 * Autor: Pedro Henrique Sauthier
 */
@Service
public class RotaService extends CrudServiceImpl<Rota, Long> {

    private final RotaRepository repository;

    public RotaService(RotaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected RotaRepository getRepository() {
        return repository;
    }

    public Optional<Rota> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Rota> findAll(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return findAll(pageable);
        }
        return repository.findByNomeContainingIgnoreCase(search, pageable);
    }
}