# ---- Etapa 1: build con Maven + JDK 17 ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cachear dependencias primero (mejor uso de la caché de capas)
COPY pom.xml .
RUN mvn -q dependency:go-offline

# Compilar el jar ejecutable (sin tests para acelerar el deploy)
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- Etapa 2: runtime ligero con JRE 17 ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copiar únicamente el jar final
COPY --from=build /app/target/*.jar app.jar

# Render inyecta la variable PORT; Spring Boot ya la lee via server.port=${PORT}
EXPOSE 5000
ENTRYPOINT ["java", "-jar", "app.jar"]
