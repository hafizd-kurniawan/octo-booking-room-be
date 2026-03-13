FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# copy dependency dulu
COPY pom.xml .

# download dependency (akan di cache docker)
RUN mvn dependency:go-offline

# baru copy source code
COPY src ./src

# build jar
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]