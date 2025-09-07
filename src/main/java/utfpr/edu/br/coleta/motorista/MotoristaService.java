package utfpr.edu.br.coleta.motorista;

import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

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

    public MotoristaService(MotoristaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected MotoristaRepository getRepository() {
        return repository;
    }
}