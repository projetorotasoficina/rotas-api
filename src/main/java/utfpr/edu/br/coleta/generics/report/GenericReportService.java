package utfpr.edu.br.coleta.generics.report;

import utfpr.edu.br.coleta.caminhao.CaminhaoRepository;
import utfpr.edu.br.coleta.generics.report.GenericSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;
import utfpr.edu.br.coleta.incidente.IncidenteRepository;
import utfpr.edu.br.coleta.motorista.MotoristaRepository;
import utfpr.edu.br.coleta.rota.RotaRepository;
import utfpr.edu.br.coleta.tipocoleta.TipoColetaRepository;
import utfpr.edu.br.coleta.tiporesiduo.TipoResiduoRepository;
import utfpr.edu.br.coleta.trajeto.TrajetoRepository;
import utfpr.edu.br.coleta.usuario.UsuarioRepository;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço que utiliza injeção de dependência para acessar repositórios e Specifications.
 */
@Service
public class GenericReportService {

    // Mapeamento de nomes de entidade para seus Repositórios (Chave: nome, Valor: Repositório)
    private final Map<String, JpaSpecificationExecutor<?>> repositoryMap = new HashMap<>();


    // Construtor para inicializar o mapa de repositórios
    // Você deve injetar todos os seus repositórios aqui
    public GenericReportService(CaminhaoRepository caminhaoRepository, IncidenteRepository  incidenteRepository, MotoristaRepository motoristaRepository, RotaRepository rotaRepository, TipoColetaRepository tipoColetaRepository, TipoResiduoRepository tipoResiduoRepository, TrajetoRepository trajetoRepository, UsuarioRepository usuarioRepository) {
        repositoryMap.put("caminhao", caminhaoRepository);
        repositoryMap.put("incidente", incidenteRepository);
        repositoryMap.put("motorista", motoristaRepository);
        repositoryMap.put("rota", rotaRepository);
        repositoryMap.put("tipoColeta", tipoColetaRepository);
        repositoryMap.put("tipoResiduo", tipoResiduoRepository);
        repositoryMap.put("trajeto", trajetoRepository);
        repositoryMap.put("usuario", usuarioRepository);

        repositoryMap.put("tipocoleta", tipoColetaRepository);
        repositoryMap.put("tiporesiduo", tipoResiduoRepository);

    }

    /**
     * Busca dados de uma entidade específica aplicando filtros dinâmicos.
     *
     * @param entityName O nome da entidade (e.g., "routes").
     * @param filters Os filtros da query string.
     * @return Uma lista de Mapas (JSON) com os dados filtrados.
     */
    @SuppressWarnings({"unchecked", "rawtypes"}) // Necessário para resolver o erro de tipos genéricos
    public List<Map<String, Object>> getReportData(String entityName, Map<String, String> filters) {

        String lowerCaseEntity = entityName.toLowerCase();

        // 1. Obter o Repositório
        JpaSpecificationExecutor<?> repository = repositoryMap.get(lowerCaseEntity);

        if (repository == null) {
            throw new IllegalArgumentException("Entidade '" + entityName + "' não suportada para relatórios genéricos. Verifique o mapeamento no GenericReportService.");
        }

        // 2. Criar a Specification com os filtros
        Specification<?> spec = GenericSpecification.byFilters(filters);

        // 3. Executar a consulta (com cast para resolver o erro de compilação)
        List<?> entities = ((JpaSpecificationExecutor) repository).findAll((Specification) spec);

        // 4. Converter Entidades JPA para List<Map<String, Object>>
        return convertEntitiesToMapList(entities);
    }

    /**
     * Converte uma lista de Entidades JPA para uma lista de Mapas genéricos.
     * Isso é necessário para retornar um JSON flexível sem DTOs específicos.
     */
    private List<Map<String, Object>> convertEntitiesToMapList(List<?> entities) {
        return entities.stream()
                .map(this::convertEntityToMap)
                .collect(Collectors.toList());
    }

    /**
     * Converte uma única Entidade JPA para um Map<String, Object> usando Reflection.
     * IMPORTANTE: Ignora relacionamentos (@ManyToOne, @OneToMany, etc) para evitar JSON gigante
     * e problemas de serialização circular.
     */
    private Map<String, Object> convertEntityToMap(Object entity) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = entity.getClass();

        // Percorre todos os campos da entidade
        for (Field field : clazz.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(jakarta.persistence.ManyToOne.class) ||
                    field.isAnnotationPresent(jakarta.persistence.OneToMany.class) ||
                    field.isAnnotationPresent(jakarta.persistence.ManyToMany.class) ||
                    field.isAnnotationPresent(jakarta.persistence.OneToOne.class)) {

                    if (field.isAnnotationPresent(jakarta.persistence.ManyToOne.class)) {
                        field.setAccessible(true);
                        Object relatedEntity = field.get(entity);
                        if (relatedEntity != null) {
                            try {
                                Field idField = relatedEntity.getClass().getDeclaredField("id");
                                idField.setAccessible(true);
                                Object relatedId = idField.get(relatedEntity);
                                map.put(field.getName() + "Id", relatedId);
                            } catch (NoSuchFieldException e) {
                            }
                        }
                    }
                    continue;
                }

                field.setAccessible(true); // Permite acesso a campos privados
                Object value = field.get(entity);

                if (value == null ||
                    value instanceof String ||
                    value instanceof Number ||
                    value instanceof Boolean ||
                    value instanceof java.time.LocalDate ||
                    value instanceof java.time.LocalDateTime ||
                    value instanceof java.util.Date ||
                    value.getClass().isEnum()) {
                    map.put(field.getName(), value);
                }

            } catch (IllegalAccessException e) {
            }
        }
        return map;
    }
}