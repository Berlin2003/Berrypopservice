FROM openjdk:21-jdk-slim

WORKDIR /app

# Reduce image size using multi-stage build (optional)
COPY --chown=1000:1000 target/demo-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5001

# Improve performance by using memory optimization
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
