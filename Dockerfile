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

# 외부 설정 파일(application.yml) 참조하도록 설정
ENV SPRING_CONFIG_LOCATION=file:/home/ec2-user/resources/application.yml

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
