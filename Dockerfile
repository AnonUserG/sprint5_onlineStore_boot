FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY build/libs/onlineStore_boot.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
