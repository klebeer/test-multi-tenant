# Dynamic Data Source Routing Demo

This Spring Boot 3 example demonstrates multi-tenancy combined with read/write data source separation using H2 in-memory databases. Each tenant is provisioned with an independent pair of data sources: a writable primary and a read-only replica used for query operations.

## Requirements

- Java 21
- Gradle 8+

## Running the application

```bash
./gradlew bootRun
```

The REST API exposes tenant aware endpoints under `/api/{tenant}/customers`. For example, to create a customer for `tenant1`:

```bash
curl -X POST http://localhost:8080/api/tenant1/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice"}'
```

Fetching the customers for `tenant2` hits a different data source:

```bash
curl http://localhost:8080/api/tenant2/customers
```

## Tests

Run the integration tests with:

```bash
./gradlew test
```

The `CustomerServiceIntegrationTest` verifies that the routing data source switches both tenants and read/write connections on the fly.
