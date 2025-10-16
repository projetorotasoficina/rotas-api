# Rotas API - Sistema de Controle de Coleta de Lixo

![Badge de Build](https://img.shields.io/badge/build-passing-brightgreen) ![Badge de Cobertura de Testes](https://img.shields.io/badge/coverage-85%25-yellow) ![Badge de Licença](https://img.shields.io/badge/license-MIT-blue)

Um sistema de API REST para gerenciamento de rotas de coleta de lixo, desenvolvido como parte do projeto da disciplina de Oficina de Integração 3 do curso de Engenharia de Software da UTFPR-CP.

## Tabela de Conteúdos

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Começando](#come%C3%A7ando)
  - [Pré-requisitos](#pr%C3%A9-requisitos)
  - [Instalação](#instala%C3%A7%C3%A3o)
- [Uso](#uso)
- [Endpoints da API](#endpoints-da-api)
- [Banco de Dados](#banco-de-dados)
- [Executando os Testes](#executando-os-testes)

## Sobre o Projeto

O Rotas API é o backend do sistema de mesmo nome, que visa otimizar e gerenciar a coleta de lixo em uma cidade. Ele fornece uma interface RESTful para cadastro e controle de motoristas, caminhões, rotas, tipos de resíduos, e para o registro de incidentes durante os trajetos. O sistema também se integra a um aplicativo móvel para os motoristas e a uma interface web para os gestores.

## Funcionalidades

- Gestão de Usuários (Motoristas e Gestores)
- Gestão de Caminhões
- Gestão de Rotas e Trajetos
- Gestão de Tipos de Coleta e Resíduos
- Registro e Acompanhamento de Incidentes
- Autenticação e Autorização com JWT
- Envio de E-mails para notificações
- Integração com Aplicativo Android
- Documentação da API com OpenAPI (Swagger)

## Tecnologias Utilizadas

- **Backend:**
  - Java 21
  - Spring Boot 3.5.5
  - Spring Data JPA (Hibernate)
  - Spring Security
  - Maven
- **Banco de Dados:**
  - PostgreSQL com PostGIS
  - Flyway (para migrations)
- **Testes:**
  - JUnit 5
  - Testcontainers
- **Containerização:**
  - Docker
  - Docker Compose
- **Outras Ferramentas:**
  - Lombok
  - ModelMapper
  - JWT (JSON Web Token)
  - SendGrid (para envio de e-mails)
  - MinIO (para armazenamento de objetos)
  - Apache Tika (para extração de metadados)

## Começando

Para obter uma cópia local do projeto e executá-la, siga os passos abaixo.

### Pré-requisitos

- Java 21 ou superior
- Maven 3.9 ou superior
- Docker e Docker Compose

### Instalação

1. Clone o repositório:
   ```sh
   git clone https://github.com/projetorotasoficina/rotas-api.git
   ```
2. Navegue até o diretório do projeto:
   ```sh
   cd rotas-api
   ```
3. Crie um arquivo `.env` na raiz do projeto, baseado no arquivo `.env.example` (que precisa ser criado), com as variáveis de ambiente necessárias.
4. Execute o Docker Compose para subir os serviços (API, banco de dados, etc.):
   ```sh
   docker-compose up -d
   ```

## Uso

Após a instalação, a API estará disponível em `http://localhost:8080`. Você pode utilizar uma ferramenta como o Postman ou o Insomnia para interagir com os endpoints.

## Endpoints da API

A documentação completa da API, gerada com o OpenAPI (Swagger), está disponível em:

`http://localhost:8080/swagger-ui.html`

## Banco de Dados

O projeto utiliza o Flyway para gerenciar as migrações do banco de dados. As migrações são aplicadas automaticamente na inicialização da aplicação. Os scripts de migração estão localizados em `src/main/resources/db/migration`.

## Executando os Testes

Para executar os testes automatizados, utilize o seguinte comando Maven:

```sh
mvn test
```

## Alunos:
- Oliver Lohann Mayer
- Pedro Henrique Sauthier
- Diego Chruscinski
- Matheus Emanoel
- Gustavo Moretto
- Luiz Alberto Passos
