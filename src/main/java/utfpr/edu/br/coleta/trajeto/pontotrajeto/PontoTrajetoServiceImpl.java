package utfpr.edu.br.coleta.trajeto.pontotrajeto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.trajeto.Trajeto;
import utfpr.edu.br.coleta.trajeto.TrajetoRepository;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoBatchResponseDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.pontotrajeto.dto.PontoTrajetoDTO;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PontoTrajetoServiceImpl extends CrudServiceImpl<PontoTrajeto, Long> implements IPontoTrajetoService {

    private final PontoTrajetoRepository repository;
    private final TrajetoRepository trajetoRepository;
    private final ModelMapper mapper;

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    protected PontoTrajetoRepository getRepository() {
        return repository;
    }

    protected ModelMapper getModelMapper() {
        return mapper;
    }

    @Override
    public PontoTrajetoDTO registrarPonto(PontoTrajetoCreateDTO dto) {

        PontoTrajeto ponto = new PontoTrajeto();
        ponto.setId(null);

        Point point = geometryFactory.createPoint(
                new org.locationtech.jts.geom.Coordinate(dto.getLongitude(), dto.getLatitude())
        );

        ponto.setTrajeto(trajetoRepository.findById(dto.getTrajetoId())
                .orElseThrow(() -> new RuntimeException("Trajeto não encontrado")));

        ponto.setLocalizacao(point);
        ponto.setHorario(dto.getHorario());
        ponto.setObservacao(dto.getObservacao());

        PontoTrajeto salvo = repository.save(ponto);

        // usa o conversor manual que pega X/Y do Point
        return convertToDTO(salvo);
    }

    @Override
    public List<PontoTrajetoDTO> findByTrajeto(Long trajetoId) {
        return repository.findByTrajetoId(trajetoId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    @Override
    @Transactional
    public PontoTrajetoBatchResponseDTO registrarPontosLote(List<PontoTrajetoCreateDTO> pontos) {
        PontoTrajetoBatchResponseDTO response = new PontoTrajetoBatchResponseDTO();
        response.setTotalRecebidos(pontos.size());

        log.info("Iniciando processamento de lote com {} pontos", pontos.size());

        for (int i = 0; i < pontos.size(); i++) {
            PontoTrajetoCreateDTO dto = pontos.get(i);
            try {
                PontoTrajetoDTO pontoSalvo = registrarPonto(dto);
                response.adicionarPontoSalvo(pontoSalvo);
            } catch (Exception e) {
                log.error("Erro ao processar ponto no índice {}: {}", i, e.getMessage());
                response.adicionarErro(i, dto, e.getMessage());
            }
        }

        if (response.todosSalvos()) {
            response.setMensagem(String.format("Todos os %d pontos foram salvos com sucesso.", response.getTotalSalvos()));
        } else if (response.getTotalSalvos() > 0) {
            response.setMensagem(String.format("%d de %d pontos salvos. %d erros.",
                    response.getTotalSalvos(), response.getTotalRecebidos(), response.getTotalErros()));
        } else {
            response.setMensagem("Nenhum ponto foi salvo. Verifique os erros.");
        }

        log.info("Processamento concluído: {} salvos, {} erros", response.getTotalSalvos(), response.getTotalErros());

        return response;
    }

    @Override
    @Transactional
    public PontoTrajetoBatchResponseDTO registrarPontosLoteAtomico(List<PontoTrajetoCreateDTO> pontos) {
        PontoTrajetoBatchResponseDTO response = new PontoTrajetoBatchResponseDTO();
        response.setTotalRecebidos(pontos.size());

        log.info("Iniciando processamento ATÔMICO de lote com {} pontos", pontos.size());

        try {
            for (PontoTrajetoCreateDTO dto : pontos) {
                if (!trajetoRepository.existsById(dto.getTrajetoId())) {
                    throw new RuntimeException("Trajeto ID " + dto.getTrajetoId() + " não encontrado");
                }
            }

            List<PontoTrajeto> pontosParaSalvar = new ArrayList<>();
            for (PontoTrajetoCreateDTO dto : pontos) {
                Point point = geometryFactory.createPoint(
                        new org.locationtech.jts.geom.Coordinate(dto.getLongitude(), dto.getLatitude())
                );

                Trajeto trajeto = trajetoRepository.findById(dto.getTrajetoId())
                        .orElseThrow(() -> new RuntimeException("Trajeto não encontrado"));

                PontoTrajeto ponto = new PontoTrajeto();
                ponto.setId(null); // <--- MESMA CORREÇÃO AQUI
                ponto.setTrajeto(trajeto);
                ponto.setLocalizacao(point);
                ponto.setHorario(dto.getHorario());
                ponto.setObservacao(dto.getObservacao());

                pontosParaSalvar.add(ponto);
            }

            List<PontoTrajeto> pontosSalvos = repository.saveAll(pontosParaSalvar);

            for (PontoTrajeto ponto : pontosSalvos) {
                response.adicionarPontoSalvo(convertToDTO(ponto));
            }

            response.setMensagem(String.format("Todos os %d pontos foram salvos com sucesso (transação atômica).",
                    response.getTotalSalvos()));

            log.info("Processamento atômico concluído: {} pontos salvos", response.getTotalSalvos());

        } catch (Exception e) {
            log.error("Erro no processamento atômico: {}", e.getMessage());
            response.setMensagem("Erro no processamento: " + e.getMessage() + ". Nenhum ponto foi salvo (rollback).");
            throw e;
        }

        return response;
    }

    private PontoTrajetoDTO convertToDTO(PontoTrajeto ponto) {
        PontoTrajetoDTO dto = new PontoTrajetoDTO();
        dto.setId(ponto.getId());
        dto.setTrajetoId(ponto.getTrajeto().getId());
        dto.setLatitude(ponto.getLocalizacao().getY());
        dto.setLongitude(ponto.getLocalizacao().getX());
        dto.setHorario(ponto.getHorario());
        dto.setObservacao(ponto.getObservacao());
        return dto;
    }
}
