FROM ubuntu:latest
LABEL authors="SHERFEDINOV KEMAL"
WORKDIR /app
COPY dispatcher/target/dispatcher-0.0.1.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]