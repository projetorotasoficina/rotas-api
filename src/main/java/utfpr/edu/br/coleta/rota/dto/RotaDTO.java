package utfpr.edu.br.coleta.rota.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * DTO (Data Transfer Object) utilizado para transferir
 * informações de Rota entre camadas do sistema.
 *
 * Autor: Pedro Henrique Sauthiier
 */
@Data
public class RotaDTO {

    private Long id;

    /** Nome do tipo de coleta. */
    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotNull(message = "O campo ativo é obrigatório.")
    private Boolean ativo;

    @Size(max = 400, message = "Observações podem ter no máximo 400 caracteres.")
    private String observacoes;

    @NotNull(message = "O tipo de resíduo é obrigatório.")
    private Long tipoResiduoId;

    @NotNull(message = "O tipo de coleta é obrigatório.")
    private Long tipoColetaId;

    /** Frequências da rota: lista de dias da semana com seus respectivos períodos. */
    @Schema(description = "Frequências da rota: lista de dias da semana com seus respectivos períodos",
            example = "[{\"diaSemana\": \"SEGUNDA\", \"periodo\": \"MANHA\"}, {\"diaSemana\": \"QUARTA\", \"periodo\": \"TARDE\"}]")
    private List<FrequenciaRotaDTO> frequencias;

    /** Área geográfica (polígono) no formato GeoJSON. */
    @Schema(description = "Área geográfica (polígono) que representa a região de cobertura da rota, no formato GeoJSON")
    private PolygonGeoJsonDTO areaGeografica;

}