# ── Build stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and config first for layer caching
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle gradle.properties ./

RUN chmod +x gradlew

# Download dependencies (이 단계에서 에러가 나도 무시하고 진행하도록 설정된 기존 로직 유지)
RUN ./gradlew dependencies --no-daemon -q 2>/dev/null || true

# Copy source and build
COPY src/ src/

# 경로 복잡성을 피하기 위해 TEMP 옵션 없이 표준 빌드 수행
RUN ./gradlew bootJar --no-daemon -x test

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Gradle의 표준 빌드 결과물 경로에서 JAR 파일을 복사
# /app/build/libs/ 아래에 생성
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]