# SmartPark - Intelligent Parking Management System

A Spring Boot REST API application for managing parking lots and vehicles with JWT authentication.

## Features

- JWT-based authentication
- Parking lot registration and management
- Vehicle registration
- Check-in/Check-out functionality with cost calculation
- Real-time parking lot occupancy tracking
- Automatic removal of vehicles parked longer than 15 minutes
- H2 in-memory database with pre-loaded data
- Comprehensive input validation
- Global exception handling

## Technology Stack

- Java 17
- Spring Boot 3.4.0
- Spring Security with JWT
- Spring Data JPA
- H2 Database
- Lombok
- Maven

## Prerequisites

- Java 17 or higher
- Maven (or use the included Maven Wrapper)

## Build Instructions

### Using Maven Wrapper (Recommended)

**Windows:**
```bash
mvnw.cmd clean install
```

**Linux/Mac:**
```bash
./mvnw clean install
```

### Using System Maven

```bash
mvn clean install
```

## Run Instructions

### Using Maven Wrapper

**Windows:**
```bash
mvnw.cmd spring-boot:run
```

**Linux/Mac:**
```bash
./mvnw spring-boot:run
```

### Using System Maven

```bash
mvn spring-boot:run
```

### Using JAR file

```bash
java -jar target/smartpark-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## Default Credentials

- **Username:** admin
- **Password:** admin123

## Pre-loaded Data

The application comes with pre-loaded data:

### Parking Lots
- LOT-001 (Downtown Plaza) - Capacity: 50, Cost: $0.50/min
- LOT-002 (Shopping Mall) - Capacity: 100, Cost: $0.75/min
- LOT-003 (Airport Terminal) - Capacity: 200, Cost: $1.00/min

### Vehicles
- ABC-123 (Car - John Doe)
- XYZ-789 (Motorcycle - Jane Smith)
- TRK-456 (Truck - Bob Johnson)
- CAR-001 (Car - Alice Williams)
- MOTO-999 (Motorcycle - Charlie Brown)

## API Endpoints

### Authentication

#### Login
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin"
}
```

### Parking Lots

All endpoints require JWT token in Authorization header: `Bearer <token>`

#### Register Parking Lot
```
POST /api/v1/parking-lots
Authorization: Bearer <token>
Content-Type: application/json

{
  "lotId": "LOT-004",
  "location": "City Center",
  "capacity": 75,
  "costPerMinute": 0.60
}
```

#### Get Parking Lot Status
```
GET /api/v1/parking-lots/{lotId}/status
Authorization: Bearer <token>
```

#### Get All Vehicles in Lot
```
GET /api/v1/parking-lots/{lotId}/vehicles
Authorization: Bearer <token>
```

#### Get All Parking Lots
```
GET /api/v1/parking-lots
Authorization: Bearer <token>
```

### Vehicles

#### Register Vehicle
```
POST /api/v1/vehicles
Authorization: Bearer <token>
Content-Type: application/json

{
  "licensePlate": "NEW-123",
  "type": "CAR",
  "ownerName": "John Smith"
}
```

Vehicle types: `CAR`, `MOTORCYCLE`, `TRUCK`

#### Check-in Vehicle
```
POST /api/v1/vehicles/check-in
Authorization: Bearer <token>
Content-Type: application/json

{
  "licensePlate": "ABC-123",
  "lotId": "LOT-001"
}
```

#### Check-out Vehicle
```
POST /api/v1/vehicles/{licensePlate}/check-out
Authorization: Bearer <token>
```

Response includes parking cost calculation.

#### Get Vehicle Details
```
GET /api/v1/vehicles/{licensePlate}
Authorization: Bearer <token>
```

#### Get All Vehicles
```
GET /api/v1/vehicles
Authorization: Bearer <token>
```

## Testing with cURL

See `API_TESTS.md` for complete cURL examples.

## H2 Database Console

Access the H2 console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:smartparkdb`
- Username: `sa`
- Password: (leave empty)

## Business Rules

1. Vehicles cannot be parked in full parking lots
2. A vehicle can only be parked in one lot at a time
3. Parking cost is calculated based on minutes parked
4. Vehicles parked longer than 15 minutes are automatically removed (runs every minute)
5. License plates can only contain letters, numbers, and dashes
6. Owner names can only contain letters and spaces

## Project Structure

```
src/main/java/com/hitachi/smartpark/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Custom exceptions and handlers
├── repository/      # JPA repositories
├── scheduler/       # Scheduled tasks
├── security/        # Security and JWT configuration
└── service/         # Business logic
```

## Error Handling

The API returns appropriate HTTP status codes and error messages:
- 200: Success
- 201: Created
- 400: Bad Request (validation errors, business rule violations)
- 401: Unauthorized (invalid credentials)
- 404: Not Found
- 409: Conflict (resource already exists)
- 500: Internal Server Error

