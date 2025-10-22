package utfpr.edu.br.coleta.caminhao;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.caminhao.enums.TipoVeiculo;
import utfpr.edu.br.coleta.generics.BaseEntity;
import utfpr.edu.br.coleta.tipocoleta.TipoColeta;
import utfpr.edu.br.coleta.tiporesiduo.TipoResiduo;

/**
 * Entidade que representa um Caminhão no sistema de coleta.
 *
 * Contém informações do veículo utilizado para realizar
 * as coletas, vinculado a um tipo de coleta e a um tipo
 * de resíduo. Um caminhão pode participar de trajetos,
 * respeitando as regras de negócio do sistema.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Entity
@Table(name = "tb_caminhao")
@Getter
@Setter
@NoArgsConstructor
public class Caminhao extends BaseEntity {

    /** Modelo do caminhão (ex: Volvo VM 270). */
    @NotBlank(message = "O modelo é obrigatório.")
    @Column(nullable = false)
    private String modelo;

    /** Placa do caminhão, única e obrigatória. */
    @NotBlank(message = "A placa é obrigatória.")
    @Pattern(regexp = "([A-Z]{3}\\d{4})|([A-Z]{3}\\d[A-Z]\\d{2})", message = "Placa deve estar no formato XXX0000 ou XXX0X00.")
    @Column(nullable = false, unique = true, length = 8)
    private String placa;

    /** Tipo de coleta vinculada ao caminhão. */
    @NotNull(message = "O tipo de coleta é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_coleta_id", nullable = false)
    private TipoColeta tipoColeta;

    /** Tipo de resíduo que o caminhão transporta. */
    @NotNull(message = "O tipo de resíduo é obrigatório.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "residuo_id", nullable = false)
    private TipoResiduo residuo;

    /** Tipo de veículo (categoria de caminhaõ) que determina a CNH mínima exigida. */
    @NotNull(message = "O tipo de veículo é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_veiculo", nullable = false, length = 20)
    private TipoVeiculo tipoVeiculo;

    /** Define se o caminhão está ativo no sistema. */
    @NotNull(message = "O campo ativo é obrigatório.")
    @Column(nullable = false)
    private Boolean ativo;

    /** Lista de trajetos realizados pelo caminhão. */
  //  @OneToMany(mappedBy = "caminhao", cascade = CascadeType.ALL, orphanRemoval = true)
   // private List<Trajeto> trajetos;

    /**
     * Regra de negócio: Caminhão inativo não pode iniciar trajeto.
     */
    public boolean podeIniciarTrajeto() {
        return Boolean.TRUE.equals(this.ativo);
    }
}
