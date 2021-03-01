FROM maven:3.6.3-openjdk-11-slim AS builder
WORKDIR /webapp
COPY ./pom.xml .
RUN mvn dependency:go-offline -B --fail-never
COPY . ./
RUN mvn package -DskipTests && java -Djarmode=layertools -jar target/offline-lock-0.0.1-SNAPSHOT.jar extract

FROM openjdk:11-jre-slim-buster
WORKDIR /webapp
COPY --from=builder /webapp/dependencies/ ./
COPY --from=builder /webapp/spring-boot-loader ./
COPY --from=builder /webapp/snapshot-dependencies/ ./
COPY --from=builder /webapp/application/ ./
CMD ["java", "org.springframework.boot.loader.JarLauncher"]
