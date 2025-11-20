# =======================================================
# 1. 빌드 단계 (Dependencies Caching)
# =======================================================
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 1-1. 캐시 레이어 1: 빌드 설정 파일만 복사 (자주 안 바뀜)
COPY build.gradle settings.gradle ./
COPY gradlew .

# 👇👇👇 [핵심 수정!] 이 줄을 추가합니다. 👇👇👇
COPY gradle/ gradle/ 
# 👆👆👆

# 1-2. 파일 권한 부여 (필수)
RUN chmod +x ./gradlew

# 1-3. 종속성 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon

# 1-4. 캐시 레이어 3: 나머지 소스 코드 복사 (자주 바뀜)
COPY src src
# (업로드 폴더 생성)
RUN mkdir -p /app/uploads && chmod 777 /app/uploads

# 1-5. 최종 빌드 (테스트 건너뛰기)
RUN ./gradlew build -x test --no-daemon

# =======================================================
# 2. 실행 단계 (경량화)
# =======================================================
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]