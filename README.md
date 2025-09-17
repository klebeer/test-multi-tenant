# Multi-tenant Spring Boot Sample

This project demonstrates a multi-tenant configuration built with Spring Boot. It configures tenant-specific datasources through `application.yml`, routes requests using a `TenantRoutingDataSource`, and performs database operations through Spring Data repositories wrapped in transactions managed by `PlatformTransactionManager`.

## How it works

* **Datasource routing**: `TenantRoutingDataSource` switches the active datasource according to the tenant stored in `TenantContextHolder`.
* **Configuration via properties**: Each tenant datasource is declared under the `multitenancy.datasources` prefix inside `application.yml`.
* **Transactions**: All operations leverage `PlatformTransactionManager` to ensure that each tenant interaction runs inside the correct transaction boundary.
* **Repositories**: `EmployeeRepository` shows how to declare CRUD operations and custom queries (such as `findByRole`) using Spring Data JPA.

## Try it out

1. Start the application:
   ```bash
   mvn spring-boot:run
   ```
2. Call the endpoints by providing the `X-Tenant-ID` header to choose the datasource:
   ```bash
   curl -H "X-Tenant-ID: tenant1" http://localhost:8080/api/employees
   curl -H "X-Tenant-ID: tenant2" http://localhost:8080/api/employees
   ```
3. Create an employee for a specific tenant:
   ```bash
   curl -X POST -H "Content-Type: application/json" \
        -H "X-Tenant-ID: tenant1" \
        -d '{"firstName":"Mario","lastName":"Rossi","role":"Consultant"}' \
        http://localhost:8080/api/employees
   ```

Each tenant keeps its data fully isolated within its own database.
