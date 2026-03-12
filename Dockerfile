FROM eclipse-temurin:17-jdk-alpine
LABEL authors="vikas"

WORKDIR /app

COPY target/*.jar app.jar

ENTRYPOINT ["java", "-Xmx512m", "-XX:+TieredCompilation", "-XX:TieredStopAtLevel=1", "-jar", "app.jar"]