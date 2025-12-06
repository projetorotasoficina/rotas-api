package utfpr.edu.br.coleta.generics.report;

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

// Imports para Swagger/OpenAPI (SpringDoc)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controller genérico para geração de relatórios personalizados de qualquer entidade.
 * URI: /api/v1/reports/generic/{entityName}
 * Retorna os dados da entidade em JSON, permitindo filtros dinâmicos via query string.
 */
@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Relatórios Genéricos", description = "Endpoints para geração de relatórios personalizados de qualquer entidade do sistema.")
public class GenericReportController {

    private final GenericReportService genericReportService;

    public GenericReportController(GenericReportService genericReportService) {
        this.genericReportService = genericReportService;
    }

    @Operation(
            summary = "Gera um relatório personalizado de uma entidade.",
            description = "Busca dados de uma entidade específica (ex: caminhao, rota) aplicando filtros dinâmicos via query parameters. Suporta filtros em campos diretos e em relacionamentos (ex: tipoResiduo.nome=Reciclavel).",
            parameters = {
                    @Parameter(
                            name = "entityName",
                            description = "Nome da entidade para o relatório (ex: caminhao, rota, incidente). Deve ser mapeado no GenericReportService.",
                            required = true,
                            example = "caminhao"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Relatório gerado com sucesso.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = List.class),
                                    examples = @ExampleObject(
                                            name = "Exemplo de Retorno",
                                            value = "[{\"id\": 1, \"placa\": \"ABC1234\", \"modelo\": \"Volvo\", \"tipoResiduo\": {\"id\": 1, \"nome\": \"Reciclavel\"}}, {\"id\": 2, \"placa\": \"XYZ5678\", \"modelo\": \"Scania\", \"tipoResiduo\": {\"id\": 2, \"nome\": \"Organico\"}}]"
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "204",
                            description = "Nenhum dado encontrado para os filtros aplicados."
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Entidade não suportada (verifique o mapeamento no GenericReportService)."
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Erro interno do servidor."
                    )
            }
    )
    @GetMapping("/generic/{entityName}")
    public ResponseEntity<List<Map<String, Object>>> getGenericReport(
            @PathVariable String entityName,
            @Parameter(
                    description = "Filtros dinâmicos. Use o nome do campo da entidade (ex: status=ATIVO) ou notação de ponto para relacionamentos (ex: tipoResiduo.nome=Reciclavel).",
                    example = "status=ATIVO&tipoResiduo.nome=Reciclavel"
            )
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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar o relatório genérico: " + e.getMessage());
        }
    }
}