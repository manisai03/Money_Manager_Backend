FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/money_manager-0.0.1-SNAPSHOT.jar money_manager-0.0.1-jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","money_manager-0.0.1-jar"]