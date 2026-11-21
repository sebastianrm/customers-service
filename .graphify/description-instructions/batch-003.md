# Node Description Batch 4 of 4

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

- "model_customercatalogs": "CustomerCatalogs.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/CustomerCatalogs.java:L1 | neighbors=[defaults()]
- "model_customercatalogs_defaults": "defaults()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/CustomerCatalogs.java:L23 | neighbors=[CustomerCatalogs.java]
- "model_customerpage": "CustomerPage.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/CustomerPage.java:L1 | neighbors=[of()]
- "model_customerpage_of": "of()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/CustomerPage.java:L28 | neighbors=[CustomerPage.java]
- "repository_customerrepository_customerrepository_findbydocumentnumber": ".findByDocumentNumber()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/repository/CustomerRepository.java:L22 | neighbors=[CustomerRepository]
- "repository_customerrepository_customerrepository_findbydocumenttypeanddocumentnumber": ".findByDocumentTypeAndDocumentNumber()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/repository/CustomerRepository.java:L31 | neighbors=[CustomerRepository]
- "rest_globalexceptionhandler": "GlobalExceptionHandler.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/GlobalExceptionHandler.java:L1 | neighbors=[GlobalExceptionHandler]
- "runtimeexception": "RuntimeException" | kind=code-symbol | neighbors=[BusinessException]
- "service_customerfilter": "CustomerFilter.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerFilter.java:L1 | neighbors=[of()]
- "service_customerfilter_of": "of()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerFilter.java:L39 | neighbors=[CustomerFilter.java]
- "service_customerresult": "CustomerResult.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerResult.java:L1 | neighbors=[Customer]
- "service_customerservice_customerservice_findall": ".findAll()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L103 | neighbors=[CustomerService]
- "service_customerservice_customerservice_getcatalogs": ".getCatalogs()" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerService.java:L190 | neighbors=[CustomerService]
- "service_customerservicetest_customerservicetest_shouldfailwhencustomerdoesnotexist": ".shouldFailWhenCustomerDoesNotExist()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L241 | neighbors=[CustomerServiceTest]
- "service_customerservicetest_customerservicetest_shouldfailwhenenterprisecustomerhasnobusinessname": ".shouldFailWhenEnterpriseCustomerHasNoBusinessName()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L126 | neighbors=[CustomerServiceTest]
- "service_customerservicetest_customerservicetest_shouldfailwhenpersonalcustomerhasnonames": ".shouldFailWhenPersonalCustomerHasNoNames()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L140 | neighbors=[CustomerServiceTest]
- "service_customerservicetest_customerservicetest_shouldfailwhenvipprofileisusedwithenterprisetype": ".shouldFailWhenVipProfileIsUsedWithEnterpriseType()" | kind=code-symbol | source=src/test/java/com/yanki/customers/domain/service/CustomerServiceTest.java:L112 | neighbors=[CustomerServiceTest]
- "string": "String" | kind=code-symbol | neighbors=[CustomerRepository]
- "dto_customerresponse": "CustomerResponse.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/interfaces/rest/dto/CustomerResponse.java:L1
- "event_customerdeletedevent": "CustomerDeletedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/event/CustomerDeletedEvent.java:L1
- "event_customerregisteredevent": "CustomerRegisteredEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/event/CustomerRegisteredEvent.java:L1
- "event_customerupdatedevent": "CustomerUpdatedEvent.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/event/CustomerUpdatedEvent.java:L1
- "model_customerprofile": "CustomerProfile.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/CustomerProfile.java:L1
- "model_customerstatus": "CustomerStatus.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/CustomerStatus.java:L1
- "model_customertype": "CustomerType.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/CustomerType.java:L1
- "model_documenttype": "DocumentType.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/model/DocumentType.java:L1
- "service_customercommand": "CustomerCommand.java" | kind=code-symbol | source=src/main/java/com/yanki/customers/domain/service/CustomerCommand.java:L1

## Instructions

Write a single JSON object mapping each node id to a one-sentence description
to: c:\Users\semorale\proyectos\yanki-accounts-service\customers-service\.graphify\description-instructions\batch-003.json

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
