package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoDTO;

import java.util.List;

public interface IPontoTrajetoService extends ICrudService<PontoTrajeto, Long> {
    PontoTrajetoDTO registrarPonto(PontoTrajetoCreateDTO dto);
    List<PontoTrajetoDTO> findByTrajeto(Long trajetoId);
}