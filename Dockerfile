# =======================================================
# 1. 빌드 단계 (Dependencies Caching)
# =======================================================
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# 1-1. 캐시 레이어 1: 빌드 설정 파일만 복사 (자주 안 바뀜)
COPY build.gradle settings.gradle ./ 
COPY gradlew .
RUN chmod +x ./gradlew
# 1-2. 캐시 레이어 2: 종속성 다운로드 및 테스트 건너뛰기
# (이 명령어는 build.gradle이 바뀌지 않으면 Docker가 건너뛰고 캐시된 결과를 사용)
RUN ./gradlew dependencies --no-daemon 

# 1-3. 캐시 레이어 3: 나머지 소스 코드 복사 (자주 바뀜)
COPY src src
RUN mkdir -p /app/uploads && chmod 777 /app/uploads

# 1-4. 최종 빌드 (소스 코드가 바뀌었을 때만 이 단계부터 재실행)
RUN ./gradlew build -x test --no-daemon

# =======================================================
# 2. 실행 단계 (경량화)
# =======================================================
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]