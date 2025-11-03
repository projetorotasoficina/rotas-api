package utfpr.edu.br.coleta.rota.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO para resposta do endpoint de áreas não percorridas.
 *
 * Retorna as áreas da rota planejada que não foram cobertas pelos trajetos realizados,
 * incluindo estatísticas de cobertura e a geometria em formato GeoJSON.
 *
 * @author Sistema de Análise de Rotas
 * @since 2025-11-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AreasNaoPercorridasDTO {

    /**
     * ID da rota analisada
     */
    @JsonProperty("rota_id")
    private Long rotaId;

    /**
     * Nome da rota
     */
    @JsonProperty("rota_nome")
    private String rotaNome;

    /**
     * Geometria das áreas não cobertas em formato GeoJSON
     */
    @JsonProperty("areas_nao_cobertas")
    private Map<String, Object> areasNaoCobertas;

    /**
     * Estatísticas de cobertura
     */
    @JsonProperty("estatisticas")
    private EstatisticasCobertura estatisticas;

    /**
     * Buffer aplicado em metros ao redor do trajeto
     */
    @JsonProperty("buffer_metros")
    private Double bufferMetros;

    /**
     * Classe interna para estatísticas de cobertura
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstatisticasCobertura {

        /**
         * Área total planejada em metros quadrados
         */
        @JsonProperty("area_total_m2")
        private Double areaTotalM2;

        /**
         * Área coberta pelos trajetos em metros quadrados
         */
        @JsonProperty("area_coberta_m2")
        private Double areaCobertaM2;

        /**
         * Área não coberta em metros quadrados
         */
        @JsonProperty("area_nao_coberta_m2")
        private Double areaNaoCobertaM2;

        /**
         * Percentual de cobertura (0-100)
         */
        @JsonProperty("percentual_cobertura")
        private Double percentualCobertura;

        /**
         * Quantidade de trajetos analisados
         */
        @JsonProperty("quantidade_trajetos")
        private Integer quantidadeTrajetos;

        /**
         * Indica se a rota está completamente coberta
         */
        @JsonProperty("cobertura_completa")
        public Boolean isCoberturaCompleta() {
            return percentualCobertura != null && percentualCobertura >= 99.9;
        }

        /**
         * Retorna o status de cobertura em texto
         */
        @JsonProperty("status_cobertura")
        public String getStatusCobertura() {
            if (percentualCobertura == null) {
                return "INDETERMINADO";
            } else if (percentualCobertura >= 99.9) {
                return "COMPLETA";
            } else if (percentualCobertura >= 80.0) {
                return "BOA";
            } else if (percentualCobertura >= 50.0) {
                return "PARCIAL";
            } else {
                return "INSUFICIENTE";
            }
        }
    }
}
