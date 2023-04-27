FROM maven:3.6.3-jdk-8-slim as build
COPY api /home/app/api
COPY ui /home/app/ui
COPY pom.xml /home/app
WORKDIR /home/app
RUN mvn clean install -DskipTests

FROM openjdk:8-jre-alpine
COPY --from=build /home/app/api/target/patient-ui-2.1-SNAPSHOT.jar /usr/local/lib/app.jar
EXPOSE 8443
ENTRYPOINT ["java","-jar","/usr/local/lib/app.jar"]
