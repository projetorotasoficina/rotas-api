package utfpr.edu.br.coleta.tipocoleta;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;

/**
 * Controller responsável por expor os endpoints REST para operações de CRUD de Tipo Coleta.
 *
 * Herda de CrudController, que já implementa endpoints genéricos
 * como GET, POST, PUT e DELETE.
 *
 * Autor: Pedro Henrique Sauthier
 */
@RestController
@RequestMapping("/tipocoleta")
public class TipoColetaController extends CrudController<TipoColeta, TipoColetaDTO, Long> {

    private final TipoColetaService service;
    private final ModelMapper modelMapper;

    public TipoColetaController(TipoColetaService service, ModelMapper modelMapper) {
        super(TipoColeta.class, TipoColetaDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected TipoColetaService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }
}