package utfpr.edu.br.coleta.rota;

import org.locationtech.jts.geom.Polygon;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import utfpr.edu.br.coleta.rota.dto.FrequenciaRotaDTO;
import utfpr.edu.br.coleta.rota.dto.PolygonGeoJsonDTO;
import utfpr.edu.br.coleta.rota.dto.RotaDTO;
import utfpr.edu.br.coleta.rota.util.GeoJsonConverter;
import utfpr.edu.br.coleta.tipocoleta.TipoColeta;
import utfpr.edu.br.coleta.tiporesiduo.TipoResiduo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe responsável por mapear entre Rota e RotaDTO,
 * incluindo conversão de geometrias GeoJSON.
 *
 * Autor: Sistema
 */
@Component
public class RotaMapper {

    private final ModelMapper modelMapper;

    public RotaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    /**
     * Converte RotaDTO para entidade Rota.
     */
    public Rota toEntity(RotaDTO dto, TipoResiduo tipoResiduo, TipoColeta tipoColeta) {
        Rota rota = new Rota();
        rota.setId(dto.getId());
        rota.setNome(dto.getNome());
        rota.setAtivo(dto.getAtivo());
        rota.setObservacoes(dto.getObservacoes());
        
        // Configurar relacionamentos
        rota.setTipoResiduo(tipoResiduo);
        rota.setTipoColeta(tipoColeta);
        
        // Converter frequências
        if (dto.getFrequencias() != null && !dto.getFrequencias().isEmpty()) {
            List<FrequenciaRota> frequencias = dto.getFrequencias().stream()
                .map(freqDTO -> new FrequenciaRota(rota, freqDTO.getDiaSemana(), freqDTO.getPeriodo()))
                .collect(Collectors.toList());
            rota.setFrequencias(frequencias);
        }
        
        // Converter GeoJSON para Polygon JTS
        if (dto.getAreaGeografica() != null) {
            Polygon polygon = GeoJsonConverter.toJtsPolygon(dto.getAreaGeografica());
            rota.setAreaGeografica(polygon);
        }
        
        return rota;
    }

    /**
     * Converte entidade Rota para RotaDTO.
     */
    public RotaDTO toDTO(Rota rota) {
        RotaDTO dto = new RotaDTO();
        dto.setId(rota.getId());
        dto.setNome(rota.getNome());
        dto.setAtivo(rota.getAtivo());
        dto.setObservacoes(rota.getObservacoes());
        
        // Configurar IDs dos relacionamentos
        if (rota.getTipoResiduo() != null) {
            dto.setTipoResiduoId(rota.getTipoResiduo().getId());
        }
        if (rota.getTipoColeta() != null) {
            dto.setTipoColetaId(rota.getTipoColeta().getId());
        }
        
        // Converter frequências
        if (rota.getFrequencias() != null && !rota.getFrequencias().isEmpty()) {
            List<FrequenciaRotaDTO> frequenciasDTO = rota.getFrequencias().stream()
                .map(freq -> new FrequenciaRotaDTO(freq.getDiaSemana(), freq.getPeriodo()))
                .collect(Collectors.toList());
            dto.setFrequencias(frequenciasDTO);
        }
        
        // Converter Polygon JTS para GeoJSON
        if (rota.getAreaGeografica() != null) {
            PolygonGeoJsonDTO geoJson = GeoJsonConverter.toGeoJson(rota.getAreaGeografica());
            dto.setAreaGeografica(geoJson);
        }
        
        return dto;
    }
}

