# Mudda Enhanced Load Testing

This directory contains improved load testing scripts for the Mudda backend, targeting critical controllers: `IssueController`, `CommentController`, `AuthController`, and `AccountController`.

## Structure

- `config.js`: Central configuration for URLs, thresholds, and test stages.
- `users.csv`: Test data for authenticating unique users.
- `utils/`:
  - `auth.js`: Logic for Login, Register, Refresh, and Logout.
  - `requests.js`: Helper for common headers (Auth Bearer, Client-Type).
- `scenarios/`:
  - `issue_browsing.js`: High-volume read operations (Listing, Search, Dashboard, Clusters).
  - `user_flow.js`: Complete interaction life-cycle (Login -> Profile -> Create Issue -> Comment -> Like).
- `main.js`: Combined test runner that orchestrates multiple scenarios simultaneously.

## Prerequisites

- [k6](https://k6.io/docs/getting-started/installation/) installed locally.
- Backend service running (default: `http://localhost:8080`).

## Running Tests

### 1. Run Everything (Realistic Simulation)
```bash
k6 run load_tests/main.js
```

### 2. Specific Scenarios
To focus on specific types of load:

**Browsing Performance:**
```bash
k6 run load_tests/scenarios/issue_browsing.js
```

**Interaction & Account Flow:**
```bash
k6 run load_tests/scenarios/user_flow.js
```

### 3. Customizing Parameters
Override the base URL or increase load via environment variables or flags:
```bash
k6 run -e BASE_URL=https://api.test.mudda.com --vus 50 --duration 5m load_tests/main.js
```

## Thresholds
Scripts are configured with standard performance guards:
- **Success Rate:** > 99% (Error rate < 1%)
- **Response Time:** 95% of requests must be under 500ms.
