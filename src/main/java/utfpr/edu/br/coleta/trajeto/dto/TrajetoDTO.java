package utfpr.edu.br.coleta.trajeto.dto;

import lombok.Data;
import utfpr.edu.br.coleta.tiporesiduo.TipoResiduoDTO;
import utfpr.edu.br.coleta.trajeto.enums.TrajetoStatus;

import java.time.LocalDateTime;

@Data
public class TrajetoDTO {
    private Long id;
    private Long rotaId;
    private Long caminhaoId;
    private Long motoristaId;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Double distanciaTotal;
    private TrajetoStatus status;
    private TipoResiduoDTO tipoResiduo;
}