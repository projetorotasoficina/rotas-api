package utfpr.edu.br.coleta.incidente;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter; // NOVO IMPORT
import io.swagger.v3.oas.annotations.media.Schema;
import utfpr.edu.br.coleta.generics.CrudController;
import utfpr.edu.br.coleta.incidente.dto.IncidenteDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/incidentes" )
public class IncidenteController extends CrudController<Incidente, IncidenteDTO> {

    private final IncidenteService service;
    private final ModelMapper modelMapper;

    public IncidenteController(
            IncidenteService service,
            ModelMapper modelMapper
    ){
        super(Incidente.class, IncidenteDTO.class);
        this.service = service;
        this.modelMapper = modelMapper;
    }

    @Override
    protected IncidenteService getService() {return service;}
    @Override
    protected ModelMapper getModelMapper() {return modelMapper;}

    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Cria um novo incidente com upload de foto para o MinIO",
            parameters = { // Usando @Parameter para descrever as partes
                    @Parameter(
                            name = "incidente",
                            description = "Dados do Incidente (IncidenteDTO) em formato JSON",
                            required = true,
                            schema = @Schema(type = "string", format = "json")
                    ),
                    @Parameter(
                            name = "foto",
                            description = "Arquivo da foto do incidente",
                            required = true,
                            schema = @Schema(type = "string", format = "binary")
                    )
            }
    )
    public ResponseEntity<IncidenteDTO> createIncidenteWithPhoto(
            @RequestPart("incidente") IncidenteDTO incidenteDTO,
            @RequestPart("foto") MultipartFile foto
    ) {
        Incidente incidente = service.saveWithPhoto(incidenteDTO, foto);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(incidente, IncidenteDTO.class));
    }
}
