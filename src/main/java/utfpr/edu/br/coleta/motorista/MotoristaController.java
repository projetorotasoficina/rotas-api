package utfpr.edu.br.coleta.motorista;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.motorista.dto.MotoristaDTO;

/**
 * Controller responsável por expor os endpoints REST para operações de CRUD de Motorista.
 *
 * Herda de CrudController, que já implementa endpoints genéricos
 * como GET, POST, PUT e DELETE.
 *
 * Autor: Luiz Alberto dos Passos
 */
@RestController
@RequestMapping("/motoristas")
public class MotoristaController extends CrudController<Motorista, MotoristaDTO, Long> {

    private final MotoristaService service;
    private final ModelMapper modelMapper;

    public MotoristaController(MotoristaService service, ModelMapper modelMapper) {
        super(Motorista.class, MotoristaDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected MotoristaService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }
}