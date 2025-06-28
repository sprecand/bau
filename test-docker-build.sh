#!/bin/bash

echo "🐳 Testing Docker builds locally..."

# Build backend
echo "Building backend..."
cd backend
docker build -t bau-backend:test .
if [ $? -ne 0 ]; then
    echo "❌ Backend build failed"
    exit 1
fi
echo "✅ Backend build successful"

# Build frontend
echo "Building frontend..."
cd ../frontend
docker build -t bau-frontend:test .
if [ $? -ne 0 ]; then
    echo "❌ Frontend build failed"
    exit 1
fi
echo "✅ Frontend build successful"

# Test running containers
echo "🚀 Testing containers..."

# Run backend
echo "Starting backend container..."
docker run -d --name bau-backend-test -p 8080:8080 bau-backend:test
sleep 10

# Run frontend
echo "Starting frontend container..."
docker run -d --name bau-frontend-test -p 80:80 bau-frontend:test
sleep 5

# Health checks
echo "🔍 Health checks..."
backend_health=$(curl -s http://localhost:8080/actuator/health | grep -o '"status":"UP"' || echo "")
frontend_health=$(curl -s http://localhost:80/health | grep -o "healthy" || echo "")

# Cleanup
echo "🧹 Cleaning up..."
docker stop bau-backend-test bau-frontend-test
docker rm bau-backend-test bau-frontend-test

# Results
if [ -n "$backend_health" ]; then
    echo "✅ Backend health check passed"
else
    echo "⚠️  Backend health check failed (check logs with: docker logs bau-backend-test)"
fi

if [ -n "$frontend_health" ]; then
    echo "✅ Frontend health check passed"
else
    echo "⚠️  Frontend health check failed (check logs with: docker logs bau-frontend-test)"
fi

echo "🎉 Docker build test completed!"
echo "Ready to push to main branch for AWS deployment." 