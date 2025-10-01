# ========================
# Etapa 1 - Build do backend
# ========================
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# ========================
# Etapa 2 - Runtime com Postgres + API
# ========================
FROM eclipse-temurin:17-jdk

# Instalar PostgreSQL
RUN apt-get update && \
    apt-get install -y postgresql postgresql-contrib && \
    rm -rf /var/lib/apt/lists/*

# Variáveis de ambiente padrão (sobrescritas pelo .env ou secrets do Fly)
ENV POSTGRES_DB=coleta_bd \
    POSTGRES_USER=postgres \
    POSTGRES_PASSWORD=admin \
    DATABASE_URL=jdbc:postgresql://localhost:5432/coleta_bd \
    DATABASE_USERNAME=postgres \
    DATABASE_PASSWORD=admin \
    PORT=8080

# Criar diretórios do Postgres
RUN mkdir -p /var/run/postgresql && chown -R postgres:postgres /var/run/postgresql

# Copiar o JAR da aplicação
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Expor portas da API e do banco
EXPOSE 8080 5432

# Script de inicialização: sobe Postgres e depois a API
CMD service postgresql start && \
    su postgres -c "psql -tc \"SELECT 1 FROM pg_database WHERE datname='${POSTGRES_DB}'\" | grep -q 1 || psql -c \"CREATE DATABASE ${POSTGRES_DB};\"" && \
    java -jar app.jar
