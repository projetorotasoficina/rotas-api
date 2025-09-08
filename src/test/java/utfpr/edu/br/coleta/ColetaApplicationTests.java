package utfpr.edu.br.coleta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Teste de inicialização da aplicação Coleta.
 * Garante que o contexto do Spring Boot é carregado corretamente
 * utilizando o profile de teste.
 *
 * Autor: Luiz Alberto dos Passos
 */
@SpringBootTest(classes = ColetaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ColetaApplicationTests {

	/**
	 * Verifica se o contexto da aplicação é carregado sem erros.
	 */
	@Test
	void contextLoads() {
		// apenas verifica se o contexto carrega
	}
}