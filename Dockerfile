FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Install Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Copy Maven files
COPY pom.xml ./
COPY mvnw* ./
COPY .mvn .mvn/
RUN chmod +x mvnw || true

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# The JAR file is now in the current working directory's target folder
# Copy it to app.jar (this copies from the container's target directory)
RUN cp target/finance-tracker.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]