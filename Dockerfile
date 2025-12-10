FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/apinexo-0.0.1.jar /app/apinexo.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]