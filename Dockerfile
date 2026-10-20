FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY nowme/gradle gradle
COPY nowme/gradlew gradlew
COPY nowme/build.gradle.kts build.gradle.kts
COPY nowme/settings.gradle.kts settings.gradle.kts
RUN ./gradlew dependencies --no-daemon || true

COPY nowme/src src
RUN ./gradlew bootJar -x test --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN mkdir -p uploads
EXPOSE 25565
ENTRYPOINT ["java", "-jar", "app.jar"]
