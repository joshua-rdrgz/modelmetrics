FROM maven:3.8.4-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -q dependency:go-offline

COPY src ./src

RUN mvn -q package -DskipTests


FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE $PORT

CMD ["java", "-jar", "app.jar"]