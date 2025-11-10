package utfpr.edu.br.coleta.rota;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.rota.dto.RotaDTO;
import utfpr.edu.br.coleta.tipocoleta.TipoColetaService;
import utfpr.edu.br.coleta.tiporesiduo.TipoResiduoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.rota.dto.AreasNaoPercorridasDTO;

/**
 * Controller responsável por expor os endpoints REST para operações de CRUD de Rota.
 *
 * Herda de CrudController, que já implementa endpoints genéricos
 * como GET, POST, PUT e DELETE.
 *
 * Autor: Pedro Henrique Sauthier
 */
@RestController
@RequestMapping("/rota")
@Slf4j
public class RotaController extends CrudController<Rota, RotaDTO> {

    private final RotaService service;
    private final ModelMapper modelMapper;
    private final RotaMapper rotaMapper;
    private final TipoResiduoService tipoResiduoService;
    private final TipoColetaService tipoColetaService;

    public RotaController(RotaService service, ModelMapper modelMapper, RotaMapper rotaMapper,
                          TipoResiduoService tipoResiduoService, TipoColetaService tipoColetaService) {
        super(Rota.class, RotaDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
        this.rotaMapper = rotaMapper;
        this.tipoResiduoService = tipoResiduoService;
        this.tipoColetaService = tipoColetaService;
    }

    @Override
    protected RotaService getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    @Override
    @PostMapping
    public ResponseEntity<RotaDTO> create(@RequestBody @Valid RotaDTO dto) {
        try {
            var tipoResiduo = tipoResiduoService.findOne(dto.getTipoResiduoId());
            var tipoColeta = tipoColetaService.findOne(dto.getTipoColetaId());
            
            Rota rota = rotaMapper.toEntity(dto, tipoResiduo, tipoColeta);
            Rota saved = service.save(rota);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(rotaMapper.toDTO(saved));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (IllegalArgumentException e) {
            // Erro de validação de polígono
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    @PutMapping("{id}")
    public ResponseEntity<RotaDTO> update(@PathVariable Long id, @RequestBody @Valid RotaDTO dto) {
        try {
            if (!id.equals(dto.getId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            Rota existingRota = service.findOne(id);

            var tipoResiduo = tipoResiduoService.findOne(dto.getTipoResiduoId());
            var tipoColeta = tipoColetaService.findOne(dto.getTipoColetaId());

            Rota rota = rotaMapper.toEntity(dto, tipoResiduo, tipoColeta, existingRota);
            Rota updated = service.save(rota);

            return ResponseEntity.ok(rotaMapper.toDTO(updated));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            // Erro de validação de polígono
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Override
    @GetMapping("{id}")
    public ResponseEntity<RotaDTO> findOne(@PathVariable Long id) {
        try {
            Rota rota = service.findOne(id);
            return ResponseEntity.ok(rotaMapper.toDTO(rota));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping("page")
    public ResponseEntity<org.springframework.data.domain.Page<RotaDTO>> findAll(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) Boolean asc,
            @RequestParam(required = false) String search) {

        org.springframework.data.domain.PageRequest pageRequest = org.springframework.data.domain.PageRequest.of(page, size);
        if (order != null && asc != null) {
            pageRequest = org.springframework.data.domain.PageRequest.of(page, size,
                asc ? org.springframework.data.domain.Sort.Direction.ASC : org.springframework.data.domain.Sort.Direction.DESC,
                order);
        }

        org.springframework.data.domain.Page<Rota> entities = service.findAll(pageRequest, search);
        org.springframework.data.domain.Page<RotaDTO> dtos = entities.map(rotaMapper::toDTO);

        return ResponseEntity.ok(dtos);
    }
    /**
     * Calcula e retorna as áreas da rota planejada que não foram percorridas pelos trajetos.
     *
     * Este endpoint compara a área geográfica planejada (Polygon) da rota com os trajetos
     * realizados (LineString), aplicando um buffer configurável ao redor dos trajetos para
     * considerar a largura de cobertura da coleta.
     *
     * @param id ID da rota a ser analisada
     * @param bufferMetros Raio do buffer em metros aplicado ao redor do trajeto (opcional, padrão: 20m)
     * @return DTO com áreas não cobertas em formato GeoJSON e estatísticas de cobertura
     */
    @Operation(
            summary = "Obter áreas não percorridas da rota",
            description = "Calcula as áreas da rota planejada que não foram cobertas pelos trajetos realizados. " +
                    "Retorna geometria em formato GeoJSON e estatísticas detalhadas de cobertura."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Áreas não percorridas calculadas com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AreasNaoPercorridasDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Rota não encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Rota não possui área geográfica definida",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno ao processar áreas não percorridas",
                    content = @Content
            )
    })
    @GetMapping("/{id}/nao-percorridas")
    public ResponseEntity<AreasNaoPercorridasDTO> obterAreasNaoPercorridas(
            @Parameter(description = "ID da rota", required = true)
            @PathVariable Long id,

            @Parameter(description = "Raio do buffer em metros aplicado ao redor do trajeto (padrão: 20m)")
            @RequestParam(required = false, defaultValue = "20.0") Double bufferMetros
    ) {
        log.info("GET /api/rotas/{}/nao-percorridas - buffer: {}m", id, bufferMetros);

        try {
            AreasNaoPercorridasDTO resultado = service.calcularAreasNaoPercorridas(id, bufferMetros);

            log.info("Áreas não percorridas calculadas com sucesso para rota ID: {} - Cobertura: {}%",
                    id, resultado.getEstatisticas().getPercentualCobertura());

            return ResponseEntity.ok(resultado);

        } catch (RotaService.RotaNaoEncontradaException e) {
            log.warn("Rota não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (RotaService.AreaPlanejadaNaoDefinidaException e) {
            log.warn("Área geográfica não definida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (RotaService.ErroProcessamentoGeoespacialException e) {
            log.error("Erro ao processar áreas não percorridas para rota ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        } catch (Exception e) {
            log.error("Erro inesperado ao processar áreas não percorridas para rota ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint alternativo que retorna apenas as estatísticas de cobertura, sem a geometria.
     * Útil para dashboards e relatórios que não precisam renderizar o mapa.
     *
     * @param id ID da rota
     * @param bufferMetros Raio do buffer em metros (opcional, padrão: 20m)
     * @return Estatísticas de cobertura
     */
    @Operation(
            summary = "Obter estatísticas de cobertura da rota",
            description = "Retorna apenas as estatísticas de cobertura da rota, sem a geometria das áreas não percorridas."
    )
    @GetMapping("/{id}/estatisticas-cobertura")
    public ResponseEntity<AreasNaoPercorridasDTO.EstatisticasCobertura> obterEstatisticasCobertura(
            @Parameter(description = "ID da rota", required = true)
            @PathVariable Long id,

            @Parameter(description = "Raio do buffer em metros (padrão: 20m)")
            @RequestParam(required = false, defaultValue = "20.0") Double bufferMetros
    ) {
        log.info("GET /api/rotas/{}/estatisticas-cobertura - buffer: {}m", id, bufferMetros);

        try {
            AreasNaoPercorridasDTO resultado = service.calcularAreasNaoPercorridas(id, bufferMetros);
            return ResponseEntity.ok(resultado.getEstatisticas());

        } catch (RotaService.RotaNaoEncontradaException e) {
            log.warn("Rota não encontrada: {}", e.getMessage());
            return ResponseEntity.notFound().build();

        } catch (RotaService.AreaPlanejadaNaoDefinidaException e) {
            log.warn("Área geográfica não definida: {}", e.getMessage());
            return ResponseEntity.badRequest().build();

        } catch (Exception e) {
            log.error("Erro ao obter estatísticas de cobertura para rota ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}