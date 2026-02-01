# =========================================
# STAGE 1: BUILD
# =========================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# 1. Layer Caching: Copy POM and download dependencies first
# This ensures that if you only change code, Docker doesn't re-download Maven libs
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Copy Source Code and Build
COPY src ./src
RUN mvn clean package -DskipTests

# =========================================
# STAGE 2: RUNTIME
# =========================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 3. Security: Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 4. Copy the JAR from builder
# We use a wildcard *.jar to grab the built artifact dynamically
# OR ensure you set <finalName>app</finalName> in pom.xml
COPY --from=builder /app/target/*.jar app.jar

# 5. Metadata
EXPOSE 8082

# 6. Run
ENTRYPOINT ["java", "-jar", "app.jar"]