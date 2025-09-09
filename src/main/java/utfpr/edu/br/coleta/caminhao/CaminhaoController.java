package utfpr.edu.br.coleta.caminhao;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;


/**
 * Controller responsável por expor os endpoints REST para operações de CRUD de Caminhão.
 *
 * Herda de CrudController, que já implementa endpoints genéricos
 * como GET, POST, PUT e DELETE.
 *
 * Autor: Luiz Alberto dos Passos
 */
@RestController
@RequestMapping("/caminhoes")
public class CaminhaoController extends CrudController<Caminhao, CaminhaoDTO> {

    private final CaminhaoService service;
    private final ModelMapper modelMapper;

    public CaminhaoController(CaminhaoService service, ModelMapper modelMapper) {
        super(Caminhao.class, CaminhaoDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected CaminhaoService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }
}