FROM eclipse-temurin:21-jre
WORKDIR /app

# GitHub Actions에서 업로드한 JAR을 컨테이너로 복사
COPY app.jar app.jar

# 외부 application.yml 마운트 적용
ENV SPRING_CONFIG_LOCATION=file:/app/application.yml

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
