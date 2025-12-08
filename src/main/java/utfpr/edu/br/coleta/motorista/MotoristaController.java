package utfpr.edu.br.coleta.motorista;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.caminhao.CaminhaoDTO;
import utfpr.edu.br.coleta.generics.CrudController;

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
public class MotoristaController extends CrudController<Motorista, MotoristaDTO> {

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

    /**
     * Retorna os caminhões compatíveis com a CNH do motorista informado.
     *
     * @param id ID do motorista
     * @return lista de caminhões compatíveis
     */
    @Operation(
            summary = "Lista caminhões compatíveis",
            description = "Retorna todos os caminhões que o motorista pode dirigir com base na sua categoria de CNH, status e validade."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta realizada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Motorista não encontrado")
    })
    @GetMapping("/{id}/caminhoes-compativeis")
    public java.util.List<CaminhaoDTO> listarCaminhoesCompativeis(@PathVariable Long id) {
        return service.listarCaminhoesCompativeis(id).stream()
                .map(c -> modelMapper.map(c, CaminhaoDTO.class))
                .toList();
    }
}