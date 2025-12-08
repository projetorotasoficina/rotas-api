package utfpr.edu.br.coleta.caminhao;

import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.rota.Rota;

import java.util.List;

/**
 * Serviço de regras de negócio para Caminhão.
 *
 * Estende a interface genérica de CRUD e define
 * operações específicas de caminhão.
 */
public interface CaminhaoService extends ICrudService<Caminhao, Long> {

    /**
     * Lista as rotas compatíveis com o caminhão informado,
     * considerando tipo de coleta e tipo de resíduo.
     *
     * @param caminhaoId ID do caminhão
     * @return lista de rotas compatíveis
     */
    List<Rota> listarRotasCompativeis(Long caminhaoId);
}