package utfpr.edu.br.coleta.usuario;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.usuario.dto.MoradorLogadoDTO;
import utfpr.edu.br.coleta.usuario.dto.MoradorUpdateDTO;

@RestController
@RequestMapping("/usuarios")
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

    /**
     * Endpoint público para cadastro de novos moradores.
     *
     * @param cadastroDTO dados do morador a ser cadastrado
     * @return usuário cadastrado com role ROLE_MORADOR
     */
    @io.swagger.v3.oas.annotations.Operation(
        summary = "Cadastrar novo morador",
        description = "Endpoint público para cadastro de novos moradores no sistema"
    )
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Morador cadastrado com sucesso"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Dados inválidos ou usuário já cadastrado")
    })
    @PostMapping("/morador")
    public ResponseEntity<Usuario> cadastrarMorador(
            @jakarta.validation.Valid @RequestBody utfpr.edu.br.coleta.usuario.dto.MoradorCadastroDTO cadastroDTO) {
        try {
            Usuario morador = usuarioService.cadastrarMorador(cadastroDTO);
            return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(morador);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/meu-perfil/morador")
    public ResponseEntity<MoradorLogadoDTO> getMeuPerfilCompleto() {
        return ResponseEntity.ok(usuarioService.obterMoradorLogadoCompleto());
    }

    // NOVO: Atualizar dados do morador logado
    @PutMapping("/meu-perfil")
    public ResponseEntity<MoradorLogadoDTO> atualizarMeuPerfil(@RequestBody MoradorUpdateDTO dto) {
        var atualizado = usuarioService.atualizarMoradorLogado(dto);
        if (atualizado == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(atualizado);
    }

    // NOVO: Excluir conta do usuário logado
    @DeleteMapping("/meu-perfil")
    public ResponseEntity<Void> excluirMinhaConta() {
        usuarioService.excluirContaLogado();
        return ResponseEntity.noContent().build();
    }
}
