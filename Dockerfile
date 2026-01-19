# Stage 1: Build con Maven
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# Stage 2: Runtime con Alpine simple
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Crear directorios necesarios
RUN mkdir -p /app/images && mkdir -p /app/logs

# Copiar el JAR desde la etapa de construcción
COPY --from=builder /app/target/kunturtatto-0.0.1.jar app_kuntur.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_kuntur.jar"]