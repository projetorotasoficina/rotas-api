package utfpr.edu.br.coleta.generics.report;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Path; // Import necessário para navegar em relacionamentos

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementa a lógica de filtro dinâmico usando Spring Data JPA Specification.
 * Suporta filtros em campos de entidades relacionadas (JOINs) usando notação de ponto (ex: "tipoResiduo.nome").
 */
public class GenericSpecification<T> implements Specification<T> {

    private final Map<String, String> filters;

    public GenericSpecification(Map<String, String> filters) {
        this.filters = filters;
    }

    public static <T> GenericSpecification<T> byFilters(Map<String, String> filters) {
        return new GenericSpecification<>(filters);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value == null || value.trim().isEmpty()) {
                continue;
            }

            // Tratamento especial para o parâmetro "search"
            if ("search".equalsIgnoreCase(key)) {
                List<Predicate> searchPredicates = new ArrayList<>();

                root.getModel().getAttributes().forEach(attribute -> {
                    if (attribute.getJavaType().equals(String.class)) {
                        try {
                            Path<String> path = root.get(attribute.getName());
                            searchPredicates.add(
                                builder.like(builder.lower(path), "%" + value.toLowerCase() + "%")
                            );
                        } catch (Exception e) {
                        }
                    }
                });

                if (!searchPredicates.isEmpty()) {
                    predicates.add(builder.or(searchPredicates.toArray(new Predicate[0])));
                }
                continue;
            }

            try {
                // 1. Obter o Path (caminho) para o campo, suportando notação de ponto
                Path expression = getPath(root, key);

                // 2. Obter o tipo do campo final
                Class<?> fieldType = expression.getJavaType();

                // 3. Lógica de filtragem

                // Para Strings, usa LIKE (case-insensitive)
                if (fieldType.equals(String.class)) {
                    predicates.add(builder.like(builder.lower(expression.as(String.class)), "%" + value.toLowerCase() + "%"));
                }
                // Para outros tipos (Long, Integer, Double, Enum, etc.), usa igualdade
                else {
                    try {
                        Object typedValue = convertToType(fieldType, value);
                        predicates.add(builder.equal(expression, typedValue));
                    } catch (Exception e) {
                        // Opcional: logar erro de conversão e ignorar o filtro
                    }
                }
            } catch (IllegalArgumentException e) {
                // Ocorre se o campo ou relacionamento não existir. Ignora o filtro.
            }
        }

        // Combina todos os predicados com AND
        return builder.and(predicates.toArray(new Predicate[0]));
    }

    /**
     * Navega pelo caminho da entidade, suportando notação de ponto para relacionamentos.
     * Ex: "tipoResiduo.nome"
     */
    private Path getPath(Root<T> root, String key) {
        if (!key.contains(".")) {
            // Se não houver ponto, é um campo direto
            return root.get(key);
        }

        // Se houver ponto, navega pelo relacionamento
        String[] parts = key.split("\\.");
        Path path = root.get(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            // Usa .get() para navegar por campos embutidos ou .join() para associações
            path = path.get(parts[i]);
        }
        return path;
    }

    // Método auxiliar para conversão de tipo (simplificado)
    private Object convertToType(Class<?> targetType, String value) {
        if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.valueOf(value);
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.valueOf(value);
        } else if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            return Double.valueOf(value);
        }
        // Adicionar suporte para Datas, Booleanos, Enums, etc.
        return value;
    }
}