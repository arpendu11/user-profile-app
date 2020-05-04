FROM openjdk:8-jdk-alpine

WORKDIR /opt/app

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
COPY target/classes /app

ENV REDIS_HOST="localhost" \
    REDIS_PORT="6379"
    
EXPOSE 9090

ENTRYPOINT ["java","-jar","app.jar"]