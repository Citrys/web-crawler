FROM openjdk:21

COPY /build/libs/crawler-web-crawler-1.0-SNAPSHOT-all.jar /app/app.jar

CMD ["java", "-jar", "/app/app.jar"]
