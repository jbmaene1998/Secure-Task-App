FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY . .
RUN mvn -pl auth-service -am -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the jar produced inside the build stage
COPY --from=build /workspace/auth-service/target/*-SNAPSHOT.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
