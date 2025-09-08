package utfpr.edu.br.coleta.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Classe de configuração responsável por disponibilizar
 * uma instância singleton do ModelMapper para todo o projeto.
 *
 * Autor: Luiz Alberto dos Passos
 */
@Configuration
public class ModelMapperConfig {

    /**
     * Cria e expõe um bean do ModelMapper para ser injetado
     * automaticamente em serviços e controladores.
     *
     * @return instância configurada de ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}