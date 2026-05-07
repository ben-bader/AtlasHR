FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml .

RUN chmod +x mvnw

COPY src src

RUN ./mvnw clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# docker-compose healthcheck uses wget on /actuator/health
RUN apk add --no-cache wget

# safer wildcard (avoids version coupling)
COPY --from=builder /app/target/*.jar app.jar

# run as non-root (best practice)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]
