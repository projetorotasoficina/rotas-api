package utfpr.edu.br.coleta.aplicativoandroid.codigoativacao;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;

import java.util.Map;

/**
 * Controller para gerenciamento de códigos de ativação.
 * 
 * Herda de CrudController para ter endpoints CRUD básicos e adiciona
 * endpoint específico para geração de novos códigos.
 * 
 * Apenas usuários com role SUPER_ADMIN podem acessar estes endpoints.
 * 
 * @author Luiz Alberto dos Passos
 */
@RestController
@RequestMapping("/codigosativacao")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class CodigoAtivacaoController extends CrudController<CodigoAtivacao, CodigoAtivacaoDTO> {

    private final ICodigoAtivacaoService service;
    private final ModelMapper modelMapper;

    public CodigoAtivacaoController(ICodigoAtivacaoService service, ModelMapper modelMapper) {
        super(CodigoAtivacao.class, CodigoAtivacaoDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected ICodigoAtivacaoService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    /**
     * Gera um novo código de ativação.
     * 
     * POST /api/codigosativacao/gerar
     * 
     * @return ResponseEntity com o código gerado
     */
    @PostMapping("/gerar")
    public ResponseEntity<?> gerarCodigo() {
        CodigoAtivacao codigo = service.gerarNovoCodigo();
        
        return ResponseEntity.ok(Map.of(
            "status", "sucesso",
            "codigo", codigo.getCodigo(),
            "dataGeracao", codigo.getDataGeracao()
        ));
    }
}

