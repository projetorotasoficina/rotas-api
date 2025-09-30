package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Point;
import utfpr.edu.br.coleta.generics.BaseEntity;
import utfpr.edu.br.coleta.trajeto.Trajeto;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "ponto_trajeto")
public class PontoTrajeto extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trajeto_id", nullable = false)
    private Trajeto trajeto;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point localizacao;

    @Column(nullable = false)
    private LocalDateTime horario;

    @Column
    private String observacao;
}