package utfpr.edu.br.coleta.incidente;

import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.incidente.dto.IncidenteDTO;
import org.springframework.web.multipart.MultipartFile;
import utfpr.edu.br.coleta.storage.MinioService;
import org.modelmapper.ModelMapper;
import java.util.List;

@Service
public class IncidenteService extends CrudServiceImpl<Incidente, Long> {

    private final IncidenteRepository repository;
    private final MinioService minioService;
    private final ModelMapper modelMapper;

    public IncidenteService(IncidenteRepository repository, MinioService minioService, ModelMapper modelMapper) {
        this.repository = repository;
        this.minioService = minioService;
        this.modelMapper = modelMapper;
    }

    @Override
    protected IncidenteRepository getRepository() {return repository;}

    public Incidente saveWithPhoto(IncidenteDTO dto, MultipartFile foto) {
        String fotoUrl = minioService.uploadFile(foto, "incidentes");
        Incidente incidente = modelMapper.map(dto, Incidente.class);
        incidente.setFotoUrl(fotoUrl);
        return repository.save(incidente);
    }

    public List<IncidenteDTO> findByTrajeto(Long trajetoId) {
        return repository.findByTrajetoId(trajetoId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private IncidenteDTO convertToDTO(Incidente incidente) {
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