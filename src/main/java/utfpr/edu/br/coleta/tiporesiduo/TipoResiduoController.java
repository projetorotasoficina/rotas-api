package utfpr.edu.br.coleta.tiporesiduo;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;

@RestController
@RequestMapping("/tiporesiduo")
public class TipoResiduoController extends CrudController<TipoResiduo, TipoResiduoDTO> {

    private final TipoResiduoService service;
    private final ModelMapper modelMapper;

    public TipoResiduoController(TipoResiduoService service, ModelMapper modelMapper) {
        super(TipoResiduo.class, TipoResiduoDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected TipoResiduoService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }
}
