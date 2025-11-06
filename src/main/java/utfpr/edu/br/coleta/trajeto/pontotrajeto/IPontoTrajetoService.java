package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoBatchResponseDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoDTO;

import java.util.List;

public interface IPontoTrajetoService extends ICrudService<PontoTrajeto, Long> {
    /**
     * Registra um único ponto no trajeto.
     */
    PontoTrajetoDTO registrarPonto(PontoTrajetoCreateDTO dto);

    /**
     * Busca todos os pontos de um trajeto específico.
     */
    List<PontoTrajetoDTO> findByTrajeto(Long trajetoId);

    /**
     * Registra múltiplos pontos em lote.
     * Continua processando mesmo se houver erros em alguns pontos.
     * Retorna detalhes de sucesso e erros.
     */
    PontoTrajetoBatchResponseDTO registrarPontosLote(List<PontoTrajetoCreateDTO> pontos);

    /**
     * Registra múltiplos pontos em lote de forma atômica.
     * Se houver erro em qualquer ponto, nenhum é salvo (rollback).
     */
    PontoTrajetoBatchResponseDTO registrarPontosLoteAtomico(List<PontoTrajetoCreateDTO> pontos);
}