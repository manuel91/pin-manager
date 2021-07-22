FROM adoptopenjdk/openjdk11:alpine-jre
EXPOSE 8080
ARG JAR_FILE=target/pin-manager-1.0-SNAPSHOT.jar
ADD ${JAR_FILE} pin-manager-app.jar
ENTRYPOINT ["java","-jar","pin-manager-app.jar"]