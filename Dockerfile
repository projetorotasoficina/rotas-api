# ========================
# Etapa 1 - Build do backend (Java 21)
# ========================
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# ========================
# Etapa 2 - Runtime apenas com a API
# ========================
FROM eclipse-temurin:21-jdk

# Definir diretório de trabalho
WORKDIR /app

# Copiar o JAR gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Variáveis de ambiente (substituídas pelo .env ou secrets)
ENV PORT=8080

# Expor apenas a porta da API
EXPOSE 8080

# Rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
