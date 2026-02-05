# InventoryX

> Modern Stock Management System built with **Domain-Driven Design** and **Clean Architecture**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-green)](https://spring.io/projects/spring-boot)
[![Nuxt](https://img.shields.io/badge/Nuxt-4.2.2-00DC82)](https://nuxt.com)
[![Java](https://img.shields.io/badge/Java-25-orange)](https://openjdk.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## ✨ Features

- 📦 **Stock Management** - Track inventory levels across multiple locations
- 🛒 **Reservations** - Reserve, release, and confirm stock for orders
- 💵 **Quick Sale (POS)** - Instant sales for retail counters
- 📊 **Dashboard** - KPI cards, low stock alerts, recent movements
- 👤 **User Management** - Role-based access control (Admin, User)
- 🌙 **Dark Mode** - Toggle between light/dark themes
- ⌨️ **Keyboard Shortcuts** - `/` to search, `Esc` to close
- 🌐 **i18n** - English & Thai language support
- 📱 **Responsive** - Works on desktop and mobile

---

## 🛠️ Tech Stack

| Layer    | Technology                          |
| -------- | ----------------------------------- |
| Backend  | Spring Boot 4.0.1, Java 25, JPA     |
| Frontend | Nuxt 4, Vue 3, Pinia, TypeScript    |
| Database | PostgreSQL 16, Flyway Migrations    |
| Security | JWT Authentication, Spring Security |

---

## 🚀 Quick Start

### Prerequisites

- Java 25+
- Node.js 18+ (with npm)
- Docker (for PostgreSQL)
- Maven 3.9+ (or use included `mvnw`)

### 1. Clone the Repository

```bash
git clone https://github.com/JackkWRY/inventory-x.git
cd inventory-x
```

### 2. Start Database

```bash
docker run --name postgres-inventoryx \
  -e POSTGRES_DB=stockmanagement \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

### 3. Run Backend

```bash
cd inventoryx-service
./mvnw spring-boot:run
```

> **Backend API:** http://localhost:8081/api/v1

### 4. Run Frontend

```bash
cd inventoryx-web
npm install
npm run dev
```

> **Frontend App:** http://localhost:3000

### Default Credentials

| Role  | Username | Password |
| ----- | -------- | -------- |
| Admin | `admin`  | `admin`  |
| User  | `user`   | `user`   |

---

## 🔧 Environment Variables

### Backend (`inventoryx-service/src/main/resources/application.properties`)

| Variable                | Description                  | Default                                            |
| ----------------------- | ---------------------------- | -------------------------------------------------- |
| `SPRING_DATASOURCE_URL` | PostgreSQL connection string | `jdbc:postgresql://localhost:5432/stockmanagement` |
| `JWT_SECRET`            | Secret key for JWT signing   | (configured in properties)                         |

### Frontend (`inventoryx-web/.env`)

| Variable       | Description          | Default                        |
| -------------- | -------------------- | ------------------------------ |
| `API_BASE_URL` | Backend API base URL | `http://localhost:8081/api/v1` |

---

## 📁 Project Structure

```
inventoryx/
│
├── inventoryx-service/         # Spring Boot Backend (DDD/Hexagonal)
│   └── src/main/java/
│       ├── domain/             # Aggregates, Value Objects, Events
│       ├── application/        # Use Cases, DTOs, Mappers
│       ├── infrastructure/     # Repositories, Security, Persistence
│       └── presentation/       # REST Controllers, Exception Handlers
│
└── inventoryx-web/             # Nuxt 4 Frontend
    └── app/
        ├── components/         # Vue Components (Common, Inventory, etc.)
        ├── composables/        # Reusable Logic (useTheme, useListFilter)
        │   └── api/            # Service Layer (useInventoryApi, etc.)
        ├── stores/             # Pinia State Management
        ├── pages/              # File-based Routing
        ├── layouts/            # App Layouts (default.vue)
        └── config/             # App Configuration (navigation.ts)
```

---

## 📊 API Endpoints

### Stock Operations

| Method | Endpoint                 | Description             |
| ------ | ------------------------ | ----------------------- |
| GET    | `/stocks`                | List stocks (paginated) |
| GET    | `/stocks/{id}`           | Get stock details       |
| POST   | `/stocks/receive`        | Receive stock           |
| POST   | `/stocks/reserve`        | Reserve stock for order |
| POST   | `/stocks/release`        | Release reservation     |
| POST   | `/stocks/confirm`        | Confirm reservation     |
| POST   | `/stocks/adjust`         | Adjust stock quantity   |
| POST   | `/stocks/withdraw`       | Withdraw stock          |
| POST   | `/stocks/sale`           | Quick sale (POS)        |
| GET    | `/stocks/{id}/movements` | Get movement history    |

### Products, Locations, Users

| Method | Endpoint             | Description              |
| ------ | -------------------- | ------------------------ |
| GET    | `/products`          | List products            |
| POST   | `/products`          | Create product           |
| PUT    | `/products/{id}`     | Update product           |
| GET    | `/locations`         | List locations           |
| POST   | `/locations`         | Create location          |
| PUT    | `/locations/{id}`    | Update location          |
| GET    | `/users`             | List users               |
| POST   | `/users`             | Create user              |
| PUT    | `/users/{id}`        | Update user              |
| PATCH  | `/users/{id}/toggle` | Toggle user active state |

### Dashboard

| Method | Endpoint     | Description           |
| ------ | ------------ | --------------------- |
| GET    | `/dashboard` | Get dashboard summary |

---

## ⌨️ Keyboard Shortcuts

| Key   | Action               |
| ----- | -------------------- |
| `/`   | Focus search bar     |
| `n`   | Open new item dialog |
| `Esc` | Close dialogs/modals |

---

## 🧪 Running Tests

### Backend

```bash
cd inventoryx-service
./mvnw test
```

### Frontend

```bash
cd inventoryx-web
npm run test  # (if configured)
```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

[MIT](LICENSE)

---

## 👨‍💻 Author

**Weerayut Kiettiphisansakun** - [@JackkWRY](https://github.com/JackkWRY)
