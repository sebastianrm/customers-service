# customers-service

Microservicio **maestro de clientes** de la plataforma bancaria **Yanki**.

Es la **única fuente de verdad** sobre los clientes de la plataforma. Ningún otro microservicio
consulta su base de datos: los demás servicios se enteran de los cambios mediante **eventos Kafka**.

- **Puerto:** `8082`
- **Base de datos propia:** MongoDB `yanki_customers` (colección `customers`)
- **Cache:** Redis (catálogos maestros y consulta por documento)
- **Stack:** Java 17, Spring Boot 3.3.5, Spring WebFlux, Spring Data Reactive (MongoDB + Redis),
  RxJava 3, Spring Kafka, springdoc-openapi

---

## 1. Funcionalidades

Derivadas de las Partes I, II y III del proyecto general (`Proyecto General vf.md`):

### 1.1 Parte I — Bases del sistema bancario

| # | Funcionalidad | Origen |
|---|---------------|--------|
| F1 | **Alta de clientes personales** con nombre, apellido, fecha de nacimiento, documento (DNI/CEX/Pasaporte/RUC), correo, teléfono, dirección | Parte I §Funcionalidades |
| F2 | **Alta de clientes empresariales** con razón social, documento, correo, teléfono, dirección | Parte I §Funcionalidades |
| F3 | **CRUD completo** de clientes: Create, FindAll (con filtros y paginación), Update, Delete (baja lógica) | Parte I §Requerimientos no funcionales |
| F4 | **Documento único** como identificador principal del cliente en toda la plataforma | Parte I §Funcionalidades |
| F5 | **Catálogos maestros**: tipos de cliente (PERSONAL/ENTERPRISE), tipos de documento (DNI/CEX/PASSPORT/RUC), estados (ACTIVE/BLOCKED/DELETED) | Parte I §Funcionalidades |

### 1.2 Parte II — Perfiles VIP/PYME

| # | Funcionalidad | Origen |
|---|---------------|--------|
| F6 | **Perfil VIP** para clientes PERSONAL: cuenta de ahorro con monto mínimo de promedio diario; requiere tarjeta de crédito previa | Parte II §Funcionalidades |
| F7 | **Perfil PYME** para clientes ENTERPRISE: cuenta corriente sin comisión de mantenimiento; requiere tarjeta de crédito previa | Parte II §Funcionalidades |
| F8 | **Perfil STANDARD** válido para ambos tipos de cliente | Parte II §Funcionalidades |
| F9 | **Validación de perfil según tipo**: VIP solo PERSONAL, PYME solo ENTERPRISE, STANDARD para ambos | Parte II §Funcionalidades |

### 1.3 Parte III — Eventos, autenticación y monedero

| # | Funcionalidad | Origen |
|---|---------------|--------|
| F10 | **Publicación de eventos Kafka** para notificar cambios a otros microservicios (sin invocación REST entre servicios) | Parte III §Bases a Desarrollar |
| F11 | **Generación de credencial temporal** al registrar un cliente (contraseña aleatoria de 12 caracteres) para que auth-service cree las credenciales | Parte III §Funcionalidades |
| F12 | **Catálogos acelerados con Redis** para acceso rápido a datos maestros | Parte III §Bases a Desarrollar |
| F13 | **Soporte para monedero móvil Yanki**: el registro de monedero solo requiere documento, celular, IMEI y correo (sin necesidad de ser cliente del banco) — customers-service valida la existencia del documento | Parte III §Funcionalidades |

### 1.4 Responsabilidades operativas

| Responsabilidad | Detalle |
|---|---|
| Maestro de clientes | Fuente única de datos de cliente (tipo, perfil, estado, documento) |
| Baja lógica | Marca `status = DELETED` sin borrar físicamente el documento |
| Catálogos maestros | Expone tipos de cliente, perfiles y tipos de documento desde Redis |
| Eventos | Publica los cambios para que el resto de servicios actualice sus vistas locales |

---

