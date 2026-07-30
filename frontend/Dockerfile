# Multi-stage Dockerfile for Angular Frontend
# Stage 1: Build
FROM node:20-alpine AS builder

WORKDIR /app

# Copy package files from frontend directory
COPY frontend/package*.json ./

# Install dependencies
RUN npm install

# Copy source code and config files
COPY frontend/src ./src
COPY frontend/angular.json .
COPY frontend/tsconfig.json .
COPY frontend/tsconfig.app.json .

# Build the Angular app
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Copy custom nginx config
COPY frontend/nginx.conf /etc/nginx/nginx.conf

# Copy built app from builder stage
COPY --from=builder /app/dist/mini-library-saas-frontend /usr/share/nginx/html

# Expose port
EXPOSE 4200

# Start nginx
CMD ["nginx", "-g", "daemon off;"]
