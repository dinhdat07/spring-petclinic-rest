FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Allows overriding the jar path at build time: --build-arg JAR_FILE=target/gateway-service-3.4.3.jar
ARG JAR_FILE=target/gateway-service-3.4.3.jar
COPY ${JAR_FILE} app.jar


EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
