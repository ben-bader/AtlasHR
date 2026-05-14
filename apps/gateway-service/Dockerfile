FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="HRMS Team"

WORKDIR /app

RUN addgroup -g 1000 hrms && adduser -D -u 1000 -G hrms hrms
COPY --from=build /app/target/*.jar app.jar
RUN chown hrms:hrms app.jar

USER hrms

HEALTHCHECK --interval=10s --timeout=5s --start-period=30s --retries=5 \
  CMD wget -qO- http://localhost:8084/actuator/health || exit 1

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]