# 1. 빌드 단계 (Gradle 공식 이미지 사용)
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test --no-daemon

# 2. 실행 단계 (안정적인 Eclipse Temurin JDK 17 사용)
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

# 3. 실행 포트 및 명령어
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]