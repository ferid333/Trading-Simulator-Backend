# Trading Simulator Backend

Spring Boot REST backend for a university trading simulator project.

## Features

- User authentication with one portfolio per account
- User-defined starting balance in USD
- Five fixed stocks across all scenarios
- `LIVE`, `COVID`, and `FINANCIAL_CRISIS` scenarios
- Market `buy` and `sell` orders only
- Portfolio valuation and order history
- Session reset
- Historical scenarios advance one trading day at a time
- MySQL persistence
- Twelve Data integration for live and historical prices

## Default Stocks

- `AAPL`
- `MSFT`
- `AMZN`
- `GOOGL`
- `TSLA`

## REST Endpoints

Base path: `/api`

- `POST /auth/register`
- `GET /scenarios`
- `GET /stocks`
- `POST /portfolio`
- `GET /portfolio`
- `GET /prices`
- `GET /orders`
- `POST /orders/buy`
- `POST /orders/sell`
- `POST /portfolio/reset`
- `POST /portfolio/advance`

## Example

Register a user:

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "password": "password123"
}
```

Initialize the authenticated user's portfolio:

```http
POST /api/portfolio
Authorization: Basic base64(alice:password123)
Content-Type: application/json

{
  "scenarioCode": "COVID",
  "startingBalance": 10000
}
```

Buy shares:

```http
POST /api/orders/buy
Authorization: Basic base64(alice:password123)
Content-Type: application/json

{
  "symbol": "AAPL",
  "quantity": 3
}
```

Advance one historical trading day:

```http
POST /api/portfolio/advance
Authorization: Basic base64(alice:password123)
```

## Configuration

Environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `TWELVE_DATA_API_KEY`

Default database values in `application.yaml`:

- `jdbc:mysql://localhost:3306/trading_simulator`
- username `root`
- password `admin`

## Run

```powershell
.\gradlew.bat bootRun
```
