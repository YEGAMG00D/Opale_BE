# ===== Build stage =====
FROM gradle:8.7.0-jdk21 AS build
WORKDIR /home/gradle/project
COPY . .
RUN gradle clean build -x test

# ===== Run stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app
# 빌드 산출물 경로는 프로젝트에 따라 다를 수 있음 (build/libs/*.jar)
COPY --from=build /home/gradle/project/build/libs/*.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
