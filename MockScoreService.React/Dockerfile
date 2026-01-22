# Build stage
FROM node:20-alpine as build

WORKDIR /app

# Copy package files
COPY package*.json ./
RUN npm ci

# Copy source code
COPY . .

# Build arguments for environment variables
ARG VITE_API_URL=http://mock-football-api:5000
ARG VITE_WEB_API_URL=http://mock-football-web:5001

# Set environment variables for build
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_WEB_API_URL=$VITE_WEB_API_URL

# Build the application
RUN npm run build

# Production stage
FROM nginx:alpine

# Copy built files
COPY --from=build /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]