## 2. Endpoints REST

Base: `/api/customers`

| Metodo | Ruta | Descripcion | Respuesta |
|---|---|---|---|
| `POST` | `/api/customers` | Registra un cliente | `201` `CustomerCreatedResponse` |
| `GET` | `/api/customers` | Lista clientes con filtros y paginacion | `200` `PageResponse<CustomerResponse>` |
| `GET` | `/api/customers/{id}` | Consulta un cliente por identificador | `200` `CustomerResponse` |
| `PUT` | `/api/customers/{id}` | Actualiza un cliente | `200` `CustomerResponse` |
| `DELETE` | `/api/customers/{id}` | Elimina logicamente un cliente | `204` |
| `GET` | `/api/customers/document/{documentType}/{documentNumber}` | Consulta por documento (cache Redis) | `200` `CustomerResponse` |
| `GET` | `/api/customers/catalogs` | Catalogos maestros (cache Redis) | `200` `CustomerCatalogsResponse` |

### 2.1 Filtros y paginacion de `GET /api/customers`

| Parametro | Tipo | Default | Descripcion |
|---|---|---|---|
| `type` | `PERSONAL` \| `ENTERPRISE` | - | Filtra por tipo de cliente |
| `profile` | `STANDARD` \| `VIP` \| `PYME` | - | Filtra por perfil |
| `status` | `ACTIVE` \| `BLOCKED` \| `DELETED` | - | Filtra por estado |
| `page` | `int` | `0` | Numero de pagina (base cero) |
| `size` | `int` | `20` | Elementos por pagina (maximo `100`) |

La respuesta usa un `PageResponse<T>` propio (`content`, `page`, `size`, `totalElements`, `totalPages`).
No se expone `org.springframework.data.domain.Page` en la API publica.

### 2.2 Ejemplos

```bash
# Alta de cliente personal
curl -X POST http://localhost:8082/api/customers \
  -H "Content-Type: application/json" \
  -d '{
        "type": "PERSONAL",
        "profile": "STANDARD",
        "documentType": "DNI",
        "documentNumber": "70123456",
        "firstName": "Juan",
        "lastName": "Perez",
        "email": "juan.perez@correo.com",
        "phone": "+51987654321",
        "address": "Av. Javier Prado Este 4200",
        "birthDate": "1990-05-20"
      }'
```

```json
{
  "id": "65f1c2a4e8b9d31234567890",
  "type": "PERSONAL",
  "profile": "STANDARD",
  "status": "ACTIVE",
  "documentType": "DNI",
  "documentNumber": "70123456",
  "firstName": "Juan",
  "lastName": "Perez",
  "email": "juan.perez@correo.com",
  "phone": "+51987654321",
  "address": "Av. Javier Prado Este 4200",
  "birthDate": "1990-05-20",
  "active": true,
  "createdAt": "2026-11-04T10:15:30",
  "updatedAt": "2026-11-04T10:15:30",
  "temporaryPassword": "aB3xY9zQ1mNp"
}
```

```bash
# Listado filtrado
curl "http://localhost:8082/api/customers?type=ENTERPRISE&profile=PYME&status=ACTIVE&page=0&size=10"

# Consulta por documento (sirve desde Redis)
curl http://localhost:8082/api/customers/document/DNI/70123456

# Catalogos maestros (sirve desde Redis)
curl http://localhost:8082/api/customers/catalogs

# Baja logica
curl -X DELETE http://localhost:8082/api/customers/65f1c2a4e8b9d31234567890
```

### 2.3 Errores

Todas las respuestas de error usan el mismo cuerpo:

```json
{
  "timestamp": "2026-11-04T10:15:30.123Z",
  "status": 409,
  "error": "Conflict",
  "code": "CUSTOMER_ALREADY_EXISTS",
  "message": "El cliente con documento 70123456 ya esta registrado",
  "path": "/api/customers",
  "details": {}
}
```

