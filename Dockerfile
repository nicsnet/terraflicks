FROM openjdk:8-alpine

COPY target/uberjar/terraflicks.jar /terraflicks/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/terraflicks/app.jar"]
