package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoBatchResponseDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoDTO;

import java.util.List;

@RestController
@RequestMapping("/api/pontos-trajeto")
public class PontoTrajetoController extends CrudController<PontoTrajeto, PontoTrajetoDTO> {

    private final IPontoTrajetoService service;
    private final ModelMapper modelMapper;

    public PontoTrajetoController(IPontoTrajetoService service, ModelMapper modelMapper) {
        super(PontoTrajeto.class, PontoTrajetoDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected ICrudService<PontoTrajeto, Long> getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    @Operation(summary = "Registra um ponto no trajeto")
    @PostMapping("/registrar")
    public ResponseEntity<PontoTrajetoDTO> registrar(@RequestBody @Valid PontoTrajetoCreateDTO dto) {
        return ResponseEntity.ok(service.registrarPonto(dto));
    }

    @Operation(
        summary = "Registra múltiplos pontos em lote",
        description = "Permite enviar vários pontos de uma vez. Continua processando mesmo se houver erros em alguns pontos. " +
                      "Ideal para sincronização de pontos coletados offline."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lote processado com sucesso (pode conter erros parciais)",
                     content = @Content(schema = @Schema(implementation = PontoTrajetoBatchResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida")
    })
    @PostMapping("/registrar-lote")
    public ResponseEntity<PontoTrajetoBatchResponseDTO> registrarLote(
            @RequestBody @Valid List<PontoTrajetoCreateDTO> pontos) {
        
        if (pontos == null || pontos.isEmpty()) {
            PontoTrajetoBatchResponseDTO response = new PontoTrajetoBatchResponseDTO();
            response.setMensagem("Lista de pontos vazia ou nula.");
            return ResponseEntity.badRequest().body(response);
        }

        PontoTrajetoBatchResponseDTO response = service.registrarPontosLote(pontos);
        
        // Retorna 200 mesmo com erros parciais (cliente decide como tratar)
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Registra múltiplos pontos em lote (transação atômica)",
        description = "Salva todos os pontos ou nenhum. Se houver erro em qualquer ponto, toda a operação é revertida (rollback). " +
                      "Use quando é crítico que todos os pontos sejam salvos juntos."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Todos os pontos foram salvos com sucesso",
                     content = @Content(schema = @Schema(implementation = PontoTrajetoBatchResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Erro no processamento - nenhum ponto foi salvo (rollback)")
    })
    @PostMapping("/registrar-lote-atomico")
    public ResponseEntity<PontoTrajetoBatchResponseDTO> registrarLoteAtomico(
            @RequestBody @Valid List<PontoTrajetoCreateDTO> pontos) {
        
        if (pontos == null || pontos.isEmpty()) {
            PontoTrajetoBatchResponseDTO response = new PontoTrajetoBatchResponseDTO();
            response.setMensagem("Lista de pontos vazia ou nula.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            PontoTrajetoBatchResponseDTO response = service.registrarPontosLoteAtomico(pontos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            PontoTrajetoBatchResponseDTO response = new PontoTrajetoBatchResponseDTO();
            response.setTotalRecebidos(pontos.size());
            response.setMensagem("Erro no processamento: " + e.getMessage() + ". Nenhum ponto foi salvo (rollback).");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}