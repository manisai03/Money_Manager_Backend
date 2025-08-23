FROM eclipse-temurin:24-jre
WORKDIR /app
COPY target/money_manager-0.0.1-SNAPSHOT.jar money_manager-v1.0.jar
EXPOSE 9090
ENTRYPOINT["java","-jar","moneymanager-v1.0.jar"]