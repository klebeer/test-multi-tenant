# Repository Guidelines

## Project Overview
- Spring Boot 3 (Java 21) sample demonstrating multi-tenant routing with explicit read/write separation.
- Purpose: resolve the active tenant per request and route to the right data source while keeping transactions consistent.

## Architecture & Data Flow
- `TenantContextHolder` stores the tenant ID in a ThreadLocal; controllers set it from headers or claims and must clear it in finally blocks.
- `TenantRoutingDataSource` extends `AbstractRoutingDataSource`, pairing tenant key + operation mode to select the data source (`TenantConnectionKey`).
- `DataSourceConfiguration` loads tenant properties from `application.properties`, registers pools in `TenantConnectionRegistry`, and exposes one `PlatformTransactionManager`.
- Flow: context set → repository call triggers routing → transaction manager binds the `{tenant}-{mode}` connection for the method scope.

## Build & Runtime Commands
- `./gradlew clean build` compiles, tests, and packages the executable jar.
- `./gradlew test` runs the JUnit 5 suite; narrow scope with `--tests '*TenantRouting*'` when iterating.
- `./gradlew bootRun` launches the app locally with dynamic tenant loading.

## Coding Style & Layering
- Keep controller → service → repository boundaries and rely on constructor injection.
- Use four-space indentation, PascalCase types, camelCase members, and locate routing helpers under `config`.
- Apply Java 21 features sparingly: records for DTOs, sealed hierarchies for routing descriptors, switch expressions for data source selection.

## Transaction & Configuration Practices
- Annotate write paths with `@Transactional`; mark read flows as `@Transactional(readOnly = true)` to favour replicas.
- Let the shared `PlatformTransactionManager` manage connections—avoid manual resource handling to preserve tenant isolation.
- Onboard tenants by adding credentials to `application.properties`, confirming schema parity, and documenting replica tags or failover rules.

## Testing Guidelines
- Unit tests should assert context propagation and routing key construction per tenant.
- Integration tests (`@SpringBootTest`) must verify read/write routing, transaction boundaries, and rollback behaviour across tenants.
- Run `./gradlew test` before every PR and add coverage whenever routing or transaction semantics change.

## Commit & Pull Request Guidelines
- Use imperative commit subjects (e.g., `feat: add tenant failover strategy`) and reference issues where applicable.
- Keep PRs focused, describe tenant scenarios touched, and include curl samples or logs illustrating routing.
- Pair routing or configuration changes with tests or scripts that prove the expected behaviour.

## Next Steps & Observability
- Tune tenant-specific pool sizes and define replica failover strategies prior to production adoption.
- Add Micrometer or tracing tags (tenant, mode) to monitor routing accuracy, latency, and saturation.
