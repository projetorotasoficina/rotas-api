package utfpr.edu.br.coleta.incidente;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.incidente.dto.IncidenteDTO;
import utfpr.edu.br.coleta.storage.MinioService;
import org.modelmapper.ModelMapper;
import utfpr.edu.br.coleta.trajeto.Trajeto;
import utfpr.edu.br.coleta.trajeto.TrajetoRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IncidenteService extends CrudServiceImpl<Incidente, Long> {

    private final IncidenteRepository repository;
    private final MinioService minioService;
    private final ModelMapper modelMapper;
    private final TrajetoRepository trajetoRepository;

    public IncidenteService(IncidenteRepository repository,
                            MinioService minioService,
                            ModelMapper modelMapper,
                            TrajetoRepository trajetoRepository) {
        this.repository = repository;
        this.minioService = minioService;
        this.modelMapper = modelMapper;
        this.trajetoRepository = trajetoRepository;
    }

    @Override
    protected IncidenteRepository getRepository() { return repository; }

    public Incidente saveWithPhoto(IncidenteDTO dto, MultipartFile foto) {

        Trajeto trajeto = trajetoRepository.findById(dto.getTrajetoId())
                .orElseThrow(() -> new IllegalArgumentException("Trajeto não encontrado: " + dto.getTrajetoId()));

        String fotoUrl = minioService.uploadFile(foto, "incidentes");

        Incidente incidente = new Incidente();
        incidente.setTrajeto(trajeto);
        incidente.setNome(dto.getNome());
        incidente.setObservacoes(dto.getObservacoes());
        incidente.setTs(dto.getTs() != null ? dto.getTs() : LocalDateTime.now());
        incidente.setLatitude(dto.getLat());
        incidente.setLongitude(dto.getLng());
        incidente.setFotoUrl(fotoUrl);

        return repository.save(incidente);
    }

    public List<IncidenteDTO> findByTrajeto(Long trajetoId) {
        return repository.findByTrajetoId(trajetoId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    public IncidenteDTO convertToDTO(Incidente incidente) {
        IncidenteDTO dto = new IncidenteDTO();
        dto.setId(incidente.getId());
        dto.setTrajetoId(incidente.getTrajeto().getId());
        dto.setNome(incidente.getNome());
        dto.setObservacoes(incidente.getObservacoes());
        dto.setTs(incidente.getTs());
        dto.setLat(incidente.getLatitude());
        dto.setLng(incidente.getLongitude());
        dto.setFotoUrl(incidente.getFotoUrl());
        return dto;
    }
}