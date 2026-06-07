FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY conecta-pet-java/conecta-pet-java/pom.xml .
RUN mvn dependency:go-offline -B

COPY conecta-pet-java/conecta-pet-java/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]