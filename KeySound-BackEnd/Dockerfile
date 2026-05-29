FROM eclipse-temurin:21-jdk-jammy
COPY target/*.jar app.jar
COPY src/main/resources/firebase-account.json firebase-account.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]