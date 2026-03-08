FROM eclipse-temurin:21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY accounts-service/pom.xml accounts-service/
COPY cash-service/pom.xml cash-service/
COPY transfer-service/pom.xml transfer-service/
COPY notification-service/pom.xml notification-service/
COPY gateway-service/pom.xml gateway-service/
COPY front-ui/pom.xml front-ui/

COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -pl gateway-service -am -B

COPY gateway-service/src gateway-service/src

RUN ./mvnw -pl gateway-service -am clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/gateway-service/target/*.jar gateway-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "gateway-service.jar"]
