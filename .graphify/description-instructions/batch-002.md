# Node Description Batch 3 of 4

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

- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_shouldretrievecatalogsfromredis": ".shouldRetrieveCatalogsFromRedis()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L56 | neighbors=[CustomerCatalogCacheServiceTest]
- "cache_customercatalogcacheservicetest_customercatalogcacheservicetest_shouldreturnemptywhenredisfails": ".shouldReturnEmptyWhenRedisFails()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/cache/CustomerCatalogCacheServiceTest.java:L114 | neighbors=[CustomerCatalogCacheServiceTest]
- "config_cacheproperties": "CacheProperties.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/config/CacheProperties.java:L1 | neighbors=[CacheProperties]
- "config_cacheproperties_cacheproperties": "CacheProperties" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/config/CacheProperties.java:L15 | neighbors=[CacheProperties.java]
- "config_openapiconfig": "OpenApiConfig.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/config/OpenApiConfig.java:L1 | neighbors=[OpenApiConfig]
- "config_openapiconfig_openapiconfig_customersopenapi": ".customersOpenApi()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/config/OpenApiConfig.java:L21 | neighbors=[OpenApiConfig]
- "controller_customercontroller": "CustomerController.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L1 | neighbors=[CustomerController]
- "controller_customercontroller_customercontroller_catalogs": ".catalogs()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L162 | neighbors=[CustomerController]
- "controller_customercontroller_customercontroller_create": ".create()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L59 | neighbors=[CustomerController]
- "controller_customercontroller_customercontroller_delete": ".delete()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L132 | neighbors=[CustomerController]
- "controller_customercontroller_customercontroller_findbydocument": ".findByDocument()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L147 | neighbors=[CustomerController]
- "controller_customercontroller_customercontroller_findbyid": ".findById()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L105 | neighbors=[CustomerController]
- "controller_customercontroller_customercontroller_list": ".list()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L78 | neighbors=[CustomerController]
- "controller_customercontroller_customercontroller_update": ".update()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/controller/CustomerController.java:L118 | neighbors=[CustomerController]
- "customers_customersserviceapplication": "CustomersServiceApplication.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/CustomersServiceApplication.java:L1 | neighbors=[CustomersServiceApplication]
- "customers_customersserviceapplication_customersserviceapplication_main": ".main()" | kind=code-symbol | source=src/main/java/com/yanki/customers/CustomersServiceApplication.java:L23 | neighbors=[CustomersServiceApplication]
- "dto_apierror": "ApiError.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/ApiError.java:L1 | neighbors=[of()]
- "dto_apierror_of": "of()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/ApiError.java:L38 | neighbors=[ApiError.java]
- "dto_customercatalogsresponse": "CustomerCatalogsResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerCatalogsResponse.java:L1 | neighbors=[from()]
- "dto_customercatalogsresponse_from": "from()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerCatalogsResponse.java:L26 | neighbors=[CustomerCatalogsResponse.java]
- "dto_customercreatedresponse": "CustomerCreatedResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerCreatedResponse.java:L1 | neighbors=[of()]
- "dto_customercreatedresponse_of": "of()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerCreatedResponse.java:L62 | neighbors=[CustomerCreatedResponse.java]
- "dto_customerrequest": "CustomerRequest.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerRequest.java:L1 | neighbors=[toCommand()]
- "dto_customerrequest_tocommand": "toCommand()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerRequest.java:L84 | neighbors=[CustomerRequest.java]
- "dto_pageresponse": "PageResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/PageResponse.java:L1 | neighbors=[of()]
- "dto_pageresponse_of": "of()" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/PageResponse.java:L37 | neighbors=[PageResponse.java]
- "exception_businessexception": "BusinessException.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/exception/BusinessException.java:L1 | neighbors=[BusinessException]
- "exception_businessexception_businessexception_businessexception": ".BusinessException()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/exception/BusinessException.java:L20 | neighbors=[BusinessException]
- "exception_businessexception_businessexception_getcode": ".getCode()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/exception/BusinessException.java:L30 | neighbors=[BusinessException]
- "exception_errorcodes": "ErrorCodes.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/exception/ErrorCodes.java:L1 | neighbors=[ErrorCodes]
- "exception_errorcodes_errorcodes_errorcodes": ".ErrorCodes()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/exception/ErrorCodes.java:L35 | neighbors=[ErrorCodes]
- "messaging_customereventpublisher": "CustomerEventPublisher.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisher.java:L1 | neighbors=[CustomerEventPublisher]
- "messaging_customereventpublishertest": "CustomerEventPublisherTest.java" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisherTest.java:L1 | neighbors=[CustomerEventPublisherTest]
- "messaging_customereventpublishertest_customereventpublishertest_shouldpublishcustomerdeletedeventwithcorrectfields": ".shouldPublishCustomerDeletedEventWithCorrectFields()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisherTest.java:L79 | neighbors=[CustomerEventPublisherTest]
- "messaging_customereventpublishertest_customereventpublishertest_shouldpublishcustomerregisteredeventwithcorrectfields": ".shouldPublishCustomerRegisteredEventWithCorrectFields()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisherTest.java:L41 | neighbors=[CustomerEventPublisherTest]
- "messaging_customereventpublishertest_customereventpublishertest_shouldpublishcustomerupdatedeventwithcorrectfields": ".shouldPublishCustomerUpdatedEventWithCorrectFields()" | kind=code-symbol | source=src/test/java/com/yanki/customers/infrastructure/messaging/CustomerEventPublisherTest.java:L60 | neighbors=[CustomerEventPublisherTest]
- "messaging_topics": "Topics.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/Topics.java:L1 | neighbors=[Topics]
- "messaging_topics_topics_topics": ".Topics()" | kind=code-symbol | source=src/main/java/com/yanki/customers/infrastructure/messaging/Topics.java:L23 | neighbors=[Topics]
- "model_customer": "Customer.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/Customer.java:L1 | neighbors=[Customer]
- "model_customer_customer": "Customer" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/Customer.java:L21 | neighbors=[Customer.java]

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\customers-service\.graphify\description-instructions\batch-002.json

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
