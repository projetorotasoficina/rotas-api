package utfpr.edu.br.coleta;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = ColetaApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test") // usa o profile de teste
class ColetaApplicationTests {

	@Test
	void contextLoads() {
		// apenas verifica se o contexto carrega
	}
}