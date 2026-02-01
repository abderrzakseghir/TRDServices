#!/bin/bash

SERVICE_NAME=$1

# 1. Check input
if [ -z "$SERVICE_NAME" ]; then
  echo "❌ Error: Specify the service name defined in docker-compose.yml"
  echo "Usage: ./refresh.sh <service-name> (or 'all')"
  exit 1
fi

echo "============================================"
echo "🔄 REFRESHING CONTAINER: $SERVICE_NAME"
echo "============================================"

if [ "$SERVICE_NAME" == "all" ]; then
    # Nuclear Option: Wipe everything and restart
    echo "⚠️  Stopping ALL containers..."
    docker-compose down
    echo "🚀 Starting ALL containers..."
    docker-compose up -d
else


    if [ "$SERVICE_NAME" == "postgres" ] || [ "$SERVICE_NAME" == "keycloak" ]; then
        echo "⚠️  Warning: You are refreshing infrastructure. This might affect other services."
    fi

    echo "⬇️  Pulling latest image (if applicable)..."
    docker-compose pull $SERVICE_NAME

    echo "♻️  Recreating container..."
    docker-compose up -d --force-recreate --no-deps $SERVICE_NAME
fi

echo "✅ DONE. Tailing logs for 10 seconds..."
timeout 10s docker logs -f "trd_$SERVICE_NAME" 2>/dev/null || docker logs -f "$SERVICE_NAME"

### 4. The "Ultimate" Workflow ⚡

Now your development loop looks like this:

1  **Code:** Make changes in `wallet-service` IntelliJ.
2  **Publish:**
    ```bash
    ./scripts/publish.sh wallet-service
    ```
3  **Refresh:**
    ```bash
    ./refresh.sh wallet-service
    ```