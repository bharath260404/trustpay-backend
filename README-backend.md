# 🛡️ TrustPay Backend - Spring Boot API

> RESTful API for AI-Powered UPI Fraud Prevention Suite

This repository contains the backend API for TrustPay - a security analysis engine that detects phishing URLs, fraudulent QR codes, and suspicious transaction patterns.

---

## 🏗️ Backend Architecture

```
Spring Boot (Port 8081) → PostgreSQL (Port 5432)
         ↓
REST Controllers → Security Engines → Database
```

---

## 🛠️ Tech Stack

- **Framework:** Spring Boot 3.2.3
- **Language:** Java 17
- **Build Tool:** Maven
- **Database:** PostgreSQL 16
- **ORM:** Spring Data JPA / Hibernate
- **Security:** CORS, Spring Security
- **Containerization:** Docker + Docker Compose

---

## 📁 Project Structure

```
src/main/java/com/trustpay/
├── TrustpayBackendApplication.java    # Main application
├── controller/
│   ├── UrlScannerController.java      # URL analysis endpoint
│   └── ...
├── engine/
│   ├── UrlScannerEngine.java          # Security analysis logic
│   └── ...
├── config/
│   └── CorsConfig.java                 # CORS configuration
├── dto/
│   ├── UrlScanRequest.java             # Request DTO
│   └── UrlScanResponse.java            # Response DTO
└── ...

src/main/resources/
└── application-h2.yml                  # Database config
```

---

## 🔗 API Endpoints

**Base URL:** `http://localhost:8081/api/v1`

### Health Check
```
GET /health
```
Response:
```json
{
  "status": "UP",
  "service": "TrustPay Backend Engine",
  "databaseConnected": true,
  "recordedThreatEntities": 2
}
```

### URL Scanner
```
POST /url/scan
Body: { "url": "https://example.com" }
```
Response:
```json
{
  "inputUrl": "https://example.com",
  "parsedDomain": "example.com",
  "scheme": "https",
  "riskLevel": "SAFE",
  "riskScore": 0.0,
  "riskFactors": [],
  "warningMessage": null
}
```

---

## 🚀 Quick Start (Backend Only)

```bash
# 1. Start Docker containers
docker-compose up -d

# 2. Check backend is running
docker ps

# 3. Test health endpoint
curl http://localhost:8081/api/v1/health

# 4. Test URL scanner
curl -X POST http://localhost:8081/api/v1/url/scan \
  -H "Content-Type: application/json" \
  -d '{"url":"https://google.com"}'
```

---

## 🐳 Docker Setup

```yaml
# docker-compose.yml
services:
  backend:
    build: .
    ports:
      - "8081:8080"
    depends_on:
      - postgres
  postgres:
    image: postgres:16
    environment:
      POSTGRES_DB: trustpay
    ports:
      - "5432:5432"
```

---

## 🔧 Key Features

- ✅ RESTful API with Spring Boot 3.2
- ✅ PostgreSQL database integration
- ✅ CORS configuration for frontend (localhost:5173)
- ✅ URL threat analysis engine
- ✅ Docker containerization
- ✅ Health check endpoint
- ✅ Structured DTO patterns
- ✅ Error handling with consistent responses

---

## 📊 Status

- **Completed:** Backend API, Database, Docker Setup, CORS, URL Scanner
- **In Progress:** Enhanced URL analysis, Content inspection
- **Repository:** [Private - Available upon request]

---

## 📞 Contact

Developer: Bharath  
GitHub: https://github.com/bharath260404  
Project: TrustPay - Full-Stack Security Suite
