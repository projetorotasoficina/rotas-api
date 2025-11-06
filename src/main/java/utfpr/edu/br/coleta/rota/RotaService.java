package utfpr.edu.br.coleta.rota;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import utfpr.edu.br.coleta.rota.dto.AreasNaoPercorridasDTO;
import utfpr.edu.br.coleta.rota.dto.AreasNaoPercorridasDTO.EstatisticasCobertura;
import java.util.Map;


/**
 * Serviço responsável pelas regras de negócio da entidade Rota.
 *
 * Estende CrudServiceImpl e implementa o método getRepository()
 * para fornecer o RotaRepository ao CRUD genérico.
 *
 * Autor: Pedro Henrique Sauthier
 */
@Service
@Slf4j
public class RotaService extends CrudServiceImpl<Rota, Long> {

    private final RotaRepository repository;
    private final ObjectMapper objectMapper;
    public RotaService(RotaRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    private static final Double BUFFER_PADRAO_METROS = 20.0;

    @Override
    protected RotaRepository getRepository() {
        return repository;
    }

    public Optional<Rota> findById(long id) {
        return repository.findById(id);
    }

    @Override
    public Page<Rota> findAll(Pageable pageable, String search) {
        if (search == null || search.trim().isEmpty()) {
            return findAll(pageable);
        }
        return repository.findByNomeContainingIgnoreCase(search, pageable);
    }
    /**
     * Calcula as áreas da rota planejada que não foram percorridas pelos trajetos.
     *
     * Este método compara a área planejada (Polygon) da rota com os trajetos
     * realizados (LineString), aplicando um buffer ao redor dos trajetos para
     * considerar a largura de cobertura da coleta.
     *
     * @param rotaId ID da rota a ser analisada
     * @param bufferMetros Raio do buffer em metros (opcional, padrão: 20m)
     * @return DTO com áreas não cobertas e estatísticas
     * @throws RotaNaoEncontradaException se a rota não existir
     * @throws AreaPlanejadaNaoDefinidaException se a rota não tiver área planejada
     * @throws ErroProcessamentoGeoespacialException em caso de erro no processamento
     */
    @Transactional(readOnly = true)
    public AreasNaoPercorridasDTO calcularAreasNaoPercorridas(Long rotaId, Double bufferMetros) {
        log.info("Calculando áreas não percorridas para rota ID: {} com buffer: {}m", rotaId, bufferMetros);

        // Validar buffer
        Double bufferFinal = (bufferMetros != null && bufferMetros > 0) ? bufferMetros : BUFFER_PADRAO_METROS;

        // Verificar se a rota existe
        Rota rota = repository.findById(rotaId)
                .orElseThrow(() -> new RotaNaoEncontradaException("Rota com ID " + rotaId + " não encontrada"));

        // Verificar se a rota possui área geográfica
        Boolean possuiAreaGeografica = repository.possuiAreaGeografica(rotaId)
                .orElse(false);

        if (!possuiAreaGeografica) {
            throw new AreaPlanejadaNaoDefinidaException(
                    "Rota ID " + rotaId + " não possui área geográfica definida. " +
                            "É necessário definir a área geográfica antes de calcular áreas não percorridas."
            );
        }

        try {
            // Obter áreas não cobertas em GeoJSON
            String areasNaoCobertas = repository.calcularAreasNaoPercorridas(rotaId, bufferFinal)
                    .orElse(null);

            // Obter estatísticas de cobertura
            String estatisticasJson = repository.obterEstatisticasCobertura(rotaId, bufferFinal)
                    .orElseThrow(() -> new ErroProcessamentoGeoespacialException(
                            "Erro ao obter estatísticas de cobertura para rota ID " + rotaId
                    ));

            // Converter JSON para objetos
            Map<String, Object> areasGeoJSON = null;
            if (areasNaoCobertas != null && !areasNaoCobertas.equals("null")) {
                areasGeoJSON = objectMapper.readValue(
                        areasNaoCobertas,
                        new TypeReference<Map<String, Object>>() {}
                );
            }

            Map<String, Object> estatisticasMap = objectMapper.readValue(
                    estatisticasJson,
                    new TypeReference<Map<String, Object>>() {}
            );

            // Construir DTO de resposta
            EstatisticasCobertura estatisticas = EstatisticasCobertura.builder()
                    .areaTotalM2(getDoubleValue(estatisticasMap, "area_total_m2"))
                    .areaCobertaM2(getDoubleValue(estatisticasMap, "area_coberta_m2"))
                    .areaNaoCobertaM2(getDoubleValue(estatisticasMap, "area_nao_coberta_m2"))
                    .percentualCobertura(getDoubleValue(estatisticasMap, "percentual_cobertura"))
                    .quantidadeTrajetos(getIntegerValue(estatisticasMap, "quantidade_trajetos"))
                    .build();

            AreasNaoPercorridasDTO resultado = AreasNaoPercorridasDTO.builder()
                    .rotaId(rotaId)
                    .rotaNome(rota.getNome())
                    .areasNaoCobertas(areasGeoJSON)
                    .estatisticas(estatisticas)
                    .bufferMetros(bufferFinal)
                    .build();

            log.info("Áreas não percorridas calculadas com sucesso. Cobertura: {}%",
                    estatisticas.getPercentualCobertura());

            return resultado;

        } catch (Exception e) {
            log.error("Erro ao processar áreas não percorridas para rota ID: {}", rotaId, e);
            throw new ErroProcessamentoGeoespacialException(
                    "Erro ao processar áreas não percorridas: " + e.getMessage(), e
            );
        }
    }

    /**
     * Sobrecarga do método com buffer padrão.
     */
    @Transactional(readOnly = true)
    public AreasNaoPercorridasDTO calcularAreasNaoPercorridas(Long rotaId) {
        return calcularAreasNaoPercorridas(rotaId, BUFFER_PADRAO_METROS);
    }

// =====================================================================
// MÉTODOS AUXILIARES
// =====================================================================

    private Double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

// =====================================================================
//  EXCEÇÕES CUSTOMIZADAS
// =====================================================================

    public static class RotaNaoEncontradaException extends RuntimeException {
        public RotaNaoEncontradaException(String message) {
            super(message);
        }
    }

    public static class AreaPlanejadaNaoDefinidaException extends RuntimeException {
        public AreaPlanejadaNaoDefinidaException(String message) {
            super(message);
        }
    }

    public static class ErroProcessamentoGeoespacialException extends RuntimeException {
        public ErroProcessamentoGeoespacialException(String message) {
            super(message);
        }

        public ErroProcessamentoGeoespacialException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}