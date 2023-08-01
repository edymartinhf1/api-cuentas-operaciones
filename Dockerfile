FROM openjdk:17-jdk-alpine
RUN mkdir /app
WORKDIR /app
COPY target/*.jar /app/api-cuentas-operaciones-0.0.1-SNAPSHOT.jar
EXPOSE 8084
ENTRYPOINT ["java","-jar","api-cuentas-operaciones-0.0.1-SNAPSHOT.jar"]


