package utfpr.edu.br.coleta.trajeto.consulta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.trajeto.consulta.dto.AgendaColetaDTO;
import utfpr.edu.br.coleta.trajeto.consulta.dto.HistoricoColetaDTO;
import utfpr.edu.br.coleta.usuario.Usuario;

import java.util.List;

/**
 * Controller REST para consultas de agenda e histórico de coleta.
 * 
 * Não estende CrudController pois não realiza operações CRUD, apenas consultas.
 * Por isso, o @RequestMapping precisa incluir "/api/" manualmente.
 * 
 * Autor: Sistema Rotas API
 */
@RestController
@RequestMapping("/api/consulta")
@RequiredArgsConstructor
@Tag(name = "Consulta de Coleta", description = "Endpoints para consulta de agenda e histórico de coleta")
public class ConsultaColetaController {

    private final ConsultaColetaService consultaColetaService;

    @GetMapping("/agenda-coleta")
    @PreAuthorize("hasAuthority('ROLE_MORADOR')")
    @Operation(summary = "Consultar agenda de coleta", 
               description = "Retorna o cronograma de coleta para o endereço do morador autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agenda retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas moradores"),
        @ApiResponse(responseCode = "400", description = "Endereço do morador não possui coordenadas")
    })
    public ResponseEntity<List<AgendaColetaDTO>> consultarAgendaColeta(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Verifica se o usuário possui coordenadas cadastradas
        if (usuario.getLatitude() == null || usuario.getLongitude() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<AgendaColetaDTO> agenda = consultaColetaService.buscarAgendaColeta(
                usuario.getLatitude(), 
                usuario.getLongitude()
        );

        return ResponseEntity.ok(agenda);
    }

    @GetMapping("/historico-coleta")
    @PreAuthorize("hasAuthority('ROLE_MORADOR')")
    @Operation(summary = "Consultar histórico de coleta", 
               description = "Retorna o histórico de passagens do caminhão pelo endereço do morador autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Não autenticado"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - apenas moradores"),
        @ApiResponse(responseCode = "400", description = "Endereço do morador não possui coordenadas")
    })
    public ResponseEntity<List<HistoricoColetaDTO>> consultarHistoricoColeta(Authentication authentication) {
        Usuario usuario = (Usuario) authentication.getPrincipal();

        // Verifica se o usuário possui coordenadas cadastradas
        if (usuario.getLatitude() == null || usuario.getLongitude() == null) {
            return ResponseEntity.badRequest().build();
        }

        List<HistoricoColetaDTO> historico = consultaColetaService.buscarHistoricoColeta(
                usuario.getLatitude(), 
                usuario.getLongitude()
        );

        return ResponseEntity.ok(historico);
    }

    @GetMapping("/agenda-coleta/coordenadas")
    @Operation(summary = "Consultar agenda de coleta por coordenadas", 
               description = "Retorna o cronograma de coleta para coordenadas específicas (endpoint público para consulta)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agenda retornada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    public ResponseEntity<List<AgendaColetaDTO>> consultarAgendaColetaPorCoordenadas(
            @Parameter(description = "Latitude do endereço", example = "-26.2289")
            @RequestParam Double latitude,
            @Parameter(description = "Longitude do endereço", example = "-52.6789")
            @RequestParam Double longitude) {

        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().build();
        }

        List<AgendaColetaDTO> agenda = consultaColetaService.buscarAgendaColeta(latitude, longitude);
        return ResponseEntity.ok(agenda);
    }

    @GetMapping("/historico-coleta/coordenadas")
    @Operation(summary = "Consultar histórico de coleta por coordenadas", 
               description = "Retorna o histórico de coletas para coordenadas específicas (endpoint público para consulta)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
    })
    public ResponseEntity<List<HistoricoColetaDTO>> consultarHistoricoColetaPorCoordenadas(
            @Parameter(description = "Latitude do endereço", example = "-26.2289")
            @RequestParam Double latitude,
            @Parameter(description = "Longitude do endereço", example = "-52.6789")
            @RequestParam Double longitude) {

        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().build();
        }

        List<HistoricoColetaDTO> historico = consultaColetaService.buscarHistoricoColeta(latitude, longitude);
        return ResponseEntity.ok(historico);
    }
}
