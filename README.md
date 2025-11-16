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

## Prerequisites

- Java 17 or higher
- Maven (or use the included Maven Wrapper)

## Build Instructions

### Using Maven Wrapper (Recommended)

**Windows:**

```bash
mvnw.cmd clean install
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

### Using System Maven

```bash
mvn spring-boot:run
```

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
