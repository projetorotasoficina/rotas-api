package utfpr.edu.br.coleta.rota.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * DTO que representa um polígono no formato GeoJSON.
 * 
 * Exemplo de estrutura GeoJSON:
 * {
 *   "type": "Polygon",
 *   "coordinates": [
 *     [
 *       [-49.123, -26.456],
 *       [-49.124, -26.457],
 *       [-49.125, -26.456],
 *       [-49.123, -26.456]
 *     ]
 *   ]
 * }
 *
 * Autor: Luiz Alberto dos Passos
 */
@Data
@Schema(description = "Polígono no formato GeoJSON")
public class PolygonGeoJsonDTO {

    @Schema(description = "Tipo de geometria (sempre 'Polygon')", 
            example = "Polygon", 
            allowableValues = {"Polygon"})
    @JsonProperty("type")
    private String type = "Polygon";

    @Schema(description = "Array de anéis de coordenadas. O primeiro anel é o exterior, anéis subsequentes são buracos. Cada anel deve ser fechado (primeiro e último ponto iguais).",
            example = "[[[-49.123, -26.456], [-49.124, -26.457], [-49.125, -26.456], [-49.123, -26.456]]]")
    @JsonProperty("coordinates")
    private List<List<List<Double>>> coordinates;
}

