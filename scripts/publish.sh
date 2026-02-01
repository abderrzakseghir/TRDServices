#!/bin/bash

# 1. Check input
SERVICE_NAME=$1
DOCKER_USER="bahaaidmc"

if [ -z "$SERVICE_NAME" ]; then
  echo "❌ Error: Please specify the service folder name."
  echo "Usage: ./publish.sh <folder-name>"
  echo "Example: ./publish.sh wallet-service"
  exit 1
fi

if [ ! -d "$SERVICE_NAME" ]; then
  echo "❌ Error: Folder '$SERVICE_NAME' does not exist."
  exit 1
fi

echo "============================================"
echo "🚀 PROCESS STARTING FOR: $SERVICE_NAME"
echo "============================================"

# 2. Go into the folder
cd $SERVICE_NAME

# 3. Compile Java (Skip Tests for speed)
echo "☕ Compiling JAR..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
  echo "❌ Maven Build Failed."
  exit 1
fi

# 4. Build Docker Image
# Logic: "AccountService" -> "account-service", "Gateway" -> "gateway"
IMAGE_NAME=$(echo "$SERVICE_FOLDER" | sed -r 's/([a-z0-9])([A-Z])/\1-\2/g' | tr '[:upper:]' '[:lower:]')
IMAGE_TAG="$DOCKER_USER/$IMAGE_NAME:latest"

echo "🐳 Building Docker Image: $IMAGE_TAG..."
docker build -t $IMAGE_TAG .

# 5. Push to Hub
echo "☁️  Pushing to Docker Hub..."
docker push $IMAGE_TAG

echo "✅ SUCCESS! Image $IMAGE_TAG is live."
