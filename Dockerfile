# 1. 빌드 단계
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app
COPY . .

# radlew 파일에 실행 권한을 강제로 부여
RUN chmod +x ./gradlew

# 빌드 실행 (테스트 건너뛰기)
RUN ./gradlew build -x test --no-daemon

# 2. 실행 단계 (호환성 좋은 기본 이미지 사용)
# alpine 대신 일반 jdk 버전을 사용
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]