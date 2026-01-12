# InventoryX - Stock Management System

Modern Stock Management System built with Domain-Driven Design (DDD) principles, designed to scale into a full E-Commerce platform.

## 🏗️ Architecture

This project implements **Domain-Driven Design** with **Clean Architecture** principles, featuring:

- **Bounded Contexts**: Inventory, Product Catalog, Warehouse Management
- **Event-Driven Communication**: Ready for future E-Commerce integration
- **Microservices-Ready**: Each domain can be deployed independently
- **CQRS Pattern**: Optimized read/write models

## 🛠️ Tech Stack

### Backend

- **Spring Boot 4.0.1** (Java 25)
- **PostgreSQL 16** - Database
- **Flyway** - Database migrations
- **Spring Security** - Authentication & Authorization
- **Lombok** - Reduce boilerplate code

### Frontend

- **Nuxt 4.2.2** (Vue 3.5.26)
- **Pinia** - State management
- **TypeScript** - Type safety
- **VueUse** - Utility composables
- **Axios** - HTTP client

### Infrastructure

- **Docker** - Containerization
- **PostgreSQL** - Primary database

## 📁 Project Structure

```
inventoryx/
├── inventoryx-service/          # Backend (Spring Boot)
│   └── src/main/java/com/stockmanagement/inventory/
│       ├── domain/              # Domain Layer (Aggregates, Value Objects)
│       ├── application/         # Application Layer (Use Cases)
│       ├── infrastructure/      # Infrastructure Layer (Persistence, Config)
│       └── presentation/        # Presentation Layer (REST Controllers)
│
└── inventoryx-web/              # Frontend (Nuxt 4)
    └── app/
        ├── components/          # Vue Components (Domain-aligned)
        ├── composables/         # Composables & API clients
        ├── stores/              # Pinia Stores (State Management)
        ├── pages/               # Pages (Auto-routing)
        └── types/               # TypeScript Types
```

## 🚀 Getting Started

### Prerequisites

- **Java 25+**
- **Node.js 18+**
- **Docker** (for PostgreSQL)
- **Maven 3.9+**
- **npm 9+**

### Backend Setup

1. **Start PostgreSQL**

```bash
docker run --name postgres-inventoryx \
  -e POSTGRES_DB=stockmanagement \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

2. **Run Backend**

```bash
cd inventoryx-service
./mvnw spring-boot:run
```

Backend will be available at: `http://localhost:8081/api/v1`

### Frontend Setup

1. **Install Dependencies**

```bash
cd inventoryx-web
npm install
```

2. **Run Development Server**

```bash
npm run dev
```

Frontend will be available at: `http://localhost:3000`

## 📊 Current Status

### ✅ Completed

- [x] Project architecture design (DDD + Clean Architecture)
- [x] Backend project setup (Spring Boot 4 + Java 25)
- [x] Frontend project setup (Nuxt 4 + Vue 3)
- [x] Database schema design (Flyway migrations)
- [x] Project structure (Domain-aligned)
- [x] Configuration files (Backend & Frontend)
- [x] Design system (Google Antigravity-inspired)

### 🚧 In Progress

- [ ] Domain Models implementation (Aggregates, Value Objects)
- [ ] Use Cases implementation (Application Services)
- [ ] REST API endpoints (Controllers)
- [ ] Frontend components (UI)
- [ ] API integration (Frontend ↔ Backend)

### 📋 Planned

- [ ] Unit & Integration tests
- [ ] Product Catalog Service
- [ ] Warehouse Management Service
- [ ] Order Management (E-Commerce)
- [ ] Customer Management (E-Commerce)
- [ ] Payment & Shipping integration

## 🎯 Features (Planned)

### Phase 1: Stock Management (MVP)

- Stock level tracking
- Stock movements (Receipt, Transfer, Adjustment)
- Multi-location inventory
- Real-time stock updates
- Movement history & audit trail

### Phase 2: E-Commerce Foundation

- Order management
- Stock reservation
- Customer management
- Event-driven integration

### Phase 3: Payment & Fulfillment

- Payment processing
- Shipping integration
- Order fulfillment workflow
- Saga orchestration

## 📚 Documentation

Detailed documentation available in `/docs` folder:

- Architecture Design
- Implementation Plan
- API Documentation
- Setup Guides

## 🤝 Contributing

This is a personal project. Contributions, issues, and feature requests are welcome!

## 📝 License

This project is licensed under the MIT License.

## 👨‍💻 Author

**Weerayut Kiettiphisansakun**

- GitHub: [@JackkWRY](https://github.com/JackkWRY)

---

**Built with ❤️ using Domain-Driven Design principles**
