package utfpr.edu.br.coleta.usuario;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.generics.ICrudService;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "UsuarioController", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController extends CrudController<Usuario, Usuario> {

    private final IUsuarioService usuarioService;
    private final ModelMapper modelMapper;

    public UsuarioController(IUsuarioService usuarioService, ModelMapper modelMapper) {
        super(Usuario.class, Usuario.class); // D = Usuario (sem DTO separado)
        this.usuarioService = usuarioService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected ICrudService<Usuario, Long> getService() {
        return usuarioService;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    @GetMapping("/meu-perfil")
    public ResponseEntity<Usuario> getMeuPerfil() {
        return ResponseEntity.ok(usuarioService.obterUsuarioLogado());
    }
}