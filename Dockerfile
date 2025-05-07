FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/kunturtatto-0.0.1.jar
COPY ${JAR_FILE} app_kuntur.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app_kuntur.jar"]