package utfpr.edu.br.coleta.incidente;

import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.incidente.dto.IncidenteDTO;
import java.util.List;

@Service
public class IncidenteService extends CrudServiceImpl<Incidente, Long> {

    private final IncidenteRepository repository;
    
    public IncidenteService(IncidenteRepository repository) {this.repository = repository;}

    @Override
    protected IncidenteRepository getRepository() {return repository;}

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
