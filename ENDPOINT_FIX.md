# 🔧 API Endpoint Fixes & Testing Guide

## ⚠️ ENDPOINT CHANGE

The admin login endpoint has been **refactored**. Use the new endpoint structure:

---

## ✅ Correct Endpoints

### **Admin Login**
```
POST /api/auth/admin/login
```

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

---

### **User Registration**
```
POST /api/auth/users/register
```

**Request Body:**
```json
{
  "name": "Virat Kohli",
  "email": "virat@example.com",
  "password": "password@123"
}
```

---

### **User Login**
```
POST /api/auth/users/login
```

**Request Body:**
```json
{
  "email": "virat@example.com",
  "password": "password@123"
}
```

---

### **Health Check**
```
GET /api/auth/health
```

---

## 📋 Complete API Reference

### Authentication Endpoints

| Endpoint | Method | Body | Description |
|----------|--------|------|-------------|
| `/api/auth/health` | GET | None | Health check |
| `/api/auth/admin/login` | POST | {username, password} | Admin login |
| `/api/auth/users/register` | POST | {name, email, password} | User registration |
| `/api/auth/users/login` | POST | {email, password} | User login |

### Admin Endpoints (Require JWT Token)

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/api/admin/update-credentials` | PUT | ✅ Bearer token | Update admin credentials |
| `/api/admin/players` | POST | ✅ Bearer token | Add player |
| `/api/admin/players/{id}` | PUT | ✅ Bearer token | Update player |
| `/api/admin/players/{id}` | DELETE | ✅ Bearer token | Delete player |
| `/api/admin/videos` | POST | ✅ Bearer token | Add video |
| `/api/admin/videos/{id}` | PUT | ✅ Bearer token | Update video |
| `/api/admin/videos/{id}` | DELETE | ✅ Bearer token | Delete video |

---

## 🚀 Testing Workflow

### Step 1: Health Check
```bash
curl -X GET http://localhost:8080/api/auth/health
```

**Expected Response:**
```
"Auth service is running"
```

---

### Step 2: Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ROLE_ADMIN",
  "userId": "507f1f77bcf86cd799439011",
  "message": "Admin login successful",
  "success": true
}
```

**👉 Save the `token` for next requests**

---

### Step 3: User Registration
```bash
curl -X POST http://localhost:8080/api/auth/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Virat Kohli",
    "email": "virat@example.com",
    "password": "password@123"
  }'
```

**Expected Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "virat@example.com",
  "role": "ROLE_USER",
  "userId": "507f1f77bcf86cd799439012",
  "message": "User registered successfully",
  "success": true
}
```

---

### Step 4: User Login
```bash
curl -X POST http://localhost:8080/api/auth/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "virat@example.com",
    "password": "password@123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "virat@example.com",
  "role": "ROLE_USER",
  "userId": "507f1f77bcf86cd799439012",
  "message": "User login successful",
  "success": true
}
```

---

### Step 5: Add Player (Admin Only)
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/api/admin/players \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Rohit Sharma",
    "country": "India",
    "jerseyNumber": 45,
    "role": "Batsman",
    "battingAverage": 48.5,
    "bowlingAverage": 0,
    "centuries": 31,
    "wickets": 0,
    "status": "Active"
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439013",
  "name": "Rohit Sharma",
  ...
}
```

---

## 📮 Postman Collection Update

### Updated Collection Endpoints:

**Authentication:**
- `POST /api/auth/admin/login` ← UPDATED
- `POST /api/auth/users/register` ← UPDATED
- `POST /api/auth/users/login` ← UPDATED
- `GET /api/auth/health`

**Admin Operations:**
- `PUT /api/admin/update-credentials`
- `POST /api/admin/players`
- `PUT /api/admin/players/{id}`
- `DELETE /api/admin/players/{id}`
- `POST /api/admin/videos`
- `PUT /api/admin/videos/{id}`
- `DELETE /api/admin/videos/{id}`

---

## ❌ Common Errors & Solutions

### Error: "Invalid username or password"

**Cause:** Admin user not found in database

**Solution:** 
1. Ensure application started (admin auto-created on startup)
2. MongoDB must be running
3. Check logs for any errors

---

### Error: 404 Not Found

**Cause:** Endpoint URL is wrong

**Solution:**
- Use `/api/auth/admin/login` NOT `/api/auth/login`
- Use `/api/auth/users/login` for user login (not `/api/auth/login`)

---

### Error: 400 Bad Request

**Cause:** Missing or invalid fields

**Solution:**
- Admin login requires: `username` and `password`
- User registration requires: `name`, `email`, `password`
- User login requires: `email` and `password`

---

### Error: 401 Unauthorized

**Cause:** Token missing or invalid

**Solution:**
- Add header: `Authorization: Bearer <token>`
- Token must be valid (not expired)

---

### Error: 403 Forbidden

**Cause:** User doesn't have admin role

**Solution:**
- Use admin token for admin endpoints
- Users (ROLE_USER) cannot access `/api/admin/` endpoints

---

## 🔍 Quick Troubleshooting

### Test Connection
```bash
curl http://localhost:8080/api/auth/health
```

### Verify Admin Created
```bash
mongosh
use crickinfo
db.users.findOne({ username: "admin" })
```

### Check Logs
```bash
# If using mvn spring-boot:run
# Watch console for errors
```

---

## 📝 Request Templates

### cURL - Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### cURL - User Register
```bash
curl -X POST http://localhost:8080/api/auth/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "user@example.com",
    "password": "pass@123"
  }'
```

### cURL - User Login
```bash
curl -X POST http://localhost:8080/api/auth/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "pass@123"
  }'
```

---

## ✅ Verification Checklist

- [ ] MongoDB is running
- [ ] Application is running (`mvn spring-boot:run`)
- [ ] Health check endpoint responds
- [ ] Admin login endpoint accessible
- [ ] JWT token generated on successful login
- [ ] Token can be used for protected endpoints

---

## 🎯 Default Credentials

```
Username: admin
Password: admin123
```

Login to: `POST /api/auth/admin/login`

---

**Version:** 2.0 (Updated)  
**Date:** April 19, 2026  
**Status:** All endpoints working ✅

