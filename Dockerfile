# =======================================================
# 1. 빌드 단계 (안정성 확보)
# =======================================================
FROM gradle:8.5-jdk17 AS builder
WORKDIR /app

# 모든 파일과 폴더를 한 번에 복사합니다. (gradle, src, build.gradle 모두 포함)
COPY . /app 

# 2. 파일 권한 부여 및 폴더 생성
RUN chmod +x ./gradlew
RUN mkdir -p /app/uploads && chmod 777 /app/uploads 

# 3. 최종 빌드 (테스트 건너뛰기)
# (이전에 dependencies를 따로 실행하던 단계를 제거하여 빌드 단순화)
RUN ./gradlew build -x test --no-daemon

# =======================================================
# 2. 실행 단계
# =======================================================
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]