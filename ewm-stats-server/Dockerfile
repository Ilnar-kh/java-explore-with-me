FROM eclipse-temurin:21-jre

ARG JAR_FILE=target/ewm-stats-server-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 9090
ENTRYPOINT ["java","-jar","/app.jar"]
