# InventoryX

> Modern Stock Management System built with **Domain-Driven Design** and **Clean Architecture**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-green)](https://spring.io/projects/spring-boot)
[![Nuxt](https://img.shields.io/badge/Nuxt-4.2.2-00DC82)](https://nuxt.com)
[![Java](https://img.shields.io/badge/Java-25-orange)](https://openjdk.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.x-blue)](https://www.typescriptlang.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

## ✨ Features

- 📦 **Stock Management** - Track inventory levels across locations
- 📊 **Dashboard** - KPI cards, low stock alerts, recent movements
- 🔍 **Search & Filter** - Find stocks by SKU, location, status
- 🌙 **Dark Mode** - Toggle between light/dark themes
- ⌨️ **Keyboard Shortcuts** - `/` to search, `Esc` to close
- 🌐 **i18n** - English & Thai language support
- 📱 **Responsive** - Works on desktop and mobile

## 🛠️ Tech Stack

| Layer    | Technology                       |
| -------- | -------------------------------- |
| Backend  | Spring Boot 4.0.1, Java 25, JPA  |
| Frontend | Nuxt 4, Vue 3, Pinia, TypeScript |
| Database | PostgreSQL 16, Flyway migrations |

## 🚀 Quick Start

### Prerequisites

- Java 25+
- Node.js 18+
- Docker
- Maven 3.9+

### 1. Start Database

```bash
docker run --name postgres-inventoryx \
  -e POSTGRES_DB=stockmanagement \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:16
```

### 2. Run Backend

```bash
cd inventoryx-service
./mvnw spring-boot:run
```

> Backend: http://localhost:8081/api/v1

### 3. Run Frontend

```bash
cd inventoryx-web
npm install
npm run dev
```

> Frontend: http://localhost:3000

## 📁 Project Structure

```
inventoryx/
├── inventoryx-service/     # Spring Boot Backend
│   └── src/main/java/.../
│       ├── domain/         # Aggregates, Value Objects
│       ├── application/    # Use Cases, Commands
│       ├── infrastructure/ # Repositories, Config
│       └── presentation/   # REST Controllers
│
└── inventoryx-web/         # Nuxt 4 Frontend
    └── app/
        ├── components/     # Vue Components
        ├── composables/    # useTheme, useKeyboardShortcuts
        ├── stores/         # Pinia State Management
        └── pages/          # Auto-routing Pages
```

## 📊 API Endpoints

| Method | Endpoint                 | Description         |
| ------ | ------------------------ | ------------------- |
| GET    | `/stocks`                | List stocks (paged) |
| GET    | `/stocks/{id}`           | Get stock details   |
| POST   | `/stocks/receive`        | Receive stock       |
| POST   | `/stocks/{id}/reserve`   | Reserve stock       |
| POST   | `/stocks/{id}/release`   | Release reservation |
| POST   | `/stocks/{id}/confirm`   | Confirm reservation |
| POST   | `/stocks/{id}/adjust`    | Adjust stock        |
| GET    | `/stocks/{id}/movements` | Movement history    |

## ⌨️ Keyboard Shortcuts

| Key   | Action        |
| ----- | ------------- |
| `/`   | Focus search  |
| `Esc` | Close dialogs |

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## 📝 License

[MIT](LICENSE)

## 👨‍💻 Author

**Weerayut Kiettiphisansakun** - [@JackkWRY](https://github.com/JackkWRY)
