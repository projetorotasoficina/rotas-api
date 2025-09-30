package utfpr.edu.br.coleta.trajeto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;

import utfpr.edu.br.coleta.caminhao.Caminhao;
import utfpr.edu.br.coleta.generics.BaseEntity;
import utfpr.edu.br.coleta.motorista.Motorista;
import utfpr.edu.br.coleta.rota.Rota;
import utfpr.edu.br.coleta.trajeto.enums.TrajetoStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "trajeto")
public class Trajeto extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "rota_id", nullable = false)
    private Rota rota;

    @ManyToOne
    @JoinColumn(name = "caminhao_id", nullable = false)
    private Caminhao caminhao;

    @ManyToOne
    @JoinColumn(name = "motorista_id", nullable = false)
    private Motorista motorista;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Double distanciaTotal;

    @Enumerated(EnumType.STRING)
    private TrajetoStatus status;

    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString caminho;
}