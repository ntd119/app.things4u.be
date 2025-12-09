FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/apinexo-0.0.1-SNAPSHOT.jar /app/apinexo.jar

EXPOSE 8080

CMD [ "java", "-jar", "apinexo.jar" ]