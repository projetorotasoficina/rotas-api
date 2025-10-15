package utfpr.edu.br.coleta.aplicativoandroid.apptoken;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;

/**
 * Controller para gerenciamento de tokens de aplicativos Android.
 * 
 * Herda de CrudController para ter endpoints CRUD básicos e adiciona
 * endpoints específicos para revogação e reativação de tokens.
 * 
 * Apenas usuários com role SUPER_ADMIN podem acessar estes endpoints.
 * 
 * @author Luiz Alberto dos Passos
 */
@RestController
@RequestMapping("/apptokens")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AppTokenController extends CrudController<AppToken, AppTokenDTO> {

    private final IAppTokenService service;
    private final ModelMapper modelMapper;

    public AppTokenController(IAppTokenService service, ModelMapper modelMapper) {
        super(AppToken.class, AppTokenDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected IAppTokenService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    /**
     * Revoga um token (desativa).
     * 
     * PUT /api/apptokens/{id}/revogar
     * 
     * @param id ID do token
     * @return ResponseEntity sem conteúdo
     */
    @PutMapping("/{id}/revogar")
    public ResponseEntity<?> revogarToken(@PathVariable Long id) {
        service.revokeToken(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Reativa um token.
     * 
     * PUT /api/apptokens/{id}/reativar
     * 
     * @param id ID do token
     * @return ResponseEntity sem conteúdo
     */
    @PutMapping("/{id}/reativar")
    public ResponseEntity<?> reativarToken(@PathVariable Long id) {
        service.reactivateToken(id);
        return ResponseEntity.ok().build();
    }
}

