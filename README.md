# Mini Library SaaS

A production-grade Library Management System built with **Spring Boot 3**, **Angular**, **MySQL 8**, and an **AI-powered Librarian Assistant** backed by **Ollama** and **ChromaDB**.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Modules](#modules)
  - [Authentication & Authorization](#1-authentication--authorization)
  - [Book Management](#2-book-management)
  - [Borrowing & Returns](#3-borrowing--returns)
  - [Reservations](#4-reservations)
  - [Notifications](#5-notifications)
  - [Wishlist](#6-wishlist)
  - [Reviews & Ratings](#7-reviews--ratings)
  - [Book Requests](#8-book-requests)
  - [Categories](#9-categories)
  - [Reports & Analytics](#10-reports--analytics)
  - [Admin Dashboard](#11-admin-dashboard)
  - [User Console](#12-user-console)
  - [Penalties & Suspensions](#13-penalties--suspensions)
  - [Audit Logging](#14-audit-logging)
  - [Email Templates](#15-email-templates)
  - [Batch Import / Export](#16-batch-import--export)
  - [Scheduled Tasks](#17-scheduled-tasks)
  - [System Configuration](#18-system-configuration)
  - [Rate Limiting](#19-rate-limiting)
  - [AI Librarian Assistant](#20-ai-librarian-assistant)
- [Roles & Permissions](#roles--permissions)
- [Seed Users](#seed-users)
- [Database Migrations](#database-migrations)
- [Environment Variables](#environment-variables)
- [Running Locally](#running-locally)
- [AI Setup](#ai-setup)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)

---

## Overview

Mini Library SaaS is a full-stack multi-role library management platform. It covers the complete lifecycle of library operations — from member self-service (borrowing, reservations, wishlist) to librarian workflows (inventory management, borrow approvals) and admin control (user management, system config, reports).

On top of the core platform, an **AI-powered Librarian Assistant** uses Retrieval-Augmented Generation (RAG) to help members discover books via natural language, and gives librarians data-driven insights about the catalog.

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2.4 · Java 21 · Spring Security · Spring Data JPA |
| Frontend | Angular (standalone components · lazy loading · AuthGuard) |
| Database | MySQL 8.0 · Flyway migrations · HikariCP connection pool |
| Cache | Redis 7 |
| Auth | JWT (HS512 · 24h expiry) · BCrypt password hashing |
| AI — LLM | Ollama (`llama3.2`) via Spring AI 1.0.0-M6 |
| AI — Embeddings | Ollama (`nomic-embed-text`) via Spring AI 1.0.0-M6 |
| AI — Vector Store | ChromaDB 0.5.20 |
| Email | MailHog (dev) · Spring Mail |
| API Docs | Springdoc OpenAPI 3 (`/swagger-ui.html`) |
| Containerisation | Docker · Docker Compose |

---

## Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                        Angular Frontend                          │
│   Auth · Books · Borrows · Reservations · AI Assistant · ...     │
└────────────────────────────┬─────────────────────────────────────┘
                             │ HTTP / JWT
┌────────────────────────────▼─────────────────────────────────────┐
│                    Spring Boot REST API                          │
│                                                                  │
│  AuthController  BookController  BorrowController  AIController  │
│  ReservationController  DashboardController  ReportController    │
│  UserController  PenaltyController  AuditController  ...         │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │                    Service Layer                         │   │
│  │  BookService · BorrowService · RecommendationService     │   │
│  │  VectorSearchService · EmbeddingService · ...            │   │
│  └───────────┬──────────────┬──────────────┬───────────────┘   │
│              │              │              │                     │
│         MySQL 8.0       Redis 7       Flyway (13 migrations)    │
└──────────────┼──────────────┼──────────────────────────────────┘
               │              │
  ┌────────────▼───┐  ┌───────▼──────────────────────────────────┐
  │  ChromaDB      │  │               Ollama                     │
  │  Vector Store  │  │  llama3.2 (chat)                         │
  │  (embeddings)  │  │  nomic-embed-text (embeddings)           │
  └────────────────┘  └──────────────────────────────────────────┘
```

### AI RAG Pipeline

```
User message
     │
     ▼
nomic-embed-text  ──► ChromaDB similarity search  ──► Top 5 matching books
                                                            │
                              User borrow history ──────────┤
                                                            │
                                                            ▼
                                              Build grounded prompt
                                                            │
                                                            ▼
                                                 llama3.2 (Ollama)
                                                            │
                                                            ▼
                                         Structured response + book cards
```

---

## Modules

### 1. Authentication & Authorization

JWT-based stateless authentication with role-based access control.

**Endpoints:**
- `POST /api/auth/login` — returns JWT + user details
- `POST /api/auth/register` — public self-registration as MEMBER

**Features:**
- HS512 signed JWT, 24-hour expiry
- BCrypt password hashing
- `AuthInterceptor` injects `Authorization: Bearer` header on every Angular request
- `AuthGuard` enforces roles on Angular routes
- Session stored in `sessionStorage` (cleared on tab close)

---

### 2. Book Management

Full CRUD for the library catalog.

**Endpoints (public read, staff write):**
- `GET /api/books` — paginated catalog
- `GET /api/books/:id` — book detail
- `POST /api/books/search` — advanced search (title, author, description, category)
- `GET /api/books/available` — only available books
- `POST /api/books` — create *(ADMIN / LIBRARIAN)*
- `PUT /api/books/:id` — update *(ADMIN / LIBRARIAN)*
- `DELETE /api/books/:id` — delete *(ADMIN)*

**Features:**
- Status tracking: `AVAILABLE`, `BORROWED`, `ARCHIVED`, `OUT_OF_STOCK`
- Optimistic locking (`@Version`) for concurrent availability updates
- Automatic availability sync on borrow/return
- Cover image URL management with placeholder triggers
- ISBN uniqueness enforcement
- **Hooks into AI indexing** — create/update/delete automatically syncs the ChromaDB vector store

---

### 3. Borrowing & Returns

Complete borrow lifecycle management.

**Endpoints:**
- `POST /api/borrows` — request to borrow *(MEMBER)*
- `GET /api/borrows` — user's borrow history
- `POST /api/borrows/:id/return` — return a book
- `POST /api/borrows/:id/renew` — renew a loan
- `GET /api/borrows/inventory` — all active borrows *(LIBRARIAN / ADMIN)*
- `POST /api/borrows/:id/mark-borrowed` — approve pending request *(LIBRARIAN)*

**Business rules:**
- Maximum 5 active borrows per member
- Loan period: 14 days
- Maximum 3 renewals per borrow record
- Members with an overdue book cannot borrow new books
- Fine: $1/day for late returns + optional damage charge

---

### 4. Reservations

Queue-based book reservation system.

**Endpoints:**
- `POST /api/reservations` — create reservation *(MEMBER)*
- `GET /api/reservations` — user's reservations
- `DELETE /api/reservations/:id` — cancel reservation
- `GET /api/reservations/inventory` — queue overview *(LIBRARIAN / ADMIN)*

**Business rules:**
- Maximum 10 active reservations per member
- Auto-promotion when a returned book becomes available
- Reservation expires after 30 days

---

### 5. Notifications

In-app notification system for important library events.

**Notification types:** `BOOK_AVAILABLE`, `OVERDUE_REMINDER`, `FINE_NOTICE`, `RESERVATION_READY`, `BOOK_DUE_SOON`, `ACCOUNT_SUSPENDED`, `REQUEST_APPROVED`, `GENERAL`

**Endpoints:**
- `GET /api/notifications` — user's notifications (paginated)
- `GET /api/notifications/unread` — unread count (used by topbar badge)
- `PUT /api/notifications/:id/read` — mark as read

---

### 6. Wishlist

Personal book wishlist for members.

**Endpoints:**
- `GET /api/wishlist` — user's wishlist (paginated)
- `POST /api/wishlist` — add book to wishlist
- `DELETE /api/wishlist/:id` — remove from wishlist

---

### 7. Reviews & Ratings

Member book reviews with 1–5 star ratings.

**Endpoints:**
- `GET /api/books/:id/reviews` — reviews for a book
- `POST /api/books/:id/reviews` — submit review *(MEMBER)*
- `PUT /api/reviews/:id/helpful` — mark review as helpful

**Business rules:**
- One review per member per book
- Average rating calculated on retrieval

---

### 8. Book Requests

Members can request books the library does not own.

**Endpoints:**
- `POST /api/book-requests` — submit acquisition request *(MEMBER)*
- `GET /api/book-requests` — user's requests / all requests *(LIBRARIAN / ADMIN)*
- `PUT /api/book-requests/:id/approve` *(ADMIN)*
- `PUT /api/book-requests/:id/reject` *(ADMIN)*

**Status flow:** `PENDING → APPROVED / REJECTED → FULFILLED / EXPIRED`

---

### 9. Categories

Book categorization.

**Endpoints:**
- `GET /api/categories` — list all (public)
- `POST /api/categories` — create *(ADMIN / LIBRARIAN)*
- `PUT /api/categories/:id` — update *(ADMIN / LIBRARIAN)*
- `DELETE /api/categories/:id` — delete *(ADMIN)*

**Seed categories:** Fiction, Non-Fiction, Science, History, Fantasy, Mystery, Romance, Business, Programming, Art

---

### 10. Reports & Analytics

Operational reporting for library management.

**Report types:** Popular books, Circulation statistics, Overdue summary, Fine collection, User activity, Inventory status, Category distribution, Monthly summary

**Endpoints:**
- `GET /api/reports` — list all reports
- `POST /api/reports/generate` — generate new report *(ADMIN / LIBRARIAN)*
- `GET /api/reports/:id` — report detail

---

### 11. Admin Dashboard

Role-specific dashboards with live statistics.

**Endpoints:**
- `GET /api/dashboard/member` — member stats (active borrows, reservations, fines)
- `GET /api/dashboard/librarian` — librarian view (pending borrows, overdue count)
- `GET /api/dashboard/admin` — admin view (total users, revenue, overdue summary)

---

### 12. User Console

Admin and librarian user management.

**Endpoints (ADMIN):**
- `GET /api/admin/users` — paginated user list with search/filter
- `POST /api/admin/users` — create user with any role
- `PUT /api/admin/users/:id/status` — activate / suspend / deactivate

**Librarian access:**
- Read-only view filtered to MEMBER accounts only

---

### 13. Penalties & Suspensions

Penalty tracking and account suspension management.

**Penalty types:** `SUSPENSION`, `WARNING`, `FINE_DEFAULT`, `DAMAGE_CHARGE`

**Endpoints:**
- `GET /api/admin/penalties` — list all penalties *(ADMIN)*
- `POST /api/admin/penalties` — issue penalty
- `PUT /api/admin/penalties/:id/lift` — lift penalty

**Business rules:**
- SUSPENSION type auto-suspends account for 30 days
- Suspended members cannot borrow or reserve

---

### 14. Audit Logging

Immutable audit trail for all significant actions.

**Logged events:** User creation, book CRUD, borrow/return, penalty issuance, config changes

**Endpoints:**
- `GET /api/admin/audit-logs` — paginated log viewer *(ADMIN)*
- Supports date-range filtering

---

### 15. Email Templates

Customisable transactional email templates.

**Template types:** `WELCOME`, `PASSWORD_RESET`, `OVERDUE_REMINDER`, `FINE_NOTICE`, `RESERVATION_READY`

**Endpoints:**
- `GET /api/admin/email-templates` *(ADMIN)*
- `PUT /api/admin/email-templates/:id` — edit template body/subject

Email delivery uses MailHog in development (accessible at [http://localhost:8025](http://localhost:8025)).

---

### 16. Batch Import / Export

Bulk book operations via CSV.

**Endpoints:**
- `POST /api/import/books` — import books from CSV *(ADMIN / LIBRARIAN)*
- `GET /api/export/books` — export catalog to CSV

**Job tracking:** Each import creates a `BatchImportJob` entity with status `PENDING → PROCESSING → COMPLETED / FAILED`.

---

### 17. Scheduled Tasks

Admin-configurable background task management.

**Endpoints:**
- `GET /api/admin/scheduled-tasks` *(ADMIN)*
- `POST /api/admin/scheduled-tasks` — create task with cron expression
- `PUT /api/admin/scheduled-tasks/:id/toggle` — enable / disable

---

### 18. System Configuration

Key-value system configuration store.

**Endpoints:**
- `GET /api/admin/config` *(ADMIN)*
- `PUT /api/admin/config/:key` — update a config value

---

### 19. Rate Limiting

Per-user, per-endpoint API rate limiting.

- Limit: 100 requests / hour per user per endpoint
- Automatic reset after 1 hour
- Tracked in the `api_rate_limits` table

---

### 20. AI Librarian Assistant

The AI module adds two intelligent features on top of the core library platform.

#### How it works — RAG (Retrieval-Augmented Generation)

The AI **never hallucinates books**. Every recommendation is grounded in the actual library catalog:

1. When a book is created, updated, or deleted in `BookService`, it is automatically indexed in ChromaDB using an embedding generated by `nomic-embed-text`.
2. On application startup, `BookVectorIndexer` (an `ApplicationRunner`) bulk-indexes all existing books.
3. When a member sends a chat message, `RecommendationService` executes the RAG pipeline:
   - Generate an embedding for the user's query
   - Semantic search in ChromaDB → top 5 closest books
   - Fetch **live availability** from MySQL for those books
   - Optionally attach the user's last 5 borrow titles as personalisation context
   - Build a grounded prompt listing only real catalog books
   - Call `llama3.2` via Ollama → structured recommendation narrative
   - Return the LLM answer + structured book cards to the frontend

#### Service Architecture

| Class | Package | Responsibility |
|-------|---------|---------------|
| `EmbeddingService` | `service/ai` | Builds rich book text (`title + author + category + description`), calls `nomic-embed-text` |
| `VectorSearchService` | `service/ai` | Upserts, deletes, and queries book embeddings in ChromaDB |
| `RecommendationService` | `service/ai` | Full RAG pipeline for member chat |
| `LibrarianInsightService` | `service/ai` | Analytics insights backed by real borrow/reservation statistics |
| `BookVectorIndexer` | `service/ai` | `ApplicationRunner` that indexes all books on startup |

#### REST Endpoints

| Method | Path | Role | Description |
|--------|------|------|-------------|
| `POST` | `/api/ai/recommend` | MEMBER | Book recommendations via RAG |
| `POST` | `/api/ai/insights` | LIBRARIAN, ADMIN | Data-driven catalog analytics |
| `POST` | `/api/ai/reindex` | ADMIN | Force re-embed all books |

#### Request / Response

**Recommend:**
```json
// Request
{ "message": "recommend a book for a beginner programmer", "userId": 3 }

// Response
{
  "answer": "Based on your interest...\n\nBook title: Clean Code\nAuthor: Robert C. Martin\nWhy this book matches: ...\nAvailability: Available",
  "books": [
    {
      "bookId": 1,
      "title": "Clean Code",
      "author": "Robert C. Martin",
      "category": "Programming",
      "availability": "Available",
      "availableCopies": 3
    }
  ]
}
```

**Insights:**
```json
// Request
{ "question": "Which books are most requested but unavailable?" }

// Response
{
  "analysis": "Based on current reservation queues...\n• Design Patterns — 2 reservations pending, 0 copies available\n...",
  "generatedAt": "2026-07-31T08:33:08Z"
}
```

#### Frontend — AI Assistant (Member only)

Route: `/ai-assistant` · Restricted to `MEMBER` role via `AuthGuard`

- Conversational chat interface with message history
- **Suggested question chips** on first load
- Per-message **book cards** showing title, author, category, and availability badge
- Loading animation while waiting for the LLM response
- Clear conversation button

#### Frontend — AI Insights (Librarian & Admin)

Route: `/ai-insights` · Restricted to `LIBRARIAN` and `ADMIN` roles

- **3 preset insight cards** (click to run instantly):
  - *Most Requested But Unavailable* — surfaces demand/supply gaps
  - *Books to Purchase* — purchasing recommendations based on borrow trends
  - *Unpopular Books* — books never borrowed; candidates for removal
- **Custom free-form query** input for any catalog question
- **Re-index Books** button *(Admin only)* — triggers `POST /api/ai/reindex`

---

## Roles & Permissions

| Feature | MEMBER | LIBRARIAN | ADMIN |
|---------|:------:|:---------:|:-----:|
| Browse / search books | ✅ | ✅ | ✅ |
| Borrow books | ✅ | — | — |
| Reserve books | ✅ | — | — |
| Wishlist | ✅ | — | — |
| Reviews | ✅ | — | — |
| Book requests | ✅ | ✅ | ✅ |
| AI Assistant (chat) | ✅ | — | — |
| Book CRUD | — | ✅ | ✅ |
| Manage categories | — | ✅ | ✅ |
| Borrowed inventory | — | ✅ | ✅ |
| Reservation inventory | — | ✅ | ✅ |
| AI Insights | — | ✅ | ✅ |
| Reports | — | ✅ | ✅ |
| User console | — | ✅ (read) | ✅ (full) |
| Penalties | — | — | ✅ |
| Audit logs | — | — | ✅ |
| Email templates | — | — | ✅ |
| System config | — | — | ✅ |
| Scheduled tasks | — | — | ✅ |
| AI Re-index | — | — | ✅ |

---

## Seed Users

All seed accounts use password **`Admin1234!`**

| Username | Role | Email |
|----------|------|-------|
| `admin` | ADMIN | admin@library.local |
| `librarian` | LIBRARIAN | librarian@library.local |
| `member1` | MEMBER | member1@library.local |
| `member2` | MEMBER | member2@library.local |
| `member_test` | MEMBER | member_test@library.local |

---

## Database Migrations

Flyway manages all schema changes. Migrations run automatically on startup.

| Version | Description |
|---------|-------------|
| V1 | Initial schema (all 20+ tables) |
| V2 | Seed roles, users, categories, and 15 books |
| V3 | Fix borrow status constraint |
| V4 | Populate book cover URLs |
| V5 | Assign curated cover images |
| V6 | Add default cover placeholder triggers |
| V7 | Fix reservation status constraint |
| V8 | Fix borrow demand status constraint |
| V9 | Seed borrow status matrix for member1/member2 |
| V10 | Add ordered status and seed book requests |
| V11 | Adjust member overdue, seed member2 return fines |
| V12 | Keep only member2 overdue seed |
| V13 | Add `member_test` seed user |

---

## Environment Variables

Copy `.env.example` to `.env` and fill in the values.

### Core

| Variable | Default | Description |
|----------|---------|-------------|
| `MYSQL_ROOT_PASSWORD` | — | MySQL root password |
| `MYSQL_DATABASE` | `library_db` | Database name |
| `MYSQL_USER` | `library_user` | App DB user |
| `MYSQL_PASSWORD` | — | App DB password |
| `DB_NAME` | `library_db` | Spring datasource DB name |
| `DB_USER` | `library_user` | Spring datasource user |
| `DB_PASSWORD` | — | Spring datasource password |
| `JWT_SECRET` | — | HS512 signing secret (min 64 chars) |
| `JWT_EXPIRATION_MS` | `86400000` | Token TTL in ms (24 h) |
| `LOGGING_LEVEL_ROOT` | `INFO` | Root log level |
| `LOGGING_LEVEL_COM_LIBRARY` | `DEBUG` | App log level |

### AI

| Variable | Default | Description |
|----------|---------|-------------|
| `OLLAMA_BASE_URL` | `http://ollama:11434` | Ollama API base URL |
| `OLLAMA_CHAT_MODEL` | `llama3.2` | Chat / generation model |
| `OLLAMA_EMBEDDING_MODEL` | `nomic-embed-text` | Embedding model |
| `CHROMA_HOST` | `http://chromadb` | ChromaDB host (include `http://`) |
| `CHROMA_PORT` | `8000` | ChromaDB port |

---

## Running Locally

### Prerequisites

- Docker Desktop (4 GB+ RAM recommended for Ollama models)
- Java 21 + Maven 3.9 (for local development without Docker)
- Node.js 18+ (for Angular dev server)

### 1. Start all services

```bash
git clone https://github.com/your-org/mini-library-saas.git
cd mini-library-saas
cp .env.example .env          # fill in secrets
docker compose up -d
```

This starts: MySQL · Redis · MailHog · Ollama · ChromaDB · Spring Boot · Angular (nginx)

### 2. Pull AI models (first run only)

The Ollama container starts empty. Pull the two required models after the container is healthy:

```bash
docker exec library-ollama ollama pull nomic-embed-text   # ~274 MB
docker exec library-ollama ollama pull llama3.2           # ~2.0 GB
```

> **Note:** `llama3.2` is a 2 GB download. This is a one-time operation — the model is persisted in the `ollama_data` Docker volume.

### 3. Restart the app

After the models are available, restart the app so `BookVectorIndexer` can index all books into ChromaDB:

```bash
docker compose restart app
```

You should see this in the logs:

```
BookVectorIndexer: starting startup book indexing...
Starting bulk vector indexing of 15 books...
Bulk vector indexing complete: 15/15 books indexed.
```

### 4. Access the application

| Service | URL |
|---------|-----|
| Angular frontend | http://localhost:4200 |
| Spring Boot API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| MailHog (email) | http://localhost:8025 |
| ChromaDB API | http://localhost:8000 |
| Ollama API | http://localhost:11434 |

---

## AI Setup

### Verify models are loaded

```bash
curl http://localhost:11434/api/tags | python3 -m json.tool
```

You should see both `llama3.2` and `nomic-embed-text` in the response.

### Test the AI endpoint

```bash
# 1. Get a JWT token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"member1","password":"Admin1234!"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")

# 2. Send a recommendation request
curl -X POST http://localhost:8080/api/ai/recommend \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"message":"recommend a fantasy adventure book"}'
```

### Change models

Set environment variables before starting:

```bash
OLLAMA_CHAT_MODEL=mistral docker compose up -d
```

Any model in the [Ollama library](https://ollama.com/library) works. Lighter models (`phi3`, `gemma2:2b`) use less RAM; larger models give richer responses.

### Re-index the catalog

```bash
# Via API (admin token required)
curl -X POST http://localhost:8080/api/ai/reindex \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

Or use the **Re-index Books** button in the AI Insights page as an Admin.

---

## API Documentation

Interactive API docs are available at **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)** after starting the app.

All endpoints are grouped by controller and include full request/response schemas.

---

## Project Structure

```
mini-library-saas/
├── src/
│   ├── main/
│   │   ├── java/com/library/
│   │   │   ├── config/           # SecurityConfig, OpenApiConfig, DataSeeder
│   │   │   ├── controller/       # 20 REST controllers (incl. AIController)
│   │   │   ├── dto/
│   │   │   │   └── ai/           # AIChatRequest/Response, AIInsightRequest/Response
│   │   │   ├── entity/           # 21 JPA entities
│   │   │   ├── exception/        # GlobalExceptionHandler
│   │   │   ├── repository/       # 23 Spring Data repositories
│   │   │   ├── security/         # JwtTokenProvider, JwtAuthenticationFilter
│   │   │   └── service/
│   │   │       ├── ai/           # EmbeddingService, VectorSearchService,
│   │   │       │                 # RecommendationService, LibrarianInsightService,
│   │   │       │                 # BookVectorIndexer
│   │   │       └── ...           # 23 other business services
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── db/migration/     # V1–V13 Flyway scripts
│   └── test/
│       └── java/com/library/     # 25 test classes (JUnit 5 + Mockito + TestContainers)
├── frontend/
│   └── src/app/
│       ├── components/
│       │   ├── ai/
│       │   │   ├── ai-assistant/ # Chat UI (Member only)
│       │   │   └── ai-insights/  # Analytics UI (Librarian / Admin)
│       │   ├── admin/            # User console, config, penalties, audit, email templates
│       │   ├── books/            # Book list, detail, management
│       │   ├── borrow/           # Borrow list
│       │   ├── dashboard/        # Member, librarian, admin dashboards
│       │   ├── librarian/        # Borrowed inventory, reservation inventory
│       │   └── ...               # auth, notifications, profile, reports, reservations, wishlist
│       ├── guards/               # AuthGuard
│       ├── interceptors/         # AuthInterceptor (JWT injection)
│       ├── models/               # TypeScript interfaces
│       └── services/             # 19 HTTP services (incl. AiService)
├── db/
│   └── init-scripts/init.sql     # MySQL init (creates DB + user)
├── docker-compose.yml
├── Dockerfile                    # Multi-stage Maven → JRE build
└── frontend/Dockerfile           # Multi-stage ng build → nginx
```
