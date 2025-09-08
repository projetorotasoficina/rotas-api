package utfpr.edu.br.coleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Coleta.
 * Responsável por inicializar o contexto Spring Boot
 * e executar a aplicação.
 *
 * Autor: Luiz Alberto dos Passos
 */
@SpringBootApplication
public class ColetaApplication {

	/**
	 * Método principal que inicia a aplicação.
	 *
	 * @param args argumentos de linha de comando
	 */
	public static void main(String[] args) {
		SpringApplication.run(ColetaApplication.class, args);
	}

}