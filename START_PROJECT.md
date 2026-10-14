# 🚀 TrustPay - Quick Start Guide

## Prerequisites
- Docker Desktop installed and running
- Node.js 18+ installed
- Java 17+ installed

---

## 🐳 Starting the Backend (Spring Boot + PostgreSQL)

### Step 1: Start Docker Desktop
1. Press **Windows Key**
2. Type "Docker Desktop"
3. Wait for Docker icon in system tray to stop animating (~30 seconds)

### Step 2: Start Backend Containers
```powershell
# Navigate to project root
cd C:\Users\Bharath\trustpay-backend

# Start containers in detached mode
docker-compose up -d

# Check if containers are running
docker ps

# View backend logs (optional)
docker logs trustpay-backend --tail 50
```

**Expected Output:**
- `trustpay-postgres` - Running (port 5432)
- `trustpay-backend` - Running (port 8081)

**Backend API:** http://localhost:8081/api/v1

---

## ⚛️ Starting the Frontend (React + Vite)

### Open a new terminal/PowerShell window:
```powershell
# Navigate to frontend folder
cd C:\Users\Bharath\trustpay-backend\trustpay-frontend

# Install dependencies (first time only)
npm install

# Start dev server
npm run dev
```

**Frontend URL:** http://localhost:5173

---

## 🧪 Testing the Application

### 1. Open Browser
Go to: http://localhost:5173

### 2. Test URL Scanner
Try these URLs:
- Safe: `https://www.google.com`
- Test: `http://suspicious-site.tk`

---

## 🛑 Stopping the Project

### Stop Frontend
Press `Ctrl + C` in the terminal running `npm run dev`

### Stop Backend
```powershell
cd C:\Users\Bharath\trustpay-backend
docker-compose down
```

---

## 🔧 Troubleshooting

### Backend not starting?
```powershell
# Check Docker is running
docker --version

# Rebuild containers
docker-compose down
docker-compose build
docker-compose up -d
```

### Frontend not starting?
```powershell
# Clear cache and reinstall
rm -r node_modules
npm install
npm run dev
```

### Port already in use?
```powershell
# Check what's using port 8081
netstat -ano | findstr :8081

# Check what's using port 5173
netstat -ano | findstr :5173
```

---

## 📦 Project Structure
```
trustpay-backend/
├── src/main/java/com/trustpay/     # Backend Java code
│   ├── controller/                  # REST API endpoints
│   ├── engine/                      # Security analysis engines
│   ├── config/                      # CORS, DB config
│   └── dto/                         # Data transfer objects
├── trustpay-frontend/               # React frontend
│   ├── src/
│   │   ├── components/              # React components
│   │   │   └── UrlScanner.jsx       # URL Scanner component
│   │   ├── services/                # API service layer
│   │   └── App.jsx                  # Main app component
│   └── package.json                 # Frontend dependencies
├── docker-compose.yml               # Docker setup
└── pom.xml                          # Backend dependencies
```

---

## 🎯 What's Working Now

✅ Backend API with Spring Boot
✅ PostgreSQL database
✅ React frontend with Vite
✅ URL Scanner with risk assessment
✅ CORS configured for frontend-backend communication
✅ Glassmorphism UI design

---

## 🔗 Important URLs

- Frontend: http://localhost:5173
- Backend API: http://localhost:8081/api/v1
- Health Check: http://localhost:8081/api/v1/health
- URL Scanner: POST http://localhost:8081/api/v1/url/scan

---

Last Updated: October 14, 2026
