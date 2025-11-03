# ===== Build stage =====
FROM gradle:8.7.0-jdk21 AS build
WORKDIR /home/gradle/project
COPY . .
RUN gradle clean build -x test

# ===== Run stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# 빌드된 JAR 복사
COPY --from=build /home/gradle/project/build/libs/*.jar /app/app.jar

# 외부 설정(application.yml) 마운트 및 경로 설정
# -v 로 마운트된 /config/application.yml 을 읽게 함
ENV SPRING_CONFIG_LOCATION=optional:file:/config/application.yml

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
