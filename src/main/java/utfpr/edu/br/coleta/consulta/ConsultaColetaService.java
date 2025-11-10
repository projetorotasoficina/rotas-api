package utfpr.edu.br.coleta.consulta;

import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.consulta.dto.AgendaColetaDTO;
import utfpr.edu.br.coleta.consulta.dto.HistoricoColetaDTO;
import utfpr.edu.br.coleta.rota.FrequenciaRota;
import utfpr.edu.br.coleta.rota.Rota;
import utfpr.edu.br.coleta.rota.RotaRepository;
import utfpr.edu.br.coleta.rota.enums.Periodo;
import utfpr.edu.br.coleta.trajeto.Trajeto;
import utfpr.edu.br.coleta.trajeto.TrajetoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service responsável pelas consultas de agenda e histórico de coleta.
 * 
 * Autor: Sistema Rotas API
 */
@Service
@RequiredArgsConstructor
public class ConsultaColetaService {

    private final RotaRepository rotaRepository;
    private final TrajetoRepository trajetoRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();

    /**
     * Retorna a agenda de coleta para um endereço específico.
     * Busca todas as rotas cuja área geográfica contém o ponto informado.
     * 
     * @param latitude Latitude do endereço
     * @param longitude Longitude do endereço
     * @return Lista com a agenda de coleta
     */
    @Transactional(readOnly = true)
    public List<AgendaColetaDTO> buscarAgendaColeta(Double latitude, Double longitude) {
        // Cria um ponto geométrico com as coordenadas
        Point ponto = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        ponto.setSRID(4326);

        // Busca todas as rotas ativas
        List<Rota> todasRotas = rotaRepository.findByAtivoTrue();

        List<AgendaColetaDTO> agenda = new ArrayList<>();

        // Filtra rotas que contêm o ponto na área geográfica
        for (Rota rota : todasRotas) {
            if (rota.getAreaGeografica() != null && rota.getAreaGeografica().contains(ponto)) {
                // Para cada frequência da rota, cria um item na agenda
                for (FrequenciaRota freq : rota.getFrequencias()) {
                    AgendaColetaDTO item = new AgendaColetaDTO();
                    item.setNomeRota(rota.getNome());
                    item.setTipoResiduo(rota.getTipoResiduo().getNome());
                    item.setTipoColeta(rota.getTipoColeta().getNome());
                    item.setDiaSemana(freq.getDiaSemana());
                    item.setPeriodo(freq.getPeriodo());
                    item.setDescricaoPeriodo(formatarDescricaoPeriodo(freq.getPeriodo()));
                    item.setObservacoes(rota.getObservacoes());
                    agenda.add(item);
                }
            }
        }

        return agenda;
    }

    /**
     * Retorna o histórico de coletas realizadas próximas a um endereço.
     * Busca trajetos finalizados em rotas que atendem o endereço.
     * 
     * @param latitude Latitude do endereço
     * @param longitude Longitude do endereço
     * @return Lista com o histórico de coletas
     */
    @Transactional(readOnly = true)
    public List<HistoricoColetaDTO> buscarHistoricoColeta(Double latitude, Double longitude) {
        // Cria um ponto geométrico com as coordenadas
        Point ponto = geometryFactory.createPoint(new Coordinate(longitude, latitude));
        ponto.setSRID(4326);

        // Busca todas as rotas ativas
        List<Rota> todasRotas = rotaRepository.findByAtivoTrue();

        List<HistoricoColetaDTO> historico = new ArrayList<>();

        // Para cada rota que contém o ponto, busca os trajetos
        for (Rota rota : todasRotas) {
            if (rota.getAreaGeografica() != null && rota.getAreaGeografica().contains(ponto)) {
                // Busca trajetos da rota
                List<Trajeto> trajetos = trajetoRepository.findByRotaIdOrderByDataInicioDesc(rota.getId());

                // Converte trajetos para DTO
                List<HistoricoColetaDTO> trajetosDTO = trajetos.stream()
                        .filter(t -> t.getDataFim() != null) // Apenas trajetos finalizados
                        .map(this::converterTrajetoParaDTO)
                        .collect(Collectors.toList());

                historico.addAll(trajetosDTO);
            }
        }

        // Ordena por data de início (mais recente primeiro)
        historico.sort((a, b) -> b.getDataInicio().compareTo(a.getDataInicio()));

        return historico;
    }

    /**
     * Converte um Trajeto para HistoricoColetaDTO.
     */
    private HistoricoColetaDTO converterTrajetoParaDTO(Trajeto trajeto) {
        HistoricoColetaDTO dto = new HistoricoColetaDTO();
        dto.setTrajetoId(trajeto.getId());
        dto.setNomeRota(trajeto.getRota().getNome());
        dto.setTipoResiduo(trajeto.getRota().getTipoResiduo().getNome());
        dto.setTipoColeta(trajeto.getRota().getTipoColeta().getNome());
        dto.setDataInicio(trajeto.getDataInicio());
        dto.setDataFim(trajeto.getDataFim());
        dto.setNomeMotorista(trajeto.getMotorista().getNome());
        dto.setPlacaCaminhao(trajeto.getCaminhao().getPlaca());
        dto.setDistanciaTotal(trajeto.getDistanciaTotal());
        dto.setStatus(trajeto.getStatus() != null ? trajeto.getStatus().name() : null);
        return dto;
    }

    /**
     * Formata a descrição do período do dia.
     */
    private String formatarDescricaoPeriodo(Periodo periodo) {
        return switch (periodo) {
            case MANHA -> "Manhã: 06:00 - 12:00";
            case TARDE -> "Tarde: 12:00 - 18:00";
            case NOITE -> "Noite: 18:00 - 23:00";
        };
    }
}
