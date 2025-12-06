package utfpr.edu.br.coleta.generics.report;

import utfpr.edu.br.coleta.generics.report.GenericReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

/**
 * Controller genérico para geração de relatórios personalizados de qualquer entidade.
 * URI: /api/v1/reports/generic/{entityName}
 * Retorna os dados da entidade em JSON, permitindo filtros dinâmicos via query string.
 */
@RestController
@RequestMapping("/api/v1/reports")
public class GenericReportController {

    private final GenericReportService genericReportService;

    public GenericReportController(GenericReportService genericReportService) {
        this.genericReportService = genericReportService;
    }

    /**
     * Endpoint genérico para buscar dados de qualquer entidade com filtros dinâmicos.
     *
     * @param entityName O nome da entidade (e.g., "routes", "incidents", "trucks").
     * @param filters Parâmetros de filtro passados via query string (e.g., ?status=COMPLETED&driverId=1).
     * @return ResponseEntity contendo uma lista de mapas (JSON) com os dados filtrados.
     */
    @GetMapping("/generic/{entityName}")
    public ResponseEntity<List<Map<String, Object>>> getGenericReport(
            @PathVariable String entityName,
            @RequestParam Map<String, String> filters) {

        try {
            List<Map<String, Object>> reportData = genericReportService.getReportData(entityName, filters);

            if (reportData.isEmpty()) {
                // Retorna 204 No Content se não houver dados
                return ResponseEntity.noContent().build();
            }

            // Retorna 200 OK com os dados
            return ResponseEntity.ok(reportData);

        } catch (IllegalArgumentException e) {
            // Captura exceções do serviço para entidades não suportadas
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Captura outras exceções internas
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar o relatório genérico: " + e.getMessage());
        }
    }
}