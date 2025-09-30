package utfpr.edu.br.coleta.incidente;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;
import utfpr.edu.br.coleta.trajeto.Trajeto;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "incidente")
public class Incidente extends BaseEntity {

    @NotNull(message = "O trajeto não pode ser nulo!")
    @ManyToOne(optional = false)
    @JoinColumn(name = "trajeto_id", nullable = false)
    private Trajeto trajeto;

    @NotBlank(message = "Um nome é obrigatório.")
    @Column(nullable = false)
    private String nome;

    @Size(max = 400, message = "Observações podem ter no máximo 400 caracteres.")
    @Column(length = 400)
    private String observacoes;

    @NotNull(message = "O timestamp é obrigatório!")
    @PastOrPresent(message = "O timestamp não pode ser no futuro.")
    @Column(name = "ts", nullable = false)
    private LocalDateTime ts;

    @Column(nullable = true)
    private Double longitude;

    @Column(nullable = true)
    private Double latitude;

    @Size(max = 255, message = "URLs de fotos podem ter no máximo 255 caracteres.")
    @Column(name = "foto_url", length = 255)
    private String fotoUrl;
}
