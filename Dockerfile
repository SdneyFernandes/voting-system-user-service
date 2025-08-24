# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copia arquivos do Maven e dependências primeiro para cache
COPY pom.xml .
COPY src ./src

# Compila o projeto sem testes
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Variáveis padrão (sobrescrevíveis no docker-compose)
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/userdb \
    SPRING_DATASOURCE_USERNAME=postgres \
    SPRING_DATASOURCE_PASSWORD=senha123 \
    SERVER_PORT=8083


# Copia o JAR da fase de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
