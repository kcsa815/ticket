# =======================================================
# 1. 빌드 단계 (최종 안정성 확보)
# =======================================================
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app

# [핵심!] 모든 파일과 폴더를 한 번에 복사합니다. (gradle, src, build.gradle 모두 포함)
# 이 방법이 가장 안정적이며, 이전의 COPY 순서 오류를 막습니다.
COPY . /app

# 2. 파일 권한 부여 및 폴더 생성
RUN chmod +x ./gradlew
RUN mkdir -p /app/uploads && chmod 777 /app/uploads 

# 3. 최종 빌드 (테스트 건너뛰기)
RUN ./gradlew build -x test --no-daemon

# =======================================================
# 2. 실행 단계
# =======================================================
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]