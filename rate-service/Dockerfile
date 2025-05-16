
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/rate-service-*.jar app.jar

EXPOSE 8081  # Different port from main service

ENTRYPOINT ["java", "-jar", "app.jar"]