| Codigo | HTTP | Origen |
|---|---|---|
| `CUSTOMER_ALREADY_EXISTS` | `409` | Documento duplicado |
| `CUSTOMER_ALREADY_DELETED` | `409` | Baja logica repetida |
| `CUSTOMER_NOT_FOUND` | `404` | Cliente inexistente |
| `INVALID_CUSTOMER_PROFILE` | `422` | Perfil incompatible con el tipo |
| `INVALID_CUSTOMER_DATA` | `422` | Datos incompatibles con el tipo de cliente |
| `INVALID_DOCUMENT_TYPE` | `422` | Tipo de documento no soportado |
| `INVALID_CUSTOMER_STATUS` | `422` | Estado no asignable por `PUT` |
| `VALIDATION_ERROR` | `400` | Fallo de `@Valid` en el cuerpo |
| `INVALID_INPUT` | `400` | JSON o parametro mal formado (por ejemplo, un enum invalido) |

Documentacion interactiva: `/swagger-ui.html` y `/v3/api-docs`.

---

## 3. Reglas de negocio

1. **Documento unico.** `documentNumber` es unico en toda la plataforma (indice unico en MongoDB).
   Si se repite, la operacion falla con `CUSTOMER_ALREADY_EXISTS` (`409`).
   El numero se normaliza en mayusculas antes de persistir y comparar.
2. **Perfil segun tipo.** `VIP` solo es valido para `PERSONAL`, `PYME` solo para `ENTERPRISE` y
   `STANDARD` es valido para ambos. Si no cuadra, `INVALID_CUSTOMER_PROFILE` (`422`).
3. **Campos por tipo de cliente.**
   - `ENTERPRISE`: exige `businessName` y **no** admite `firstName`, `lastName` ni `birthDate`.
   - `PERSONAL`: exige `firstName` y `lastName`, y **no** admite `businessName`.
   - Los campos no permitidos se persisten como `null`.
4. **Tipo de documento.** Obligatorio y limitado a `DNI`, `CEX`, `PASSPORT` o `RUC`.
5. **Baja logica.** `DELETE` marca `status = DELETED` y `active = false`; el documento **no** se borra
   de MongoDB. Un cliente ya eliminado responde `CUSTOMER_ALREADY_DELETED` (`409`).
6. **Estado.** El alta siempre crea clientes `ACTIVE` y habilitados. `PUT` puede asignar `ACTIVE` o
   `BLOCKED`; el estado `DELETED` solo se alcanza mediante `DELETE` (de lo contrario el evento de
   eliminacion nunca se publicaria).
7. **Credencial temporal.** En el alta se genera una contrasena temporal de **12 caracteres** con
   `SecureRandom`. Se devuelve **unicamente en la respuesta de creacion** y viaja en el evento
   `yanki.customer.registered.v1`; nunca aparece en listados ni en consultas posteriores.
8. **Usuario de acceso.** El `username` se deriva del correo electronico en minusculas.
9. **Cache.** La consulta por documento se cachea en Redis y la cache se invalida cuando el cliente
   cambia (se borran la clave anterior y la nueva). Ante una falla de Redis el servicio degrada al
   repositorio de datos sin propagar el error.

### 3.1 Modelo de datos

Coleccion `customers`:

| Campo | Tipo | Notas |
|---|---|---|
| `id` | `String` | UUID generado por MongoDB |
| `type` | `CustomerType` | `PERSONAL` \| `ENTERPRISE` (se persiste como `String`) |
| `profile` | `CustomerProfile` | `STANDARD` \| `VIP` \| `PYME` (se persiste como `String`) |
| `status` | `CustomerStatus` | `ACTIVE` \| `BLOCKED` \| `DELETED` (se persiste como `String`) |
| `documentType` | `DocumentType` | `DNI` \| `CEX` \| `PASSPORT` \| `RUC` (se persiste como `String`) |
| `documentNumber` | `String` | **Indice unico** |
| `businessName` | `String` | Solo empresarial |
| `firstName` / `lastName` | `String` | Solo personal |
| `email` | `String` | Indice |
| `phone` | `String` | Indice |
| `address` | `String` | |
| `birthDate` | `LocalDate` | Solo personal |
| `active` | `boolean` | |
| `createdAt` / `updatedAt` | `LocalDateTime` | |

