package utfpr.edu.br.coleta.trajeto;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.caminhao.Caminhao;
import utfpr.edu.br.coleta.caminhao.CaminhaoRepository;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;
import utfpr.edu.br.coleta.motorista.Motorista;
import utfpr.edu.br.coleta.motorista.MotoristaRepository;
import utfpr.edu.br.coleta.rota.RotaRepository;
import utfpr.edu.br.coleta.trajeto.dto.TrajetoCreateDTO;
import utfpr.edu.br.coleta.trajeto.dto.TrajetoDTO;
import utfpr.edu.br.coleta.trajeto.enums.TrajetoStatus;
import utfpr.edu.br.coleta.motorista.validator.CNHVeiculoValidator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrajetoServiceImpl extends CrudServiceImpl<Trajeto, Long> implements ITrajetoService {

    private final TrajetoRepository repository;
    private final CaminhaoRepository caminhaoRepository;
    private final MotoristaRepository motoristaRepository;
    private final RotaRepository rotaRepository;
    private final ModelMapper mapper;
    private final CNHVeiculoValidator cnhVeiculoValidator;

    @Override
    protected TrajetoRepository getRepository() {
        return repository;
    }

    // 🔑 Esse método não existe no CrudServiceImpl, mas o teu Controller exige ModelMapper.
    // Então sobrescrevemos aqui para injetar corretamente no fluxo.
    protected ModelMapper getModelMapper() {
        return mapper;
    }

    @Override
    public TrajetoDTO iniciarTrajeto(TrajetoCreateDTO dto) {
        Trajeto trajeto = new Trajeto();
        trajeto.setRota(rotaRepository.findById(dto.getRotaId())
                .orElseThrow(() -> new RuntimeException("Rota não encontrada")));
        
        Caminhao caminhao = caminhaoRepository.findById(dto.getCaminhaoId())
                .orElseThrow(() -> new RuntimeException("Caminhão não encontrado"));
        
        Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
        
        // Validação: CNH do motorista deve ser compatível com o tipo de veículo
        cnhVeiculoValidator.validar(motorista, caminhao);
        
        trajeto.setCaminhao(caminhao);
        trajeto.setMotorista(motorista);
        trajeto.setDataInicio(LocalDateTime.now());
        trajeto.setStatus(TrajetoStatus.EM_ANDAMENTO);

        return mapper.map(repository.save(trajeto), TrajetoDTO.class);
    }

    @Override
    public TrajetoDTO finalizarTrajeto(Long id) {
        Trajeto trajeto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajeto não encontrado"));
        trajeto.setDataFim(LocalDateTime.now());
        trajeto.setStatus(TrajetoStatus.FINALIZADO);

        return mapper.map(repository.save(trajeto), TrajetoDTO.class);
    }

    @Override
    public TrajetoDTO cancelarTrajeto(Long id) {
        Trajeto trajeto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trajeto não encontrado"));
        trajeto.setDataFim(LocalDateTime.now());
        trajeto.setStatus(TrajetoStatus.CANCELADO);

        return mapper.map(repository.save(trajeto), TrajetoDTO.class);
    }
}