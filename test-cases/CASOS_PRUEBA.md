# Casos de Prueba — customers-service

**Microservicio:** customers-service (puerto 8082)  
**Caso de Uso:** [CU-01: Gestión de Clientes](../CASOS_USO/CU-01-AltaClientes.md)  
**Diagramas de Secuencia:** [SEC001](../DIAGRAMAS_SECUENCIAS/SEC001_alta_cliente_personal.md), [SEC042](../DIAGRAMAS_SECUENCIAS/SEC042_credencial_automatica_eventdriven.md)  
**Fuente:** [CASOS_PRUEBA_QA.md](../CASOS_PRUEBA/CASOS_PRUEBA_QA.md)  
**RTM:** [Matriz de Trazabilidad](../CASOS_PRUEBA/RTM.md#cu-01)

---

## Criterios generales de pruebas

### Precondiciones generales para Happy Path
- Existe un usuario válido con credenciales activas.
- Se dispone de `username` y `password` válidos.
- Los servicios involucrados se encuentran operativos.
- El API Gateway puede validar correctamente el bearer token JWT.
- Cuando aplique, Redis, MongoDB y Kafka se encuentran disponibles.
- `JWT_SECRET` y `JWT_ISSUER` son idénticos en auth-service y api-gateway (Config Server).
- Eureka Server tiene registrados los 10 microservicios backend.

### Criterios de validación comunes
- Validar código HTTP esperado.
- Validar estructura y contenido de la respuesta (JSON schema).
- Validar persistencia en base de datos o vistas/proyecciones.
- Validar publicación o consumo de eventos Kafka cuando aplique.
- Validar invalidación/uso de caché Redis cuando aplique.
- Validar mensajes o códigos de error funcional esperados.
- Validar headers de propagación (`X-User-Id`, `X-User-Roles`, `X-Customer-Id`, `X-Request-Id`) en llamadas downstream.
- Validar comportamiento de Circuit Breaker (timeout 2s, fallback) cuando aplique.
- Validar idempotencia cuando aplique (headers `X-Idempotency-Key`).

---

## CU-01: Gestión de Clientes (CRUD)

### CP-001 — Alta de cliente personal exitosa (STANDARD)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md, SEC042_credencial_automatica_eventdriven.md
- **Tipo**: Happy Path
- **Precondiciones**:
  - Operador autenticado con token JWT válido (rol `ROLE_OPERATOR` o `ROLE_ADMIN`).
  - No existe cliente con el mismo tipo y número de documento.
  - Customers Service, MongoDB (`yanki_customers`), Redis y Kafka operativos.
- **Pasos**:
  1. Enviar `POST /api/customers` con datos válidos de cliente PERSONAL (type=PERSONAL, profile=STANDARD, documentType=DNI, documentNumber único, firstName, lastName, email, phone, address, birthDate).
  2. Validar respuesta `201 Created` con body que incluya `id`, `temporaryPassword` (12 chars), `status=ACTIVE`.
  3. Verificar persistencia en MongoDB: cliente con `status=ACTIVE`, `active=true`, `createdAt`, `updatedAt`.
  4. Verificar publicación de evento `yanki.customer.registered.v1` en Kafka (key=customerId, payload con roles=[ROLE_CUSTOMER]).
  5. Verificar invalidación de caché Redis: `yanki:customers:catalogs` y `yanki:customers:document:DNI:{number}`.
  6. Verificar que auth-service consume el evento y crea credencial automática (username=email lowercase, password=BCrypt(temporaryPassword), roles=[ROLE_CUSTOMER], enabled=true).
- **Postcondiciones**:
  - Cliente creado con estado `ACTIVE`.
  - Credencial automática creada en `yanki_auth.credentials`.
  - Evento procesado con idempotencia (processed_events).
- **Errores esperados**: No aplica.

### CP-002 — Alta de cliente personal exitosa (VIP)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Happy Path
- **Precondiciones**: Igual que CP-001 pero profile=VIP.
- **Pasos**:
  1. Enviar `POST /api/customers` con `type:PERSONAL`, `profile:VIP`, resto datos válidos.
  2. Validar `201 Created` con `profile=VIP`.
  3. Verificar evento `yanki.customer.registered.v1` con `roles=[ROLE_CUSTOMER]` (VIP es perfil, no rol de auth).
  4. Verificar que customer_card_view se actualizará cuando se emita tarjeta crédito (requisito para VIP).
- **Postcondiciones**: Cliente VIP creado. Nota: la validación de tarjeta crédito activa se hace al abrir cuenta VIP (CU-14), no en alta de cliente.

### CP-003 — Alta de cliente empresarial exitosa (ENTERPRISE/PYME)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md, SEC042_credencial_automatica_eventdriven.md
- **Tipo**: Happy Path
- **Precondiciones**: Igual que CP-001.
- **Pasos**:
  1. Enviar `POST /api/customers` con `type:ENTERPRISE`, `profile:PYME`, `documentType:RUC`, `documentNumber`, `businessName`, `email`, `phone`, `address` (SIN firstName/lastName/birthDate).
  2. Validar `201 Created` con `type=ENTERPRISE`, `profile=PYME`.
  3. Verificar evento `yanki.customer.registered.v1` con `roles=[ROLE_CUSTOMER, ROLE_ENTERPRISE]`.
- **Postcondiciones**: Cliente empresarial PYME creado con rol ENTERPRISE.

### CP-004 — Alta de cliente rechazada por documento duplicado
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Regla de negocio (RN-01)
- **Precondiciones**:
  - Operador autenticado con JWT válido.
  - Ya existe un cliente con el mismo documento (type + documentNumber).
- **Pasos**:
  1. Enviar `POST /api/customers` con documento ya registrado.
  2. Validar respuesta `409 Conflict`.
- **Postcondiciones**:
  - No se crea un nuevo cliente.
  - No se publica evento de alta.
- **Errores esperados**:
  - HTTP `409`
  - Código `CUSTOMER_ALREADY_EXISTS`

### CP-005 — Alta de cliente rechazada: PERSONAL sin firstName/lastName (RN-05)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Validación
- **Precondiciones**: Operador autenticado.
- **Pasos**:
  1. Enviar `POST /api/customers` con `type:PERSONAL` pero sin `firstName` o `lastName`.
  2. Validar rechazo.
- **Errores esperados**:
  - HTTP `422`
  - Código `INVALID_CUSTOMER_DATA`

### CP-006 — Alta de cliente rechazada: ENTERPRISE sin businessName (RN-04)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Validación
- **Precondiciones**: Operador autenticado.
- **Pasos**:
  1. Enviar `POST /api/customers` con `type:ENTERPRISE` pero sin `businessName`.
  2. Validar rechazo.
- **Errores esperados**:
  - HTTP `422`
  - Código `INVALID_CUSTOMER_DATA`

### CP-007 — Alta de cliente rechazada: Perfil VIP en ENTERPRISE (RN-02)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Regla de negocio
- **Precondiciones**: Operador autenticado.
- **Pasos**:
  1. Enviar `POST /api/customers` con `type:ENTERPRISE`, `profile:VIP`.
  2. Validar rechazo.
- **Errores esperados**:
  - HTTP `422`
  - Código `INVALID_CUSTOMER_PROFILE`

### CP-008 — Alta de cliente rechazada: Perfil PYME en PERSONAL (RN-03)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Regla de negocio
- **Precondiciones**: Operador autenticado.
- **Pasos**:
  1. Enviar `POST /api/customers` con `type:PERSONAL`, `profile:PYME`.
  2. Validar rechazo.
- **Errores esperados**:
  - HTTP `422`
  - Código `INVALID_CUSTOMER_PROFILE`

### CP-009 — Consulta de cliente por ID exitosa
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Happy Path
- **Precondiciones**: Cliente autenticado, cliente existe.
- **Pasos**:
  1. Enviar `GET /api/customers/{id}`.
  2. Validar `200 OK` con estructura `CustomerResponse`.
- **Postcondiciones**: Sin cambios de estado.

### CP-010 — Consulta de cliente por documento (Redis Cache HIT)
- **CU:** CU-01 | **SEC:** SEC042_credencial_automatica_eventdriven.md
- **Tipo**: Happy Path
- **Precondiciones**: Cliente existe, caché Redis poblado (`yanki:customers:document:DNI:{number}` TTL 10m).
- **Pasos**:
  1. Enviar `GET /api/customers/document/DNI/70123456`.
  2. Validar `200 OK` y respuesta servida desde Redis (verificar headers o logs de cache HIT).
- **Postcondiciones**: Sin cambios.

### CP-011 — Consulta de cliente por documento (Redis Cache MISS → MongoDB)
- **CU:** CU-01 | **SEC:** SEC042_credencial_automatica_eventdriven.md
- **Tipo**: Happy Path
- **Precondiciones**: Cliente existe, caché Redis vacío/expirado para el documento.
- **Pasos**:
  1. Enviar `GET /api/customers/document/DNI/70123456`.
  2. Validar `200 OK` y respuesta desde MongoDB.
  3. Verificar que se populó Redis con TTL 10m.
- **Postcondiciones**: Caché actualizado.

### CP-012 — Actualización de cliente exitosa (cambio perfil a VIP)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Happy Path
- **Precondiciones**: Cliente existe ACTIVE, operador autenticado, cliente tiene tarjeta crédito activa (validación interna en customers-service consulta customer_card_view).
- **Pasos**:
  1. Enviar `PUT /api/customers/{id}` con `profile:VIP`.
  2. Validar `200 OK` con `profile=VIP`, `updatedAt` actualizado.
  3. Verificar invalidación Redis: cache documento anterior + nuevo.
  4. Verificar evento `yanki.customer.updated.v1` publicado (customerId, profile=VIP, status=ACTIVE, occurredAt).
  5. Verificar que accounts/cards/credits/wallet services consumen y actualizan `customer_profile_view`.
- **Postcondiciones**: Perfil actualizado, vistas locales sincronizadas.

### CP-013 — Actualización rechazada: VIP sin tarjeta crédito activa
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Regla de negocio (validación transversal con cards-service)
- **Precondiciones**: Cliente PERSONAL STANDARD, SIN tarjeta crédito activa (`customer_card_view.hasCreditCard=false`).
- **Pasos**:
  1. Enviar `PUT /api/customers/{id}` con `profile:VIP`.
  2. Validar `422 Unprocessable Entity`.
- **Errores esperados**:
  - HTTP `422`
  - Código `INVALID_VIP_PROFILE`
  - Mensaje: "Cliente VIP requiere tarjeta de crédito activa"

### CP-014 — Actualización rechazada: PYME sin tarjeta crédito activa
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Regla de negocio
- **Precondiciones**: Cliente ENTERPRISE STANDARD, SIN tarjeta crédito activa.
- **Pasos**: Enviar `PUT /api/customers/{id}` con `profile:PYME`.
- **Errores esperados**: HTTP `422`, código `INVALID_PYME_PROFILE`.

### CP-015 — Actualización rechazada: intento asignar status=DELETED (RN-08)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Validación
- **Precondiciones**: Cliente existe.
- **Pasos**: Enviar `PUT /api/customers/{id}` con `status:DELETED`.
- **Errores esperados**: HTTP `422`, código `INVALID_CUSTOMER_STATUS`.

### CP-016 — Baja lógica de cliente exitosa
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Happy Path
- **Precondiciones**: Operador autenticado, cliente existe ACTIVE.
- **Pasos**:
  1. Enviar `DELETE /api/customers/{id}`.
  2. Validar `204 No Content`.
  3. Verificar en MongoDB: `status=DELETED`, `active=false`, `updatedAt=now`.
  4. Verificar invalidación Redis cache cliente.
  5. Verificar evento `yanki.customer.deleted.v1` (customerId, occurredAt).
  6. Verificar consumo por accounts/cards/credits/wallet → marcan vistas como DELETED.
- **Postcondiciones**: Cliente marcado DELETED, vistas locales actualizadas.

### CP-017 — Baja lógica rechazada: cliente ya DELETED (RN-07)
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Error / Regla de negocio
- **Precondiciones**: Cliente con `status=DELETED`.
- **Pasos**: Enviar `DELETE /api/customers/{id}`.
- **Errores esperados**: HTTP `409`, código `CUSTOMER_ALREADY_DELETED`.

### CP-018 — Listado de clientes con filtros y paginación
- **CU:** CU-01 | **SEC:** SEC001_alta_cliente_personal.md
- **Tipo**: Happy Path
- **Precondiciones**: Operador autenticado, existen clientes.
- **Pasos**:
  1. Enviar `GET /api/customers?type=PERSONAL&profile=VIP&status=ACTIVE&page=0&size=20`.
  2. Validar `200 OK` con `PageResponse<CustomerResponse>` (content, page, size, totalElements, totalPages).
- **Postcondiciones**: Sin cambios.

### CP-019 — Consulta de catálogos maestros (Redis Cache)
- **CU:** CU-01 | **SEC:** SEC042_credencial_automatica_eventdriven.md
- **Tipo**: Happy Path
- **Precondiciones**: Cache Redis `yanki:customers:catalogs` poblado (TTL 30m).
- **Pasos**:
  1. Enviar `GET /api/customers/catalogs`.
  2. Validar `200 OK` con enums: CustomerType, CustomerProfile, CustomerStatus, DocumentType.
- **Postcondiciones**: Sin cambios.

---

## Resumen de cobertura

| CP | Nombre | Tipo | CU | SEC |
|----|--------|------|----|-----|
| CP-001 | Alta cliente personal STANDARD | Happy Path | CU-01 | SEC001, SEC042 |
| CP-002 | Alta cliente personal VIP | Happy Path | CU-01 | SEC001 |
| CP-003 | Alta cliente empresarial PYME | Happy Path | CU-01 | SEC001, SEC042 |
| CP-004 | Alta rechazada: documento duplicado | Error | CU-01 | SEC001 |
| CP-005 | Alta rechazada: PERSONAL sin firstName/lastName | Error | CU-01 | SEC001 |
| CP-006 | Alta rechazada: ENTERPRISE sin businessName | Error | CU-01 | SEC001 |
| CP-007 | Alta rechazada: VIP en ENTERPRISE | Error | CU-01 | SEC001 |
| CP-008 | Alta rechazada: PYME en PERSONAL | Error | CU-01 | SEC001 |
| CP-009 | Consulta por ID exitosa | Happy Path | CU-01 | SEC001 |
| CP-010 | Consulta por documento (Cache HIT) | Happy Path | CU-01 | SEC042 |
| CP-011 | Consulta por documento (Cache MISS) | Happy Path | CU-01 | SEC042 |
| CP-012 | Actualización perfil a VIP | Happy Path | CU-01 | SEC001 |
| CP-013 | Actualización rechazada: VIP sin tarjeta crédito | Error | CU-01 | SEC001 |
| CP-014 | Actualización rechazada: PYME sin tarjeta crédito | Error | CU-01 | SEC001 |
| CP-015 | Actualización rechazada: status=DELETED por PUT | Error | CU-01 | SEC001 |
| CP-016 | Baja lógica exitosa | Happy Path | CU-01 | SEC001 |
| CP-017 | Baja rechazada: ya DELETED | Error | CU-01 | SEC001 |
| CP-018 | Listado con filtros y paginación | Happy Path | CU-01 | SEC001 |
| CP-019 | Consulta catálogos maestros | Happy Path | CU-01 | SEC042 |

**Total:** 19 casos (10 Happy Path + 9 Error)

---

## Referencias bidireccionales

| Archivo | Sección | Descripción |
|---------|---------|-------------|
| [`../../docs/CASOS_USO`](../../docs/CASOS_USO/CU-01-AltaClientes.md) | Caso de Uso completo | Gestión de clientes (CU-01) |
| [`../../DIAGRAMAS_SECUENCIAS/SEC001_alta_cliente_personal.md`](../../DIAGRAMAS_SECUENCIAS/SEC001_alta_cliente_personal.md) | Diagrama de secuencia | Flujo de alta de cliente personal |
| [`../../DIAGRAMAS_SECUENCIAS/SEC042_credencial_automatica_eventdriven.md`](../../DIAGRAMAS_SECUENCIAS/SEC042_credencial_automatica_eventdriven.md) | Diagrama de secuencia | Flujo de credencial automática event-driven |
| [`../../docs/CASOS_PRUEBA`](../../docs/CASOS_PRUEBA/CASOS_PRUEBA_QA.md) | CP-001 a CP-019 | Fuente original de los casos de prueba |
| [`../../docs/CASOS_PRUEBA`](../../docs/CASOS_PRUEBA/RTM.md) | Fila CU-01 | Matriz de trazabilidad completa |
| [`../../docs/CASOS_PRUEBA`](../../docs/CASOS_PRUEBA/TRACEABILIDAD_POR_SERVICIO.md) | customers-service | Trazabilidad por microservicio |
