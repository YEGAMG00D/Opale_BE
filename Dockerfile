FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY jar/app.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
