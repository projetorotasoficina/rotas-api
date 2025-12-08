package utfpr.edu.br.coleta.incidente;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.incidente.dto.IncidenteDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/incidentes")
public class IncidenteController extends CrudController<Incidente, IncidenteDTO> {

    private final IncidenteService service;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;

    public IncidenteController(
            IncidenteService service,
            ModelMapper modelMapper,
            ObjectMapper objectMapper
    ) {
        super(Incidente.class, IncidenteDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    protected ICrudService<Incidente, Long> getService() {
        return service;
    }

    @Override
    protected ModelMapper getModelMapper() {
        return modelMapper;
    }

    // --- ENDPOINT ESPECIAL: multipart com foto ---

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Cria um incidente com upload de foto no MinIO",
            parameters = {
                    @Parameter(
                            name = "incidente",
                            description = "Corpo JSON com os dados do incidente",
                            required = true,
                            schema = @Schema(type = "string", format = "json")
                    ),
                    @Parameter(
                            name = "foto",
                            description = "Arquivo de imagem",
                            required = true,
                            schema = @Schema(type = "string", format = "binary")
                    )
            }
    )
    public ResponseEntity<IncidenteDTO> createWithPhoto(
            @RequestPart("incidente") String incidenteJson,
            @RequestPart("foto") MultipartFile foto
    ) throws Exception {

        IncidenteDTO dto = objectMapper.readValue(incidenteJson, IncidenteDTO.class);

        Incidente incidente = service.saveWithPhoto(dto, foto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(service.convertToDTO(incidente));
    }

    @GetMapping("/relatorio")
    @Operation(summary = "Busca incidentes filtrados para relatório")
    public ResponseEntity<List<IncidenteDTO>> buscarParaRelatorio(
            @Parameter(description = "Data de início no formato ISO (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) String dataInicio,
            @Parameter(description = "Data de fim no formato ISO (yyyy-MM-dd'T'HH:mm:ss)")
            @RequestParam(required = false) String dataFim,
            @Parameter(description = "ID da rota para filtrar")
            @RequestParam(required = false) Long rotaId
    ) {
        LocalDateTime inicio = null;
        LocalDateTime fim = null;

        if (dataInicio != null && !dataInicio.isEmpty()) {
            inicio = LocalDateTime.parse(dataInicio, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        if (dataFim != null && !dataFim.isEmpty()) {
            fim = LocalDateTime.parse(dataFim, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }

        List<IncidenteDTO> incidentes = service.buscarParaRelatorio(inicio, fim, rotaId);
        return ResponseEntity.ok(incidentes);
    }
}