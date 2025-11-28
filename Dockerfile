FROM openjdk:26-ea-17-jdk-slim
ARG JAR_FILE=out/artifacts/homework2_jar/homework2.jar
ADD ${JAR_FILE} homework2.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/homework2.jar"]
