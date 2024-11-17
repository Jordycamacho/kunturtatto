FROM openjdk:17-jdk-alpine
COPY target/kunturtatto-0.0.1-SNAPSHOT.jar kunturtatto.jar
ENTRYPOINT [ "java", "-jar",  "kunturtatto.jar"]