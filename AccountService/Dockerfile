# =========================================
# STAGE 1: BUILD (Compiling the Java Code)
# =========================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

# 2. Download dependencies
RUN mvn dependency:go-offline

# 3. Copy the Source Code
COPY src ./src

# 4. Build the JAR (Skip tests to speed up container build; tests should run in CI)
RUN mvn clean package -DskipTests

# =========================================
# STAGE 2: RUNTIME (Running the Application)
# =========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app


RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 6. Copy the JAR from the builder stage
# We rename it to 'app.jar' so we don't care about version numbers (0.0.1-SNAPSHOT)
COPY --from=builder /app/target/*.jar app.jar

# 7. Expose the port (Documentation only)
EXPOSE 8081

# 8. Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]