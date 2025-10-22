package utfpr.edu.br.coleta.motorista.enums;

/**
 * Enum que representa as categorias de CNH (Carteira Nacional de Habilitação).
 *
 * Cada categoria permite dirigir tipos específicos de veículos conforme
 * legislação brasileira (Resolução CONTRAN nº 168/2004).
 *
 * Autor: Sistema
 */
public enum CategoriaCNH {
    /**
     * Categoria A: Veículos motorizados de duas ou três rodas, com ou sem carro lateral.
     */
    A("Motocicletas, motonetas e ciclomotores"),

    /**
     * Categoria B: Veículos motorizados não abrangidos pela categoria A, cujo peso bruto total
     * não exceda 3.500 kg e cuja lotação não exceda 8 lugares (excluindo o motorista).
     */
    B("Automóveis e utilitários leves"),

    /**
     * Categoria C: Veículos de carga cujo peso bruto total exceda 3.500 kg.
     * Exige habilitação B há pelo menos 1 ano.
     */
    C("Caminhões leves e médios"),

    /**
     * Categoria D: Veículos destinados ao transporte de passageiros com lotação acima de 8 lugares.
     * Exige habilitação B há pelo menos 2 anos ou C há pelo menos 1 ano.
     */
    D("Ônibus e vans de passageiros"),

    /**
     * Categoria E: Combinação de veículos em que a unidade tratora se enquadre nas categorias
     * B, C ou D e cuja unidade acoplada tenha mais de 6.000 kg ou cuja lotação exceda 8 lugares.
     * Exige habilitação C ou D há pelo menos 1 ano.
     */
    E("Caminhões pesados com reboque/carreta"),

    /**
     * Categoria AB: Combinação das categorias A e B.
     */
    AB("Motocicletas + Automóveis"),

    /**
     * Categoria AC: Combinação das categorias A e C.
     */
    AC("Motocicletas + Caminhões leves/médios"),

    /**
     * Categoria AD: Combinação das categorias A e D.
     */
    AD("Motocicletas + Ônibus"),

    /**
     * Categoria AE: Combinação das categorias A e E.
     */
    AE("Motocicletas + Caminhões pesados");

    private final String descricao;

    CategoriaCNH(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /**
     * Verifica se esta categoria permite dirigir veículos da categoria especificada.
     * Exemplo: CNH categoria E permite dirigir veículos das categorias B, C, D e E.
     *
     * @param categoria Categoria a ser verificada
     * @return true se esta CNH permite dirigir a categoria especificada
     */
    public boolean permiteCategoria(CategoriaCNH categoria) {
        if (this == categoria) {
            return true;
        }

        // Categoria E permite dirigir B, C, D e E
        if (this == E || this == AE) {
            return categoria == B || categoria == C || categoria == D || categoria == E ||
                   categoria == AB || categoria == AC || categoria == AD || categoria == AE;
        }

        // Categoria D permite dirigir B e D
        if (this == D || this == AD) {
            return categoria == B || categoria == D || categoria == AB || categoria == AD;
        }

        // Categoria C permite dirigir B e C
        if (this == C || this == AC) {
            return categoria == B || categoria == C || categoria == AB || categoria == AC;
        }

        // Categoria B permite dirigir apenas B
        if (this == B || this == AB) {
            return categoria == B || categoria == AB;
        }

        // Categoria A permite dirigir apenas A
        if (this == A) {
            return categoria == A;
        }

        return false;
    }
}

