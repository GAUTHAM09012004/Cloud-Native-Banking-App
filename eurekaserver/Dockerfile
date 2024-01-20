FROM openjdk:17-jdk-slim

EXPOSE 8080

MAINTAINER Gautham

COPY target/eurekaserver-0.0.1-SNAPSHOT.jar eurekaserver-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","eurekaserver-0.0.1-SNAPSHOT.jar"]