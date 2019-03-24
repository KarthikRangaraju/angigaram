FROM openjdk:8-jdk-alpine
EXPOSE 8080
ADD /build/libs/angigaram-0.7.0-SNAPSHOT.jar angigaram.jar
ENTRYPOINT ["java", "-jar", "angigaram.jar"]