package utfpr.edu.br.coleta.caminhao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.motorista.MotoristaDTO;
import utfpr.edu.br.coleta.motorista.MotoristaService;
import utfpr.edu.br.coleta.rota.dto.RotaDTO;

import java.util.List;

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
    private final MotoristaService motoristaService; // necessário para o endpoint novo

    public CaminhaoController(CaminhaoService service,
                              ModelMapper modelMapper,
                              MotoristaService motoristaService) {
        super(Caminhao.class, CaminhaoDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
        this.motoristaService = motoristaService;
    }

    @Override
    protected CaminhaoService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    /**
     * Retorna os motoristas compatíveis com o caminhão informado,
     * verificando CNH, validade, categoria e status ativo.
     *
     * @param id ID do caminhão
     * @return lista de motoristas aptos a dirigir o veículo
     */
    @Operation(
            summary = "Lista motoristas compatíveis com o caminhão",
            description = "Retorna apenas motoristas ativos cuja CNH é compatível com o tipo de veículo fornecido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Caminhão não encontrado")
    })
    @GetMapping("/{id}/motoristas-compativeis")
    public List<MotoristaDTO> listarMotoristasCompativeis(@PathVariable Long id) {
        return motoristaService.listarMotoristasCompativeis(id).stream()
                .map(m -> modelMapper.map(m, MotoristaDTO.class))
                .toList();
    }

    /**
     * Retorna as rotas compatíveis com o caminhão informado.
     *
     * A compatibilidade é determinada pelo tipo de coleta e pelo tipo
     * de resíduo definidos no caminhão e na rota. Apenas rotas ativas
     * e totalmente compatíveis são retornadas.
     *
     * Este endpoint é utilizado pelo sistema administrativo para
     * organizar a logística e evitar incompatibilidades de operação.
     *
     * @param id ID do caminhão
     * @return lista de rotas compatíveis com o caminhão
     */
    @Operation(
            summary = "Lista rotas compatíveis com o caminhão",
            description = "Retorna somente rotas ativas cujo tipo de coleta e tipo de resíduo são compatíveis com o caminhão fornecido."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Caminhão não encontrado")
    })
    @GetMapping("/{id}/rotas-compativeis")
    public List<RotaDTO> listarRotasCompativeis(@PathVariable Long id) {
        return service.listarRotasCompativeis(id).stream()
                .map(rota -> modelMapper.map(rota, RotaDTO.class))
                .toList();
    }
}