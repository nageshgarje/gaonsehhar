FROM eclipse-temurin:25-jre AS runtime
WORKDIR /app
COPY backend/target/gaonsehhar-api.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75","-jar","app.jar"]
