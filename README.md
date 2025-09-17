# Multi-tenant Spring Boot Sample

Este proyecto demuestra una configuración multi-tenant utilizando Spring Boot, `PlatformTransactionManager`
con `JpaTransactionManager`, y repositorios de Spring Data para ejecutar consultas contra diferentes bases de datos.

## Cómo funciona

* **Datasource Routing**: `TenantRoutingDataSource` redirige las conexiones dependiendo del tenant establecido en `TenantContextHolder`.
* **Configuración por propiedades**: Cada base de datos se configura en `application.yml` bajo la llave `multitenancy.datasources`.
* **Transacciones**: Todas las operaciones usan `PlatformTransactionManager` para garantizar que cada operación se ejecute dentro de la transacción correcta del tenant.
* **Repositorios**: `EmployeeRepository` ilustra cómo declarar consultas con Spring Data JPA (`findByRole`).

## Probar la aplicación

1. Ejecuta la aplicación:
   ```bash
   mvn spring-boot:run
   ```
2. Invoca los endpoints usando el header `X-Tenant-ID` para seleccionar la base de datos:
   ```bash
   curl -H "X-Tenant-ID: tenant1" http://localhost:8080/api/employees
   curl -H "X-Tenant-ID: tenant2" http://localhost:8080/api/employees
   ```
3. Crea un empleado en un tenant específico:
   ```bash
   curl -X POST -H "Content-Type: application/json" \
        -H "X-Tenant-ID: tenant1" \
        -d '{"firstName":"Mario","lastName":"Rossi","role":"Consultant"}' \
        http://localhost:8080/api/employees
   ```

Cada tenant mantiene sus propios datos aislados dentro de su base de datos.
