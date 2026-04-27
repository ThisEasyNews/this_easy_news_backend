# ── Build stage ──────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy gradle wrapper and config first for layer caching
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle gradle.properties ./

RUN chmod +x gradlew

# Download dependencies (cache layer)
RUN TEMP=/tmp ./gradlew dependencies --no-daemon -q 2>/dev/null || true

# Copy source and build
COPY src/ src/

# TEMP=/tmp overrides the Windows-specific buildDir fallback in build.gradle
# Output: /tmp/this-easy-news-build/libs/this-easy-news-*.jar
RUN TEMP=/tmp ./gradlew bootJar --no-daemon -x test

# ── Runtime stage ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /tmp/this-easy-news-build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
