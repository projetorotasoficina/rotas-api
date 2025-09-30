package utfpr.edu.br.coleta.incidente;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

@Service
public class IncidenteService extends CrudServiceImpl<Incidente, Long> {

    private final IncidenteRepository repository;

    public IncidenteService(IncidenteRepository repository) {this.repository = repository;}

    @Override
    protected IncidenteRepository getRepository() {return repository;}

}
