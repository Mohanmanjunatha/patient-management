# step to build the code
#step to run the code
From maven:3.9.9-eclipse-temurin-21 AS builder

#specify a working directory inside the container

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B
#cache the dependencies when the change has been detected

COPY src ./src

RUN mvn clean package


FROM openjdk:21-jdk AS runner

WORKDIR /app

COPY --from=builder ./app/target/patient-service-0.0.1-SNAPSHOT.jar ./app.jar

EXPOSE 4000

ENTRYPOINT ["java", "-jar", "app.jar"]

