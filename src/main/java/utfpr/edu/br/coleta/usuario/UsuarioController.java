package utfpr.edu.br.coleta.usuario;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;

/**
 * Controller responsável por expor endpoints de CRUD de Usuário.
 *
 * Autor: Luiz Alberto dos Passos
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends CrudController<Usuario, UsuarioDTO, Long> {

    private final UsuarioService service;
    private final ModelMapper modelMapper;

    public UsuarioController(UsuarioService service, ModelMapper modelMapper) {
        super(Usuario.class, UsuarioDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected UsuarioService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }
}