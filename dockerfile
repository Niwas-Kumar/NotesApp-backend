# Use OpenJDK 17
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn/ .mvn
COPY pom.xml .

# Copy source code
COPY src/ src/

# Give execute permission for mvnw
RUN chmod +x mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Expose the port Spring Boot uses
EXPOSE 8081

# Run the jar
CMD ["java", "-jar", "target/NotesApp-0.0.1-SNAPSHOT.jar"]
