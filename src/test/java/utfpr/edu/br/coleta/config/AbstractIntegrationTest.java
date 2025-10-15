package utfpr.edu.br.coleta.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Classe base abstrata para testes de integração usando Testcontainers.
 *
 * Todos os testes de repositório e integração devem estender esta classe
 * para garantir que utilizem PostgreSQL com PostGIS ao invés de H2.
 *
 * Usa um container singleton compartilhado entre TODAS as classes de teste
 * para evitar problemas de conexão e melhorar performance.
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {

    /**
     * Container PostgreSQL singleton compartilhado entre todos os testes.
     * É iniciado uma única vez e reutilizado por todas as classes de teste.
     */
    private static final PostgreSQLContainer<?> postgresContainer;

    static {
        postgresContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgis/postgis:15-3.3")
                        .asCompatibleSubstituteFor("postgres")
        )
                .withDatabaseName("coleta_test")
                .withUsername("test")
                .withPassword("test");

        // Inicia o container uma única vez
        postgresContainer.start();
    }

    /**
     * Configura as propriedades do Spring dinamicamente com base no container.
     * Isso sobrescreve as configurações do application.properties/yml durante os testes.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);

        // Configurações adicionais do Hibernate para PostgreSQL
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");

        // Desabilita validação de schema do Hibernate
        // Isso permite que o Flyway gerencie o schema sem interferência do Hibernate
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
    }
}
