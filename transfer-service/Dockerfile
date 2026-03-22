FROM eclipse-temurin:21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY accounts-service/pom.xml accounts-service/
COPY cash-service/pom.xml cash-service/
COPY transfer-service/pom.xml transfer-service/
COPY notification-service/pom.xml notification-service/
COPY gateway-service/pom.xml gateway-service/
COPY front-ui/pom.xml front-ui/
COPY my-bank-starter/pom.xml my-bank-starter/

COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -pl transfer-service -am -B

COPY my-bank-starter/src my-bank-starter/src
COPY transfer-service/src transfer-service/src

RUN ./mvnw -pl transfer-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/transfer-service/target/*.jar transfer-service.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "transfer-service.jar"]
