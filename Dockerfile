# ============================
# 1. Stage: сборка jar через Gradle Wrapper
# ============================
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Копируем wrapper и gradle конфиги (для кэширования зависимостей)
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Скачиваем зависимости чтобы кэшировались
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# Копируем исходники
COPY src src

# Собираем jar
RUN ./gradlew clean build --no-daemon

# ============================
# 2. Stage: минимальный runtime
# ============================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем только готовый jar из builder
COPY --from=builder /app/build/libs/onlineStore_boot.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
