FROM openjdk:24-jdk
WORKDIR /app
COPY target/money_manager-0.0.1-SNAPSHOT.jar money_manager-0.0.1-jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","money_manager-0.0.1-jar"]
