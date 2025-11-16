# SmartPark API Testing Guide

This document provides cURL commands to test all API endpoints.

## Step 1: Authentication

First, obtain a JWT token:

```bash
curl -X POST http://localhost:8080/api/v1/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin"
}
```

**Save the token for subsequent requests. Replace `<TOKEN>` in the following commands with your actual token.**

---

## Step 2: Parking Lot Operations

### 2.1 Get All Parking Lots

```bash
curl -X GET http://localhost:8080/api/v1/parking-lots \
  -H "Authorization: Bearer <TOKEN>"
```

### 2.2 Get Parking Lot Status

```bash
curl -X GET http://localhost:8080/api/v1/parking-lots/LOT-001/status \
  -H "Authorization: Bearer <TOKEN>"
```

**Expected Response:**
```json
{
  "lotId": "LOT-001",
  "location": "Downtown Plaza",
  "capacity": 50,
  "occupiedSpaces": 0,
  "availableSpaces": 50
}
```

### 2.3 Register New Parking Lot

```bash
curl -X POST http://localhost:8080/api/v1/parking-lots \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "lotId": "LOT-004",
    "location": "City Center",
    "capacity": 75,
    "costPerMinute": 0.60
  }'
```

### 2.4 Get Vehicles in Parking Lot

```bash
curl -X GET http://localhost:8080/api/v1/parking-lots/LOT-001/vehicles \
  -H "Authorization: Bearer <TOKEN>"
```

---

## Step 3: Vehicle Operations

### 3.1 Get All Vehicles

```bash
curl -X GET http://localhost:8080/api/v1/vehicles \
  -H "Authorization: Bearer <TOKEN>"
```

### 3.2 Get Vehicle Details

```bash
curl -X GET http://localhost:8080/api/v1/vehicles/ABC-123 \
  -H "Authorization: Bearer <TOKEN>"
```

### 3.3 Register New Vehicle

```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "TEST-999",
    "type": "CAR",
    "ownerName": "Test User"
  }'
```

**Valid Vehicle Types:** `CAR`, `MOTORCYCLE`, `TRUCK`

---

## Step 4: Check-in and Check-out

### 4.1 Check-in Vehicle

```bash
curl -X POST http://localhost:8080/api/v1/vehicles/check-in \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "ABC-123",
    "lotId": "LOT-001"
  }'
```

**Expected Response:**
```json
{
  "licensePlate": "ABC-123",
  "type": "CAR",
  "ownerName": "John Doe",
  "parkingLot": {
    "lotId": "LOT-001",
    "location": "Downtown Plaza",
    ...
  },
  "checkInTime": "2025-11-16T21:30:00",
  "checkOutTime": null
}
```

### 4.2 Verify Parking Lot Status After Check-in

```bash
curl -X GET http://localhost:8080/api/v1/parking-lots/LOT-001/status \
  -H "Authorization: Bearer <TOKEN>"
```

**Expected Response:**
```json
{
  "lotId": "LOT-001",
  "location": "Downtown Plaza",
  "capacity": 50,
  "occupiedSpaces": 1,
  "availableSpaces": 49
}
```

### 4.3 Check-out Vehicle

```bash
curl -X POST http://localhost:8080/api/v1/vehicles/ABC-123/check-out \
  -H "Authorization: Bearer <TOKEN>"
```

**Expected Response:**
```json
{
  "licensePlate": "ABC-123",
  "lotId": "LOT-001",
  "checkInTime": "2025-11-16T21:30:00",
  "checkOutTime": "2025-11-16T21:35:00",
  "minutesParked": 5,
  "parkingCost": 2.50
}
```

---

## Step 5: Error Scenarios

### 5.1 Invalid Authentication

```bash
curl -X GET http://localhost:8080/api/v1/parking-lots \
  -H "Authorization: Bearer invalid_token"
```

**Expected:** 401 Unauthorized or 403 Forbidden

### 5.2 Check-in to Full Parking Lot

First, create a small parking lot:

```bash
curl -X POST http://localhost:8080/api/v1/parking-lots \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "lotId": "SMALL-LOT",
    "location": "Small Lot",
    "capacity": 1,
    "costPerMinute": 0.50
  }'
```

Check-in first vehicle:

```bash
curl -X POST http://localhost:8080/api/v1/vehicles/check-in \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "ABC-123",
    "lotId": "SMALL-LOT"
  }'
```

Try to check-in second vehicle (should fail):

```bash
curl -X POST http://localhost:8080/api/v1/vehicles/check-in \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "XYZ-789",
    "lotId": "SMALL-LOT"
  }'
```

**Expected:** 400 Bad Request with message "Parking lot is full"

### 5.3 Check-in Already Parked Vehicle

```bash
curl -X POST http://localhost:8080/api/v1/vehicles/check-in \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "ABC-123",
    "lotId": "LOT-002"
  }'
```

**Expected:** 400 Bad Request with message about vehicle already parked

### 5.4 Invalid License Plate Format

```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "INVALID@PLATE!",
    "type": "CAR",
    "ownerName": "Test User"
  }'
```

**Expected:** 400 Bad Request with validation error

### 5.5 Invalid Owner Name Format

```bash
curl -X POST http://localhost:8080/api/v1/vehicles \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "licensePlate": "VALID-123",
    "type": "CAR",
    "ownerName": "Test123"
  }'
```

**Expected:** 400 Bad Request with validation error

---

## Complete Test Sequence

Here's a complete test sequence you can run:

```bash
# 1. Login and save token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"

# 2. Get all parking lots
curl -X GET http://localhost:8080/api/v1/parking-lots \
  -H "Authorization: Bearer $TOKEN"

# 3. Check-in vehicle
curl -X POST http://localhost:8080/api/v1/vehicles/check-in \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"licensePlate":"ABC-123","lotId":"LOT-001"}'

# 4. Check parking lot status
curl -X GET http://localhost:8080/api/v1/parking-lots/LOT-001/status \
  -H "Authorization: Bearer $TOKEN"

# 5. Get vehicles in lot
curl -X GET http://localhost:8080/api/v1/parking-lots/LOT-001/vehicles \
  -H "Authorization: Bearer $TOKEN"

# 6. Wait a few seconds (optional)
sleep 5

# 7. Check-out vehicle
curl -X POST http://localhost:8080/api/v1/vehicles/ABC-123/check-out \
  -H "Authorization: Bearer $TOKEN"

# 8. Verify parking lot status updated
curl -X GET http://localhost:8080/api/v1/parking-lots/LOT-001/status \
  -H "Authorization: Bearer $TOKEN"
```

---

## Notes

- All timestamps are in ISO-8601 format
- Parking cost is calculated as: `minutes_parked * cost_per_minute`
- The scheduler runs every minute to remove vehicles parked longer than 15 minutes
- All endpoints except `/api/auth/login` require JWT authentication

