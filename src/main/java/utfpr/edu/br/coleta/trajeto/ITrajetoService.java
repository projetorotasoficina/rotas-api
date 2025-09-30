package utfpr.edu.br.coleta.trajeto;

import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.trajeto.dto.TrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.dto.TrajetoDTO;

public interface ITrajetoService extends ICrudService<Trajeto, Long> {
    TrajetoDTO iniciarTrajeto(TrajetoCreateDTO dto);
    TrajetoDTO finalizarTrajeto(Long id);
    TrajetoDTO cancelarTrajeto(Long id);
}