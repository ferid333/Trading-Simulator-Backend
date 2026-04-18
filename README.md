# Trading Simulator Backend

Spring Boot REST backend for a university trading simulator project.

## Features

- Anonymous simulation sessions
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

- `GET /scenarios`
- `GET /stocks`
- `POST /sessions`
- `GET /sessions/{sessionId}/portfolio`
- `GET /sessions/{sessionId}/prices`
- `GET /sessions/{sessionId}/orders`
- `POST /sessions/{sessionId}/orders/buy`
- `POST /sessions/{sessionId}/orders/sell`
- `POST /sessions/{sessionId}/reset`
- `POST /sessions/{sessionId}/advance`

## Example

Create a session:

```http
POST /api/sessions
Content-Type: application/json

{
  "scenarioCode": "COVID",
  "startingBalance": 10000
}
```

Buy shares:

```http
POST /api/sessions/{sessionId}/orders/buy
Content-Type: application/json

{
  "symbol": "AAPL",
  "quantity": 3
}
```

Advance one historical trading day:

```http
POST /api/sessions/{sessionId}/advance
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
