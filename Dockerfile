FROM openjdk:11
COPY ./build/libs/*.jar app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "/app.jar"]