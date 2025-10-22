package utfpr.edu.br.coleta.rota.util;

import org.locationtech.jts.geom.*;
import utfpr.edu.br.coleta.rota.dto.PolygonGeoJsonDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para conversão entre objetos JTS Geometry e GeoJSON.
 *
 * Autor: Luiz Alberto dos Passos
 */
public class GeoJsonConverter {

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    /**
     * Converte um PolygonGeoJsonDTO para um objeto JTS Polygon.
     *
     * @param geoJson objeto GeoJSON representando um polígono
     * @return objeto JTS Polygon
     * @throws IllegalArgumentException se o GeoJSON for inválido
     */
    public static Polygon toJtsPolygon(PolygonGeoJsonDTO geoJson) {
        if (geoJson == null || geoJson.getCoordinates() == null || geoJson.getCoordinates().isEmpty()) {
            return null;
        }

        validateGeoJsonType(geoJson);

        List<List<List<Double>>> coordinates = geoJson.getCoordinates();

        // Primeiro anel é o exterior
        LinearRing shell = createLinearRing(coordinates.get(0));

        // Anéis subsequentes são buracos (holes)
        LinearRing[] holes = null;
        if (coordinates.size() > 1) {
            holes = new LinearRing[coordinates.size() - 1];
            for (int i = 1; i < coordinates.size(); i++) {
                holes[i - 1] = createLinearRing(coordinates.get(i));
            }
        }

        Polygon polygon = GEOMETRY_FACTORY.createPolygon(shell, holes);
        
        validatePolygon(polygon);
        
        return polygon;
    }

    /**
     * Converte um objeto JTS Polygon para PolygonGeoJsonDTO.
     *
     * @param polygon objeto JTS Polygon
     * @return objeto GeoJSON representando o polígono
     */
    public static PolygonGeoJsonDTO toGeoJson(Polygon polygon) {
        if (polygon == null) {
            return null;
        }

        PolygonGeoJsonDTO geoJson = new PolygonGeoJsonDTO();
        geoJson.setType("Polygon");

        List<List<List<Double>>> coordinates = new ArrayList<>();

        // Anel exterior
        coordinates.add(extractCoordinates(polygon.getExteriorRing()));

        // Anéis interiores (buracos)
        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            coordinates.add(extractCoordinates(polygon.getInteriorRingN(i)));
        }

        geoJson.setCoordinates(coordinates);
        return geoJson;
    }

    /**
     * Cria um LinearRing a partir de uma lista de coordenadas.
     */
    private static LinearRing createLinearRing(List<List<Double>> ringCoordinates) {
        if (ringCoordinates == null || ringCoordinates.size() < 4) {
            throw new IllegalArgumentException("Um anel de polígono deve ter pelo menos 4 pontos (incluindo o fechamento)");
        }

        Coordinate[] coordinates = new Coordinate[ringCoordinates.size()];
        for (int i = 0; i < ringCoordinates.size(); i++) {
            List<Double> point = ringCoordinates.get(i);
            if (point.size() < 2) {
                throw new IllegalArgumentException("Cada ponto deve ter pelo menos longitude e latitude");
            }
            // GeoJSON usa [longitude, latitude]
            coordinates[i] = new Coordinate(point.get(0), point.get(1));
        }

        // Verificar se o anel está fechado
        if (!coordinates[0].equals2D(coordinates[coordinates.length - 1])) {
            throw new IllegalArgumentException("O anel do polígono deve ser fechado (primeiro e último ponto devem ser iguais)");
        }

        return GEOMETRY_FACTORY.createLinearRing(coordinates);
    }

    /**
     * Extrai coordenadas de um LineString para formato GeoJSON.
     */
    private static List<List<Double>> extractCoordinates(LineString lineString) {
        List<List<Double>> coordinates = new ArrayList<>();
        for (Coordinate coord : lineString.getCoordinates()) {
            List<Double> point = new ArrayList<>();
            point.add(coord.x); // longitude
            point.add(coord.y); // latitude
            coordinates.add(point);
        }
        return coordinates;
    }

    /**
     * Valida o tipo do GeoJSON.
     */
    private static void validateGeoJsonType(PolygonGeoJsonDTO geoJson) {
        if (!"Polygon".equals(geoJson.getType())) {
            throw new IllegalArgumentException("Tipo de geometria deve ser 'Polygon', recebido: " + geoJson.getType());
        }
    }

    /**
     * Valida se o polígono é válido segundo as regras do JTS.
     */
    private static void validatePolygon(Polygon polygon) {
        if (!polygon.isValid()) {
            throw new IllegalArgumentException("O polígono não é válido. Verifique se os anéis estão fechados e não se auto-intersectam.");
        }

        if (polygon.isEmpty()) {
            throw new IllegalArgumentException("O polígono não pode estar vazio");
        }
    }
}

