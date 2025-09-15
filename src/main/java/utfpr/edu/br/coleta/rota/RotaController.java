package utfpr.edu.br.coleta.rota;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;

/**
 * Controller responsável por expor os endpoints REST para operações de CRUD de Rota.
 *
 * Herda de CrudController, que já implementa endpoints genéricos
 * como GET, POST, PUT e DELETE.
 *
 * Autor: Pedro Henrique Sauthier
 */
@RestController
@RequestMapping("/rota")
public class RotaController extends CrudController<Rota, RotaDTO> {

    private final RotaService service;
    private final ModelMapper modelMapper;

    public RotaController(RotaService service, ModelMapper modelMapper) {
        super(Rota.class, RotaDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected RotaService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }
}