---

## 4. Eventos Kafka

La clave de cada mensaje es el `customerId`, para preservar el orden de particion por entidad.

| Topic | Quando se publica | Payload |
|---|---|---|
| `yanki.customer.registered.v1` | Alta de cliente | `CustomerRegisteredEvent(customerId, username, email, temporaryPassword, roles)` |
| `yanki.customer.updated.v1` | Actualizacion de cliente | `CustomerUpdatedEvent(customerId, customerType, profile, status, occurredAt)` |
| `yanki.customer.deleted.v1` | Baja logica | `CustomerDeletedEvent(customerId, occurredAt)` |

Roles asignados: `ROLE_CUSTOMER` para todos y `ROLE_ENTERPRISE` adicional para clientes
empresariales.

---

## 5. Redis

| Clave | Contenido | TTL |
|---|---|---|
| `yanki:customers:catalogs` | Catalogos maestros (JSON) | `yanki.cache.catalog-ttl` (default `30m`) |
| `yanki:customers:document:{DOCUMENT_TYPE}:{DOCUMENT_NUMBER}` | Cliente (JSON) | `yanki.cache.customer-ttl` (default `10m`) |

Ambos TTL se configuran con `@ConfigurationProperties(prefix = "yanki.cache")`.

---

## 6. Variables de entorno

| Variable | Default | Descripcion |
|---|---|---|
| `SERVER_PORT` | `8082` | Puerto HTTP del servicio |
| `MONGODB_URI` | `mongodb://localhost:27017/yanki_customers` | URI de MongoDB |
| `REDIS_HOST` | `localhost` | Host de Redis |
| `REDIS_PORT` | `6379` | Puerto de Redis |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Servidores Kafka |
| `KAFKA_GROUP_ID` | `customers-service` | Grupo de consumidores |
| `EUREKA_URL` | `http://localhost:8761/eureka/` | Servidor de descubrimiento |
| `CONFIG_SERVER_URL` | `http://localhost:8888` | Config Server de Spring Cloud |
| `YANKI_CACHE_CATALOG_TTL` | `30m` | TTL de los catalogos maestros en Redis |
| `YANKI_CACHE_CUSTOMER_TTL` | `10m` | TTL de la cache de clientes por documento |

---

## 7. Como ejecutarlo

### 7.1 Requisitos

- JDK 17
- Maven 3.9+
- MongoDB, Redis y Kafka (con `docker-compose.yml` de la raiz del proyecto)

### 7.2 Desarrollo

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.21.0.3"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
mvn spring-boot:run
```

### 7.3 Build y pruebas

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17.0.21.0.3"
$env:PATH="$env:JAVA_HOME\bin;$env:PATH"
mvn clean verify
```

El build ejecuta **Checkstyle** (sin violaciones de nivel error) y las pruebas unitarias con
JUnit 5, Mockito y StepVerifier. El reporte de cobertura queda en `target/site/jacoco/index.html`.

Pruebas incluidas:

| Test | Cobertura |
|---|---|
| `CustomerServiceTest` | 19 tests cubriendo CP-001 a CP-019 |
| `CustomerControllerTest` | 7 tests de endpoints REST con WebTestClient |
| `CustomerMongoAdapterTest` | 6 tests del adaptador MongoDB |
| `CustomerCacheAdapterTest` | 7 tests del adaptador Redis |
| `CustomerKafkaPublisherAdapterTest` | 3 tests del adaptador Kafka |
| `TemporaryPasswordGeneratorTest` | 2 tests de longitud y aleatoriedad |

### 7.4 Docker

