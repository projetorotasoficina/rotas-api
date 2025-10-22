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

            var tipoResiduo = tipoResiduoService.findOne(dto.getTipoResiduoId());
            var tipoColeta = tipoColetaService.findOne(dto.getTipoColetaId());
            
            Rota rota = rotaMapper.toEntity(dto, tipoResiduo, tipoColeta);
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
}