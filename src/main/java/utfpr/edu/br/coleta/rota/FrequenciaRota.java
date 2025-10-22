package utfpr.edu.br.coleta.rota;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;

/**
 * Entidade que representa a frequência de uma rota,
 * associando um dia da semana a um período específico.
 *
 * Permite que uma mesma rota tenha períodos diferentes
 * para cada dia da semana (ex: Segunda MANHA, Quarta TARDE).
 *
 * Autor: Sistema
 */
@Entity
@Table(name = "tb_frequencia_rota",
       uniqueConstraints = @UniqueConstraint(columnNames = {"rota_id", "dia_semana"}))
@Getter
@Setter
@NoArgsConstructor
public class FrequenciaRota extends BaseEntity {

    @NotNull(message = "A rota é obrigatória.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rota_id", nullable = false)
    private Rota rota;

    @NotNull(message = "O dia da semana é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemana diaSemana;

    @NotNull(message = "O período é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "periodo", nullable = false, length = 10)
    private Periodo periodo;

    public FrequenciaRota(Rota rota, DiaSemana diaSemana, Periodo periodo) {
        this.rota = rota;
        this.diaSemana = diaSemana;
        this.periodo = periodo;
    }
}

