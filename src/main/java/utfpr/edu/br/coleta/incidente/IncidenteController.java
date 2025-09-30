package utfpr.edu.br.coleta.incidente;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.incidente.dto.IncidenteDTO;

@RestController
@RequestMapping("/api/incidentes")
public class IncidenteController extends CrudController<Incidente, IncidenteDTO> {

    private final IncidenteService service;
    private final ModelMapper modelMapper;

    public IncidenteController(
            IncidenteService service,
            ModelMapper modelMapper
    ){
        super(Incidente.class, IncidenteDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected IncidenteService getService() {return service;}
    @Override
    protected ModelMapper getModelMapper() {return modelMapper;}
}
