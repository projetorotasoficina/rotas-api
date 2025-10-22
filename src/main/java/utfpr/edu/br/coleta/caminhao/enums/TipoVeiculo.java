package utfpr.edu.br.coleta.caminhao.enums;

import utfpr.edu.br.coleta.motorista.enums.CategoriaCNH;

import java.util.Arrays;
import java.util.List;

/**
 * Enum que representa os tipos de veículos (caminhões) utilizados na coleta de resíduos.
 *
 * Cada tipo de veículo exige uma categoria mínima de CNH para ser conduzido.
 *
 * Autor: Sistema
 */
public enum TipoVeiculo {
    /**
     * Veículo Utilitário Leve (VUC): Até 3.500 kg de PBT.
     * Exemplos: Fiorino, Kangoo, Partner.
     * CNH exigida: B
     */
    VUC("Veículo Utilitário de Carga", 3500, CategoriaCNH.B, CategoriaCNH.AB),

    /**
     * Caminhão Leve: De 3.501 kg até 6.000 kg de PBT.
     * Exemplos: HR, Daily, Accelo 815.
     * CNH exigida: C
     */
    CAMINHAO_LEVE("Caminhão Leve", 6000, CategoriaCNH.C, CategoriaCNH.AC),

    /**
     * Caminhão Médio: De 6.001 kg até 16.000 kg de PBT.
     * Exemplos: Cargo 1319, Atego 1719, VM 260.
     * CNH exigida: C
     */
    CAMINHAO_MEDIO("Caminhão Médio", 16000, CategoriaCNH.C, CategoriaCNH.AC),

    /**
     * Caminhão Pesado: De 16.001 kg até 40.000 kg de PBT (caminhão truck).
     * Exemplos: FH 440, Axor 2544, Constellation 24-250.
     * CNH exigida: C
     */
    CAMINHAO_PESADO("Caminhão Pesado (Truck)", 40000, CategoriaCNH.C, CategoriaCNH.AC),

    /**
     * Caminhão Extra Pesado com Reboque/Carreta: Acima de 40.000 kg de PBT.
     * Exemplos: FH 540 6x4 com carreta, Scania R 500 com bitrem.
     * CNH exigida: E
     */
    CAMINHAO_CARRETA("Caminhão com Reboque/Carreta", 80000, CategoriaCNH.E, CategoriaCNH.AE);

    private final String descricao;
    private final int pesoMaximoKg;
    private final List<CategoriaCNH> categoriasPermitidas;

    TipoVeiculo(String descricao, int pesoMaximoKg, CategoriaCNH... categoriasPermitidas) {
        this.descricao = descricao;
        this.pesoMaximoKg = pesoMaximoKg;
        this.categoriasPermitidas = Arrays.asList(categoriasPermitidas);
    }

    public String getDescricao() {
        return descricao;
    }

    public int getPesoMaximoKg() {
        return pesoMaximoKg;
    }

    /**
     * Retorna a categoria mínima de CNH exigida para dirigir este tipo de veículo.
     */
    public CategoriaCNH getCategoriaMinima() {
        return categoriasPermitidas.get(0);
    }

    /**
     * Verifica se uma determinada categoria de CNH permite dirigir este tipo de veículo.
     *
     * @param categoriaCNH Categoria da CNH do motorista
     * @return true se a CNH permite dirigir este veículo
     */
    public boolean permiteCategoriaCNH(CategoriaCNH categoriaCNH) {
        if (categoriaCNH == null) {
            return false;
        }

        // Verifica se a CNH do motorista permite dirigir a categoria mínima exigida
        return categoriaCNH.permiteCategoria(this.getCategoriaMinima());
    }

    /**
     * Retorna uma mensagem de erro amigável quando a CNH é incompatível.
     */
    public String getMensagemErroIncompatibilidade(CategoriaCNH categoriaCNH) {
        return String.format(
            "CNH categoria %s não permite dirigir %s. Categoria mínima exigida: %s",
            categoriaCNH != null ? categoriaCNH.name() : "não informada",
            this.descricao,
            this.getCategoriaMinima().name()
        );
    }
}

