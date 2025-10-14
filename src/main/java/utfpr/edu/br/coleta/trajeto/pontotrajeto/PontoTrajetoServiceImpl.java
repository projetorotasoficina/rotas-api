package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.trajeto.TrajetoRepository;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PontoTrajetoServiceImpl extends CrudServiceImpl<PontoTrajeto, Long> implements IPontoTrajetoService {

    private final PontoTrajetoRepository repository;
    private final TrajetoRepository trajetoRepository;
    private final ModelMapper mapper;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    protected PontoTrajetoRepository getRepository() {
        return repository;
    }

    protected ModelMapper getModelMapper() {
        return mapper;
    }

    @Override
    public PontoTrajetoDTO registrarPonto(PontoTrajetoCreateDTO dto) {
        Point point = geometryFactory.createPoint(new org.locationtech.jts.geom.Coordinate(dto.getLongitude(), dto.getLatitude()));

        PontoTrajeto ponto = new PontoTrajeto();
        ponto.setTrajeto(trajetoRepository.findById(dto.getTrajetoId())
                .orElseThrow(() -> new RuntimeException("Trajeto não encontrado")));
        ponto.setLocalizacao(point);
        ponto.setHorario(dto.getHorario());
        ponto.setObservacao(dto.getObservacao());

        return mapper.map(repository.save(ponto), PontoTrajetoDTO.class);
    }

    @Override
    public List<PontoTrajetoDTO> findByTrajeto(Long trajetoId) {
        return repository.findByTrajetoId(trajetoId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private PontoTrajetoDTO convertToDTO(PontoTrajeto ponto) {
        PontoTrajetoDTO dto = new PontoTrajetoDTO();
        dto.setId(ponto.getId());
        dto.setTrajetoId(ponto.getTrajeto().getId());
        dto.setLatitude(ponto.getLocalizacao().getY());
        dto.setLongitude(ponto.getLocalizacao().getX());
        dto.setHorario(ponto.getHorario());
        dto.setObservacao(ponto.getObservacao());
        return dto;
    }
}