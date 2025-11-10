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
        return toEntity(dto, tipoResiduo, tipoColeta, null);
    }

    /**
     * Converte RotaDTO para entidade Rota, atualizando uma entidade existente se fornecida.
     */
    public Rota toEntity(RotaDTO dto, TipoResiduo tipoResiduo, TipoColeta tipoColeta, Rota existingRota) {
        Rota rota = existingRota != null ? existingRota : new Rota();
        rota.setId(dto.getId());
        rota.setNome(dto.getNome());
        rota.setAtivo(dto.getAtivo());
        rota.setObservacoes(dto.getObservacoes());

        // Configurar relacionamentos
        rota.setTipoResiduo(tipoResiduo);
        rota.setTipoColeta(tipoColeta);

        // Garantir que a lista de frequências existe
        if (rota.getFrequencias() == null) {
            rota.setFrequencias(new java.util.ArrayList<>());
        }

        // Atualizar frequências de forma inteligente
        if (dto.getFrequencias() != null && !dto.getFrequencias().isEmpty()) {
            // Criar um mapa das frequências do DTO por dia da semana
            java.util.Map<utfpr.edu.br.coleta.rota.enums.DiaSemana, utfpr.edu.br.coleta.rota.enums.Periodo> dtoFrequenciasMap =
                dto.getFrequencias().stream()
                    .collect(Collectors.toMap(
                        FrequenciaRotaDTO::getDiaSemana,
                        FrequenciaRotaDTO::getPeriodo
                    ));

            // Remover frequências que não existem mais no DTO
            rota.getFrequencias().removeIf(freq -> !dtoFrequenciasMap.containsKey(freq.getDiaSemana()));

            // Atualizar frequências existentes ou adicionar novas
            for (FrequenciaRotaDTO freqDTO : dto.getFrequencias()) {
                FrequenciaRota existingFreq = rota.getFrequencias().stream()
                    .filter(f -> f.getDiaSemana().equals(freqDTO.getDiaSemana()))
                    .findFirst()
                    .orElse(null);

                if (existingFreq != null) {
                    // Atualizar período da frequência existente
                    existingFreq.setPeriodo(freqDTO.getPeriodo());
                } else {
                    // Adicionar nova frequência
                    rota.getFrequencias().add(new FrequenciaRota(rota, freqDTO.getDiaSemana(), freqDTO.getPeriodo()));
                }
            }
        } else {
            // Se não houver frequências no DTO, limpar as existentes
            rota.getFrequencias().clear();
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

