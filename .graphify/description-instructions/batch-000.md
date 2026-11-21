# Node Description Batch 1 of 4

Graphify is running in assistant/skill mode (no API key). You are the host
assistant (Claude Code / Codex / Gemini CLI). Read the prompt below and write
your JSON answer to the answer file.

## Prompt

You are documenting nodes in a knowledge graph.
For each entry below, write ONE concise factual plain-language sentence
describing what it is or does. Use only the provided context.
For a code symbol (kind=code-symbol — a function, class, or constant),
describe what the function/symbol does based on its name, source location
and neighbors — e.g. "Resolves the configured ontology profile from graphify.yaml.".
Write every description in English (en). Do not switch languages.
No marketing language.
Respond ONLY with a JSON object mapping each node id (as a string) to its
one-sentence description — no prose, no markdown fences.

- "service_customerservice_customerservice": "CustomerService" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L40 | neighbors=[CustomerService.java, .applyUpdate(), .create(), .delete(), .evictDocumentKeys(), .findAll()]
- "service_customerservicetest_customerservicetest": "CustomerServiceTest" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L44 | neighbors=[CustomerServiceTest.java, .existingCustomer(), .personalCommand(), .shouldCreateCustomerWhenDataIsValid(), .shouldDeleteCustomerLogically(), .shouldFailWhenCustomerDoesNotExist()]
- "cache_customercatalogcacheservice_customercatalogcacheservice": "CustomerCatalogCacheService" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L23 | neighbors=[CustomerCatalogCacheService.java, .deserialize(), .documentKey(), .evictCustomerByDocument(), .getCatalogs(), .getCustomerByDocument()]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest": "CustomerCatalogCacheServiceTest" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L31 | neighbors=[CustomerCatalogCacheServiceTest.java, .customer(), .setUp(), .shouldCacheCatalogsWithConfiguredTtl(), .shouldCacheCustomerByDocumentWithConfi…, .shouldEvictCustomerByDocument()]
- "customer": "Customer" | kind=code-symbol | neighbors=[CustomerCatalogCacheService.java, CustomerCatalogCacheServiceTest.java, CustomerResponseMapper.java, CustomerSearchAdapter.java, CustomerRepository.java, CustomerRepository]
- "controller_customercontroller_customercontroller": "CustomerController" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L40 | neighbors=[CustomerController.java, .catalogs(), .create(), .delete(), .findByDocument(), .findById()]
- "rest_globalexceptionhandler_globalexceptionhandler": "GlobalExceptionHandler" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L23 | neighbors=[GlobalExceptionHandler.java, .handleBusiness(), .handleInput(), .handleResponseStatus(), .handleUnexpected(), .handleValidation()]
- "service_customerservice_customerservice_validatecommand": ".validateCommand()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L310 | neighbors=[CustomerService, .create(), .update(), .isBlank(), .validateDocumentType(), .validateProfile()]
- "repository_customerrepository_customerrepository": "CustomerRepository" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/repository/CustomerRepository.java:L14 | neighbors=[CustomerRepository.java, Customer, ReactiveMongoRepository, .findByDocumentNumber(), .findByDocumentTypeAndDocumentNumber(), String]
- "rest_globalexceptionhandler_globalexceptionhandler_path": ".path()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L133 | neighbors=[GlobalExceptionHandler, .handleBusiness(), .handleInput(), .handleResponseStatus(), .handleUnexpected(), .handleValidation()]
- "service_customerservice_customerservice_update": ".update()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L142 | neighbors=[CustomerService, .applyUpdate(), .findById(), .normalizeDocumentNumber(), .validateCommand(), .validateStatusChange()]
- "service_customerservicetest_customerservicetest_existingcustomer": ".existingCustomer()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L257 | neighbors=[CustomerServiceTest, .shouldDeleteCustomerLogically(), .shouldFailWhenDocumentNumberAlreadyExi…, .shouldFailWhenUpdatedDocumentNumberBel…, .shouldListCustomersWithFilters(), .shouldUpdateCustomer()]
- "messaging_customereventpublisher_customereventpublisher": "CustomerEventPublisher" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisher.java:L19 | neighbors=[CustomerEventPublisher.java, .publishDeleted(), .publishRegistered(), .publishUpdated(), .send()]
- "service_customerservice_customerservice_applyupdate": ".applyUpdate()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L247 | neighbors=[CustomerService, .evictDocumentKeys(), .normalizeEmail(), .trimToNull(), .update()]
- "service_customerservice_customerservice_create": ".create()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L72 | neighbors=[CustomerService, .normalizeDocumentNumber(), .normalizeEmail(), .persistNewCustomer(), .validateCommand()]
- "service_customerservice_customerservice_isblank": ".isBlank()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L420 | neighbors=[CustomerService, .findByDocument(), .trimToNull(), .validateCommand(), .validateTypeSpecificFields()]
- "cache_customercatalogcacheservice_customercatalogcacheservice_documentkey": ".documentKey()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L122 | neighbors=[CustomerCatalogCacheService, .evictCustomerByDocument(), .getCustomerByDocument(), .putCustomerByDocument()]
- "exception_businessexception_businessexception": "BusinessException" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/exception/BusinessException.java:L9 | neighbors=[BusinessException.java, .BusinessException(), .getCode(), RuntimeException]
- "messaging_customereventpublisher_customereventpublisher_send": ".send()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisher.java:L65 | neighbors=[CustomerEventPublisher, .publishDeleted(), .publishRegistered(), .publishUpdated()]
- "messaging_customereventpublishertest_customereventpublishertest": "CustomerEventPublisherTest" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisherTest.java:L29 | neighbors=[CustomerEventPublisherTest.java, .shouldPublishCustomerDeletedEventWithC…, .shouldPublishCustomerRegisteredEventWi…, .shouldPublishCustomerUpdatedEventWithC…]
- "service_customerservice_customerservice_normalizedocumentnumber": ".normalizeDocumentNumber()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L440 | neighbors=[CustomerService, .create(), .findByDocument(), .update()]
- "service_customerservice_customerservice_persistnewcustomer": ".persistNewCustomer()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L206 | neighbors=[CustomerService, .create(), .rolesFor(), .trimToNull()]
- "service_customerservice_customerservice_trimtonull": ".trimToNull()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L430 | neighbors=[CustomerService, .applyUpdate(), .persistNewCustomer(), .isBlank()]
- "cache_customercatalogcacheservice_customercatalogcacheservice_deserialize": ".deserialize()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L147 | neighbors=[CustomerCatalogCacheService, .getCatalogs(), .getCustomerByDocument()]
- "cache_customercatalogcacheservice_customercatalogcacheservice_getcustomerbydocument": ".getCustomerByDocument()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L76 | neighbors=[CustomerCatalogCacheService, .deserialize(), .documentKey()]
- "cache_customercatalogcacheservice_customercatalogcacheservice_putcustomerbydocument": ".putCustomerByDocument()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L90 | neighbors=[CustomerCatalogCacheService, .documentKey(), .serialize()]
- "cache_customercatalogcacheservice_customercatalogcacheservice_serialize": ".serialize()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L135 | neighbors=[CustomerCatalogCacheService, .putCatalogs(), .putCustomerByDocument()]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_customer": ".customer()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L122 | neighbors=[CustomerCatalogCacheServiceTest, .shouldCacheCustomerByDocumentWithConfi…, .shouldRetrieveCustomerByDocumentFromRe…]
- "dto_customerresponsemapper_customerresponsemapper": "CustomerResponseMapper" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerResponseMapper.java:L9 | neighbors=[CustomerResponseMapper.java, .toCreatedResponse(), .toResponse()]
- "persistence_customersearchadapter_customersearchadapter": "CustomerSearchAdapter" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/persistence/CustomerSearchAdapter.java:L24 | neighbors=[CustomerSearchAdapter.java, .buildCriteria(), .search()]
- "repository_customerrepository": "CustomerRepository.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/repository/CustomerRepository.java:L1 | neighbors=[Customer, ReactiveMongoRepository, CustomerRepository]
- "rest_globalexceptionhandler_globalexceptionhandler_handlebusiness": ".handleBusiness()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L34 | neighbors=[GlobalExceptionHandler, .path(), .resolveBusinessStatus()]
- "service_customerservice_customerservice_findbydocument": ".findByDocument()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L114 | neighbors=[CustomerService, .isBlank(), .normalizeDocumentNumber()]
- "service_customerservice_customerservice_findbyid": ".findById()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L91 | neighbors=[CustomerService, .delete(), .update()]
- "service_customerservice_customerservice_normalizeemail": ".normalizeEmail()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L450 | neighbors=[CustomerService, .applyUpdate(), .create()]
- "service_customerservice_customerservice_validatetypespecificfields": ".validateTypeSpecificFields()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L367 | neighbors=[CustomerService, .validateCommand(), .isBlank()]
- "service_customerservicetest_customerservicetest_personalcommand": ".personalCommand()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L251 | neighbors=[CustomerServiceTest, .shouldCreateCustomerWhenDataIsValid(), .shouldFailWhenDocumentNumberAlreadyExi…]
- "service_customerservicetest_customerservicetest_shouldfailwhendocumentnumberalreadyexists": ".shouldFailWhenDocumentNumberAlreadyExists()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L100 | neighbors=[CustomerServiceTest, .existingCustomer(), .personalCommand()]
- "cache_customercatalogcacheservice": "CustomerCatalogCacheService.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L1 | neighbors=[CustomerCatalogCacheService, Customer]
- "cache_customercatalogcacheservice_customercatalogcacheservice_evictcustomerbydocument": ".evictCustomerByDocument()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L106 | neighbors=[CustomerCatalogCacheService, .documentKey()]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\customers-service\.graphify\description-instructions\batch-000.json

Keep each description factual and concise (one sentence). No markdown, no prose
outside the JSON object. It is acceptable to omit a node if context is
insufficient — but include every node you can ground confidently.

Example answer format:
```json
{
  "node_id_1": "Resolves the configured ontology profile from graphify.yaml.",
  "node_id_2": "Colonel James Barclay, an antagonist in The Crooked Man."
}
```
