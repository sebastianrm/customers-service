# Node Description Batch 2 of 4

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

- "cache_customercatalogcacheservice_customercatalogcacheservice_getcatalogs": ".getCatalogs()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L48 | neighbors=[CustomerCatalogCacheService, .deserialize()]
- "cache_customercatalogcacheservice_customercatalogcacheservice_putcatalogs": ".putCatalogs()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheService.java:L61 | neighbors=[CustomerCatalogCacheService, .serialize()]
- "cache_customercatalogcacheservicetest": "CustomerCatalogCacheServiceTest.java" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L1 | neighbors=[CustomerCatalogCacheServiceTest, Customer]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_shouldcachecustomerbydocumentwithconfiguredttl": ".shouldCacheCustomerByDocumentWithConfiguredTtl()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L91 | neighbors=[CustomerCatalogCacheServiceTest, .customer()]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_shouldretrievecustomerbydocumentfromredis": ".shouldRetrieveCustomerByDocumentFromRedis()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L80 | neighbors=[CustomerCatalogCacheServiceTest, .customer()]
- "config_openapiconfig_openapiconfig": "OpenApiConfig" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/config/OpenApiConfig.java:L13 | neighbors=[OpenApiConfig.java, .customersOpenApi()]
- "customers_customersserviceapplication_customersserviceapplication": "CustomersServiceApplication" | kind=code-symbol | source=src/main/java/com/yanki/customers/CustomersServiceApplication.java:L15 | neighbors=[CustomersServiceApplication.java, .main()]
- "dto_customerresponsemapper": "CustomerResponseMapper.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerResponseMapper.java:L1 | neighbors=[Customer, CustomerResponseMapper]
- "dto_customerresponsemapper_customerresponsemapper_tocreatedresponse": ".toCreatedResponse()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerResponseMapper.java:L33 | neighbors=[CustomerResponseMapper, .toResponse()]
- "dto_customerresponsemapper_customerresponsemapper_toresponse": ".toResponse()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerResponseMapper.java:L18 | neighbors=[CustomerResponseMapper, .toCreatedResponse()]
- "exception_errorcodes_errorcodes": "ErrorCodes" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/exception/ErrorCodes.java:L9 | neighbors=[ErrorCodes.java, .ErrorCodes()]
- "messaging_customereventpublisher_customereventpublisher_publishdeleted": ".publishDeleted()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisher.java:L53 | neighbors=[CustomerEventPublisher, .send()]
- "messaging_customereventpublisher_customereventpublisher_publishregistered": ".publishRegistered()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisher.java:L33 | neighbors=[CustomerEventPublisher, .send()]
- "messaging_customereventpublisher_customereventpublisher_publishupdated": ".publishUpdated()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisher.java:L43 | neighbors=[CustomerEventPublisher, .send()]
- "messaging_topics_topics": "Topics" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/Topics.java:L9 | neighbors=[Topics.java, .Topics()]
- "persistence_customersearchadapter": "CustomerSearchAdapter.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/persistence/CustomerSearchAdapter.java:L1 | neighbors=[Customer, CustomerSearchAdapter]
- "persistence_customersearchadapter_customersearchadapter_buildcriteria": ".buildCriteria()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/persistence/CustomerSearchAdapter.java:L54 | neighbors=[CustomerSearchAdapter, .search()]
- "persistence_customersearchadapter_customersearchadapter_search": ".search()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/persistence/CustomerSearchAdapter.java:L37 | neighbors=[CustomerSearchAdapter, .buildCriteria()]
- "reactivemongorepository": "ReactiveMongoRepository" | kind=code-symbol | neighbors=[CustomerRepository.java, CustomerRepository]
- "rest_globalexceptionhandler_globalexceptionhandler_handleinput": ".handleInput()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L68 | neighbors=[GlobalExceptionHandler, .path()]
- "rest_globalexceptionhandler_globalexceptionhandler_handleresponsestatus": ".handleResponseStatus()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L85 | neighbors=[GlobalExceptionHandler, .path()]
- "rest_globalexceptionhandler_globalexceptionhandler_handleunexpected": ".handleUnexpected()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L103 | neighbors=[GlobalExceptionHandler, .path()]
- "rest_globalexceptionhandler_globalexceptionhandler_handlevalidation": ".handleValidation()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L50 | neighbors=[GlobalExceptionHandler, .path()]
- "rest_globalexceptionhandler_globalexceptionhandler_resolvebusinessstatus": ".resolveBusinessStatus()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L119 | neighbors=[GlobalExceptionHandler, .handleBusiness()]
- "service_customerservice": "CustomerService.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L1 | neighbors=[Customer, CustomerService]
- "service_customerservice_customerservice_delete": ".delete()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L166 | neighbors=[CustomerService, .findById()]
- "service_customerservice_customerservice_evictdocumentkeys": ".evictDocumentKeys()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L294 | neighbors=[CustomerService, .applyUpdate()]
- "service_customerservice_customerservice_rolesfor": ".rolesFor()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L407 | neighbors=[CustomerService, .persistNewCustomer()]
- "service_customerservice_customerservice_validatedocumenttype": ".validateDocumentType()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L330 | neighbors=[CustomerService, .validateCommand()]
- "service_customerservice_customerservice_validateprofile": ".validateProfile()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L346 | neighbors=[CustomerService, .validateCommand()]
- "service_customerservice_customerservice_validatestatuschange": ".validateStatusChange()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L394 | neighbors=[CustomerService, .update()]
- "service_customerservicetest": "CustomerServiceTest.java" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L1 | neighbors=[Customer, CustomerServiceTest]
- "service_customerservicetest_customerservicetest_shouldcreatecustomerwhendataisvalid": ".shouldCreateCustomerWhenDataIsValid()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L65 | neighbors=[CustomerServiceTest, .personalCommand()]
- "service_customerservicetest_customerservicetest_shoulddeletecustomerlogically": ".shouldDeleteCustomerLogically()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L221 | neighbors=[CustomerServiceTest, .existingCustomer()]
- "service_customerservicetest_customerservicetest_shouldfailwhenupdateddocumentnumberbelongstoanothercustomer": ".shouldFailWhenUpdatedDocumentNumberBelongsToAnotherCustomer()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L204 | neighbors=[CustomerServiceTest, .existingCustomer()]
- "service_customerservicetest_customerservicetest_shouldlistcustomerswithfilters": ".shouldListCustomersWithFilters()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L152 | neighbors=[CustomerServiceTest, .existingCustomer()]
- "service_customerservicetest_customerservicetest_shouldupdatecustomer": ".shouldUpdateCustomer()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L172 | neighbors=[CustomerServiceTest, .existingCustomer()]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_setup": ".setUp()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L49 | neighbors=[CustomerCatalogCacheServiceTest]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_shouldcachecatalogswithconfiguredttl": ".shouldCacheCatalogsWithConfiguredTtl()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L67 | neighbors=[CustomerCatalogCacheServiceTest]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_shouldevictcustomerbydocument": ".shouldEvictCustomerByDocument()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L105 | neighbors=[CustomerCatalogCacheServiceTest]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\customers-service\.graphify\description-instructions\batch-001.json

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
