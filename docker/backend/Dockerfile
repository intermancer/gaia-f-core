FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

COPY build/libs/gaia-f-core-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
