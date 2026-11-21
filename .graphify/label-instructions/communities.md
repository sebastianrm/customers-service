# Community Labeling

Graphify is running in assistant/skill mode (no API key). You are the host
assistant (Claude Code / Codex / Gemini CLI). Read the community listing below
and write 2-5 word plain-language names for each.

## Language

Write every name in English (en). Do not switch languages.

## Communities

Community 0: CustomerService, .applyUpdate(, .create(, .delete(, .evictDocumentKeys(, .findAll(, .findByDocument(, .findById(, .getCatalogs(, .isBlank(, .normalizeDocumentNumber(, .normalizeEmail(
Community 1: CustomerRepository, CustomerCatalogCacheService.java, CustomerCatalogCacheServiceTest.java, Customer, CustomerResponseMapper.java, CustomerResponseMapper, .toCreatedResponse(, .toResponse(, ReactiveMongoRepository, CustomerRepository.java, .findByDocumentNumber(, .findByDocumentTypeAndDocumentNumber(
Community 2: CustomerServiceTest, .existingCustomer(, .personalCommand(, .shouldCreateCustomerWhenDataIsValid(, .shouldDeleteCustomerLogically(, .shouldFailWhenCustomerDoesNotExist(, .shouldFailWhenDocumentNumberAlreadyExists(, .shouldFailWhenEnterpriseCustomerHasNoBusinessName(, .shouldFailWhenPersonalCustomerHasNoNames(, .shouldFailWhenUpdatedDocumentNumberBelongsToAnotherCustomer, .shouldFailWhenVipProfileIsUsedWithEnterpriseType(, .shouldListCustomersWithFilters(
Community 3: CustomerCatalogCacheService, .deserialize(, .documentKey(, .evictCustomerByDocument(, .getCatalogs(, .getCustomerByDocument(, .putCatalogs(, .putCustomerByDocument(, .serialize(
Community 4: CustomerCatalogCacheServiceTest, .customer(, .setUp(, .shouldCacheCatalogsWithConfiguredTtl(, .shouldCacheCustomerByDocumentWithConfiguredTtl(, .shouldEvictCustomerByDocument(, .shouldRetrieveCatalogsFromRedis(, .shouldRetrieveCustomerByDocumentFromRedis(, .shouldReturnEmptyWhenRedisFails(
Community 5: CustomerController, CustomerController.java, .catalogs(, .create(, .delete(, .findByDocument(, .findById(, .list(, .update(
Community 6: GlobalExceptionHandler, GlobalExceptionHandler.java, .handleBusiness(, .handleInput(, .handleResponseStatus(, .handleUnexpected(, .handleValidation(, .path(, .resolveBusinessStatus(
Community 7: CustomerEventPublisher, CustomerEventPublisher.java, .publishDeleted(, .publishRegistered(, .publishUpdated(, .send(
Community 8: BusinessException, BusinessException.java, .BusinessException(, .getCode(, RuntimeException
Community 9: CustomerEventPublisherTest, CustomerEventPublisherTest.java, .shouldPublishCustomerDeletedEventWithCorrectFields(, .shouldPublishCustomerRegisteredEventWithCorrectFields(, .shouldPublishCustomerUpdatedEventWithCorrectFields(
Community 10: CustomerSearchAdapter.java, CustomerSearchAdapter, .buildCriteria(, .search(
Community 11: OpenApiConfig.java, OpenApiConfig, .customersOpenApi(
Community 12: CustomersServiceApplication.java, CustomersServiceApplication, .main(
Community 13: ErrorCodes.java, ErrorCodes, .ErrorCodes(
Community 14: Topics.java, Topics, .Topics(
Community 15: CacheProperties.java, CacheProperties
Community 16: ApiError.java, of(
Community 17: CustomerCatalogsResponse.java, from(
Community 18: CustomerCreatedResponse.java, of(
Community 19: CustomerRequest.java, toCommand(
Community 20: PageResponse.java, of(
Community 21: CustomerCatalogs.java, defaults(
Community 22: Customer.java, Customer
Community 23: CustomerPage.java, of(
Community 24: CustomerFilter.java, of(
Community 25: CustomerResponse.java
Community 26: CustomerDeletedEvent.java
Community 27: CustomerRegisteredEvent.java
Community 28: CustomerUpdatedEvent.java
Community 29: CustomerProfile.java
Community 30: CustomerStatus.java
Community 31: CustomerType.java
Community 32: DocumentType.java
Community 33: CustomerCommand.java

## Instructions

Write a single JSON object mapping each community id (as a string) to its
2-5 word name to: c:\Users\semorale\proyectos\yanki-accounts-service\customers-service\.graphify\label-instructions\communities.json

Example:
```json
{
  "0": "Authentication Flow",
  "1": "Authentication Flow",
  "2": "Authentication Flow"
}
```

Then re-run `graphify update` (or `graphify label`) to ingest the names.