```bash
docker build -t yanki/customers-service:1.0.0 .
docker run -p 8082:8082 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/yanki_customers \
  -e REDIS_HOST=host.docker.internal \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  yanki/customers-service:1.0.0
```

### 7.5 Salud del servicio

```bash
curl http://localhost:8082/actuator/health
```

---

## 8. Estructura del codigo

```
src/main/java/com/yanki/customers
├── CustomersServiceApplication.java
├── domain
│   ├── model        # Customer (puro, sin anotaciones de BD), enums, CustomerCatalogs, CustomerPage
│   ├── port
│   │   ├── in       # Puertos de entrada (casos de uso): CreateCustomerUseCase, FindCustomerUseCase, ModifyCustomerUseCase
│   │   └── out      # Puertos de salida: CustomerPersistencePort, CustomerEventPublisherPort, CustomerCachePort, TemporaryPasswordPort
│   ├── service      # CustomerService implementa puertos de entrada; CustomerCommand, CustomerFilter, CustomerResult
│   ├── event        # Payloads inmutables de eventos Kafka (Java records)
│   └── exception    # BusinessException y codigos de error
├── infrastructure
│   ├── cache
│   │   └── adapter  # CustomerCacheAdapter (implementa CustomerCachePort → Redis)
│   ├── config       # CacheProperties, OpenApiConfig
│   ├── messaging
│   │   ├── adapter  # CustomerKafkaPublisherAdapter (implementa CustomerEventPublisherPort → Kafka)
│   │   └── Topics
│   ├── persistence
│   │   ├── document # CustomerDocument (@Document, @Id, @Indexed — acoplado a MongoDB)
│   │   ├── repository # SpringDataCustomerRepository (ReactiveMongoRepository)
│   │   ├── mapper     # CustomerDocumentMapper (traduce domain ↔ document)
│   │   └── adapter    # CustomerMongoAdapter (implementa CustomerPersistencePort)
│   └── security
│       ├── TemporaryPasswordGenerator
│       └── adapter    # TemporaryPasswordAdapter (implementa TemporaryPasswordPort)
└── interfaces
    └── rest
        ├── controller # CustomerController (inyecta puertos de entrada, no servicios)
        ├── dto        # request/response, PageResponse, ApiError, mappers
        └── GlobalExceptionHandler.java
```

**Arquitectura Hexagonal:** El dominio (`domain`) es 100% puro: no importa Spring Data, Kafka, Web ni Reactor.
Las dependencias fluyen hacia adentro a través de puertos (`domain.port`). Los adaptadores en `infrastructure`
implementan los puertos de salida y traducen entre Project Reactor (`Mono`/`Flux`) y RxJava 3 (`Single`/`Maybe`/`Completable`).

---

## 9. Trazabilidad

| Tipo | Archivo | Descripción |
|------|---------|-------------|
| **Caso de Uso** | `../CASOS_USO/CU-01-AltaClientes.md` | Gestión de clientes (CU-01) |
| **Diagramas SEC** | `../DIAGRAMAS_SECUENCIAS/SEC001_*.md` | Flujo de alta de cliente |
| **Diagramas SEC** | `../DIAGRAMAS_SECUENCIAS/SEC042_*.md` | Flujo de consulta por documento |
| **Casos de Prueba** | `test-cases/CASOS_PRUEBA.md` | 19 CPs locales (10 Happy Path + 9 Error) |
| **Casos de Prueba (fuente)** | `../CASOS_PRUEBA/CASOS_PRUEBA_QA.md` | CP-001 a CP-019 (fuente global) |
| **Coleccion Postman** | `postman/Yanki-Customers-Service.postman_collection.json` | 19 requests con test scripts y respuestas de ejemplo |
| **RTM** | `../CASOS_PRUEBA/RTM.md` | Matriz CU-01 ↔ SEC001/SEC042 ↔ CP-001..019 |
| **Trazabilidad por servicio** | `../CASOS_PRUEBA/TRACEABILIDAD_POR_SERVICIO.md` | customers-service en matriz global |
