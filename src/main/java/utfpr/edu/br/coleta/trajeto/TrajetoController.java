package utfpr.edu.br.coleta.trajeto;

import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.incidente.IncidenteService;
import utfpr.edu.br.coleta.incidente.dto.IncidenteDTO;
import utfpr.edu.br.coleta.tiporesiduo.TipoResiduoDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.IPontoTrajetoService;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoDTO;
import utfpr.edu.br.coleta.trajeto.dto.TrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.dto.TrajetoDTO;

import java.util.List;

@RestController
@RequestMapping("/trajetos")
public class TrajetoController extends CrudController<Trajeto, TrajetoDTO> {

    private final ITrajetoService service;
    private final IPontoTrajetoService pontoTrajetoService;
    private final IncidenteService incidenteService;
    private final ModelMapper modelMapper;

    public TrajetoController(
            ITrajetoService service,
            IPontoTrajetoService pontoTrajetoService,
            IncidenteService incidenteService,
            ModelMapper modelMapper
    ) {
        super(Trajeto.class, TrajetoDTO.class);
        this.service = service;
        this.pontoTrajetoService = pontoTrajetoService;
        this.incidenteService = incidenteService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected ICrudService<Trajeto, Long> getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    @Override
    protected TrajetoDTO convertToDto(Trajeto trajeto) {
        TrajetoDTO dto = modelMapper.map(trajeto, TrajetoDTO.class);

        if (trajeto.getRota() != null && trajeto.getRota().getTipoResiduo() != null) {
            dto.setTipoResiduo(modelMapper.map(trajeto.getRota().getTipoResiduo(), TipoResiduoDTO.class));
        }

        return dto;
    }

    @Operation(summary = "Inicia um novo trajeto")
    @PostMapping("/iniciar")
    public ResponseEntity<TrajetoDTO> iniciar(@RequestBody TrajetoCreateDTO dto) {
        return ResponseEntity.ok(service.iniciarTrajeto(dto));
    }

    @Operation(summary = "Finaliza um trajeto")
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<TrajetoDTO> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(service.finalizarTrajeto(id));
    }

    @Operation(summary = "Cancela um trajeto")
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<TrajetoDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelarTrajeto(id));
    }

    @Operation(summary = "Lista todos os pontos registrados de um trajeto")
    @GetMapping("/{id}/pontos")
    public ResponseEntity<List<PontoTrajetoDTO>> listarPontos(@PathVariable Long id) {
        return ResponseEntity.ok(pontoTrajetoService.findByTrajeto(id));
    }

    @Operation(summary = "Lista todos os incidentes registrados de um trajeto")
    @GetMapping("/{id}/incidentes")
    public ResponseEntity<List<IncidenteDTO>> listarIncidentes(@PathVariable Long id) {
        return ResponseEntity.ok(incidenteService.findByTrajeto(id));
    }
}