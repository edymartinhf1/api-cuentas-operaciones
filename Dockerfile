FROM openjdk:11.0.16
WORKDIR /app
COPY ./target/api-cuentas-operaciones-0.0.1-SNAPSHOT.jar .
EXPOSE 8084
ENTRYPOINT ["java","-jar","api-cuentas-operaciones-0.0.1-SNAPSHOT.jar"]


