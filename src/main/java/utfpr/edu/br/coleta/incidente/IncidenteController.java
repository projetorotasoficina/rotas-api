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
}