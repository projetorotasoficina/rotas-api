package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import io.swagger.v3.oas.annotations.Operation;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoDTO;

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
    public ResponseEntity<PontoTrajetoDTO> registrar(@RequestBody PontoTrajetoCreateDTO dto) {
        return ResponseEntity.ok(service.registrarPonto(dto));
    }
}