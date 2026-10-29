# GaonSeGhar

A production-style direct-from-village marketplace with Spring Boot 3, PostgreSQL, JWT authentication, Maven, React, TypeScript, and Vite.

## Architecture

- `backend/`: REST controllers, services, repositories, JPA entities, Flyway migrations, and stateless JWT security.
- `frontend/`: React marketplace UI and typed API client.
- PostgreSQL is the source of truth. Checkout uses a pessimistic write lock to validate and decrement stock atomically, preventing overselling.

## Prerequisites

Java 25 (LTS), Maven 3.9+, Node.js 18+, Docker Desktop or PostgreSQL 16.

## Run locally

```bash
docker compose up -d postgres
cd backend && mvn spring-boot:run
```

In another terminal:

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173; API: http://localhost:8080.

Configure non-development values with `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` and a restrictive `CORS_ORIGINS` list. The JWT secret should contain at least 32 characters.

## API examples

Register a seller:

```bash
curl -X POST http://localhost:8080/api/auth/register -H 'Content-Type: application/json' \
  -d '{"name":"Sita Devi","email":"sita@example.com","password":"strongpass123","village":"Rampur","role":"SELLER"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"email":"sita@example.com","password":"strongpass123"}'
```

Create a listing with the returned JWT:

```bash
curl -X POST http://localhost:8080/api/products -H 'Authorization: Bearer TOKEN' -H 'Content-Type: application/json' \
  -d '{"name":"Cold-pressed groundnut oil","description":"Stone-pressed in Rampur","category":"Food","price":350,"stock":20,"unit":"litre","village":"Rampur"}'
```

Browse and place an order:

```bash
curl 'http://localhost:8080/api/products?category=Food&q=oil&page=0&size=20'
curl -X POST http://localhost:8080/api/orders -H 'Authorization: Bearer TOKEN' -H 'Content-Type: application/json' \
  -d '{"items":[{"productId":1,"quantity":2}],"deliveryName":"Asha","deliveryPhone":"8888888888","deliveryAddress":"12 City Road, Pune"}'
```

## Tests

```bash
cd backend && mvn test
cd frontend && npm test
cd frontend && npm run build
```

## Security and operations

Passwords use BCrypt; JWT remains stateless and should be served over HTTPS in production. Configure ingress rate limiting, managed secrets, HTTPS, restrictive CORS, database least privilege, and audit logs. Never log passwords or tokens. This slice covers seller/customer authentication, listings, marketplace search, and transactional checkout; payments, fulfilment transitions, reviews, moderation, delivery tracking and notifications can extend the same modules.