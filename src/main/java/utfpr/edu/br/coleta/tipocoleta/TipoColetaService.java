package utfpr.edu.br.coleta.tipocoleta;

import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

/**
 * Serviço responsável pelas regras de negócio da entidade Tipo coleta.
 *
 * Estende CrudServiceImpl e implementa o método getRepository()
 * para fornecer o TipoColetaRepository ao CRUD genérico.
 *
 * Autor: Pedro Henrique Sauthier
 */
@Service
public class TipoColetaService extends CrudServiceImpl<TipoColeta, Long> {

    private final TipoColetaRepository repository;

    public TipoColetaService(TipoColetaRepository repository) {
        this.repository = repository;
    }

    @Override
    protected TipoColetaRepository getRepository() {
        return repository;
    }
}