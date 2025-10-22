package utfpr.edu.br.coleta.trajeto.pontotrajeto.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO de resposta para processamento em lote de pontos de trajeto.
 *
 * Retorna estatísticas e detalhes do processamento.
 *
 * Autor: Sistema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta do processamento em lote de pontos de trajeto")
public class PontoTrajetoBatchResponseDTO {

    @Schema(description = "Total de pontos recebidos", example = "10")
    private int totalRecebidos;

    @Schema(description = "Total de pontos salvos com sucesso", example = "9")
    private int totalSalvos;

    @Schema(description = "Total de pontos com erro", example = "1")
    private int totalErros;

    @Schema(description = "Lista de pontos salvos com sucesso")
    private List<PontoTrajetoDTO> pontosSalvos = new ArrayList<>();

    @Schema(description = "Lista de erros ocorridos durante o processamento")
    private List<ErroProcessamento> erros = new ArrayList<>();

    @Schema(description = "Mensagem geral do processamento")
    private String mensagem;

    /**
     * Classe interna para representar erros de processamento.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Detalhes de um erro de processamento")
    public static class ErroProcessamento {
        
        @Schema(description = "Índice do ponto no array original (base 0)", example = "5")
        private int indice;

        @Schema(description = "Dados do ponto que causou erro")
        private PontoTrajetoCreateDTO ponto;

        @Schema(description = "Mensagem de erro", example = "Trajeto não encontrado")
        private String mensagem;
    }

    /**
     * Adiciona um ponto salvo com sucesso.
     */
    public void adicionarPontoSalvo(PontoTrajetoDTO ponto) {
        this.pontosSalvos.add(ponto);
        this.totalSalvos++;
    }

    /**
     * Adiciona um erro de processamento.
     */
    public void adicionarErro(int indice, PontoTrajetoCreateDTO ponto, String mensagem) {
        this.erros.add(new ErroProcessamento(indice, ponto, mensagem));
        this.totalErros++;
    }

    /**
     * Verifica se houve algum erro no processamento.
     */
    public boolean temErros() {
        return totalErros > 0;
    }

    /**
     * Verifica se todos os pontos foram salvos com sucesso.
     */
    public boolean todosSalvos() {
        return totalRecebidos > 0 && totalSalvos == totalRecebidos && totalErros == 0;
    }
}

