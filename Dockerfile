FROM eclipse-temurin:21-jre
WORKDIR /app

# JAR 두기 (Actions에서 COPY 필요 없음 → EC2에서 docker build 시 ADD로 jar 복사됨)
ADD jar/app.jar /app/app.jar

# 외부 application.yml을 /config/application.yml로 마운트
ENV SPRING_CONFIG_LOCATION=optional:file:/config/application.yml

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
