# customers-service

Microservicio maestro de clientes de la plataforma bancaria Yanki. Única fuente de verdad sobre los clientes; otros servicios reciben cambios por eventos Kafka.

- **Puerto:** 8082
- **Base de datos:** MongoDB `yanki_customers` (colección `customers`)
- **Cache:** Redis (catálogos y consulta por documento)
- **Stack:** Java 17, Spring Boot 3.3.5, WebFlux, RxJava 3, Spring Kafka, Spring Data Redis Reactive
- **Integración:** Spring Cloud Config, Eureka Service Discovery, Resilience4j Circuit Breaker

## Build & Test

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.21.0.3"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
mvn clean verify
```

- Compila y ejecuta pruebas unitarias (JUnit 5 + Mockito + StepVerifier). Genera reporte JaCoCo en `target/site/jacoco/index.html` (excluyendo `interfaces/**`, `config/**` y `CustomersServiceApplication`).
- Para ejecutar el servicio: `mvn spring-boot:run` (requiere MongoDB, Redis y Kafka disponibles).

## Code Style

- **Nombres en inglés** (clases, métodos, URLs). **Javadoc en español.**
- **Lombok:** `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`, `@Slf4j`.
- **Inyección por constructor** con `@RequiredArgsConstructor`.
- **Checkstyle:** máximo 160 caracteres por línea, imports explícitos (sin `import ...*`), naming conventions Java.
- **Reactividad pura:** prohibido `.block()`, `.blockingGet()`, `.blockingFirst()`, `Thread.sleep()`.
- **Montos y dinero:** usar `Double` o `BigDecimal`, nunca `Float`.

## Architecture

Sigue Arquitectura Hexagonal. Consultar [`../system-prompt-auditoria-hexagonal-yanki.md`](../system-prompt-auditoria-hexagonal-yanki.md) para auditorías o refactorizaciones.

```
src/main/java/com/yanki/customers
├── domain
│   ├── model             # Customer (puro), enums, CustomerCatalogs, CustomerPage
│   ├── port
│   │   ├── in            # CreateCustomerUseCase, FindCustomerUseCase, ModifyCustomerUseCase
│   │   └── out           # CustomerPersistencePort, CustomerEventPublisherPort,
│   │                     # CustomerCachePort, TemporaryPasswordPort
│   ├── service           # CustomerService implementa puertos de entrada,
│   │                     # CustomerCommand, CustomerFilter, CustomerResult
│   ├── event             # CustomerRegisteredEvent, CustomerUpdatedEvent, CustomerDeletedEvent
│   └── exception         # BusinessException, ErrorCodes
├── infrastructure
│   ├── cache
│   │   └── adapter       # CustomerCacheAdapter (implementa CustomerCachePort → Redis)
│   ├── config            # CacheProperties, OpenApiConfig
│   ├── messaging
│   │   ├── adapter       # CustomerKafkaPublisherAdapter (implementa CustomerEventPublisherPort)
│   │   └── Topics
│   ├── persistence
│   │   ├── document      # CustomerDocument (@Document, @Id, @Indexed)
│   │   ├── repository    # SpringDataCustomerRepository (ReactiveMongoRepository)
│   │   ├── mapper        # CustomerDocumentMapper (domain ↔ document)
│   │   └── adapter       # CustomerMongoAdapter (implementa CustomerPersistencePort)
│   └── security
│       ├── TemporaryPasswordGenerator
│       └── adapter       # TemporaryPasswordAdapter (implementa TemporaryPasswordPort)
└── interfaces/rest
    ├── controller        # CustomerController (inyecta puertos de entrada)
    ├── dto               # Request/Response DTOs, mappers
    └── GlobalExceptionHandler
```

**Regla de dependencias:** `domain` no importa `infrastructure` ni `interfaces`. Las dependencias fluyen hacia adentro. El `domain.model.Customer` es 100% puro (sin `@Document`, `@Id`, `@Indexed`).

## Boundaries

- **Database-per-service:** solo accede a `yanki_customers`. Nunca toque BD de otros servicios.
- **Sin llamadas REST a otros microservicios.** La comunicación es por eventos Kafka.
- **Configuración externalizada:** cero hardcodeo de valores; todo por variables de entorno o Config Server.
- **Auth delegada:** la validación JWT la hace api-gateway. Este servicio recibe roles por header `X-User-Roles`.

## Kafka Topics (Outbound)

| Topic | Key | Payload |
|-------|-----|---------|
| `yanki.customer.registered.v1` | `customerId` | `CustomerRegisteredEvent(customerId, username, email, temporaryPassword, roles, customerType, status)` |
| `yanki.customer.updated.v1` | `customerId` | `CustomerUpdatedEvent(customerId, customerType, profile, status, occurredAt)` |
| `yanki.customer.deleted.v1` | `customerId` | `CustomerDeletedEvent(customerId, occurredAt)` |

Roles asignados: `ROLE_CUSTOMER` para todos y `ROLE_ENTERPRISE` adicional para clientes empresariales.

## Redis Cache

| Patrón de clave | TTL | Configurable |
|-----------------|-----|--------------|
| `yanki:customers:document:{TYPE}:{NUMBER}` | 10m | `yanki.cache.customer-ttl` |
| `yanki:customers:catalogs` | 30m | `yanki.cache.catalog-ttl` |

Invalidar explícitamente al hacer PUT/DELETE. Los TTL se configuran con `@ConfigurationProperties(prefix = "yanki.cache")`.

## Tests

| Test | Cobertura |
|------|-----------|
| `CustomerServiceTest` | Alta, documento duplicado, perfil inválido, filtros, actualización, baja lógica |
| `CustomerControllerTest` | 7 tests de endpoints REST con WebTestClient |
| `CustomerMongoAdapterTest` | 6 tests del adaptador MongoDB |
| `CustomerCacheAdapterTest` | 7 tests del adaptador Redis |
| `CustomerKafkaPublisherAdapterTest` | 3 tests del adaptador Kafka |
| `TemporaryPasswordGeneratorTest` | 2 tests de longitud y aleatoriedad |

## Traceability

- **Caso de Uso:** CU-01 (Gestión de Clientes)
- **Diagramas:** SEC001, SEC042
- **Casos de Prueba:** [test-cases/CASOS_PRUEBA.md](test-cases/CASOS_PRUEBA.md) (CP-001 a CP-019)
- **Colección Postman:** [postman/Yanki-Customers-Service.postman_collection.json](postman/Yanki-Customers-Service.postman_collection.json) (19 requests con test scripts)
- Ver [`../CASOS_PRUEBA/RTM.md`](../CASOS_PRUEBA/RTM.md) para la matriz completa.