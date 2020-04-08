# A multi-stage build Dockerfile to create an image for the Vert.x JPA example.
FROM maven:3-jdk-11 as builder

#Copy the source files into the Maven image and invoke the package goal.
COPY . /opt/app/src
WORKDIR /opt/app/src
RUN mvn package

# Copy the API node's fat JAR into the image.
FROM adoptopenjdk:11.0.6_10-jre-hotspot
WORKDIR /opt/app
COPY --from=builder /opt/app/src/api-node/target/api-node-1.2-fat.jar .

# Expose port TCP/8080 and set the command
EXPOSE 8080
CMD ["java","--add-modules","java.se","--add-exports","java.base/jdk.internal.ref=ALL-UNNAMED","--add-opens","java.base/java.lang=ALL-UNNAMED","--add-opens","java.base/java.nio=ALL-UNNAMED","--add-opens","java.base/sun.nio.ch=ALL-UNNAMED","--add-opens","java.management/sun.management=ALL-UNNAMED","--add-opens","jdk.management/com.sun.management.internal=ALL-UNNAMED","-jar","./api-node-1.2-fat.jar"]
