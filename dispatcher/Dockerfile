FROM ghcr.io/graalvm/graalvm-community:21
LABEL authors="SHERFEDINOV KEMAL"
WORKDIR /app
COPY target/dispatcher-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]