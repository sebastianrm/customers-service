# Graph Report - .  (2026-11-08)

## Corpus Check
- Corpus is ~8,979 words - fits in a single context window. You may not need a graph.

## Summary
- 156 nodes · 178 edges · 18 communities detected
- Extraction: 100% EXTRACTED · 0% INFERRED · 0% AMBIGUOUS
- Token cost: 0 input · 0 output
- Edge kinds: method: 86 · calls: 50 · contains: 28 · imports: 10 · inherits: 4


## Input Scope
- Requested: auto
- Resolved: all (source: default-auto)
- Included files: 38 · Candidates: recursive
- Excluded: 0 untracked · 0 ignored · 2 sensitive · 0 missing committed
## God Nodes (most connected - your core abstractions)
1. `CustomerService` - 21 edges
2. `CustomerServiceTest` - 13 edges
3. `CustomerCatalogCacheService` - 9 edges
4. `CustomerCatalogCacheServiceTest` - 9 edges
5. `GlobalExceptionHandler` - 8 edges
6. `CustomerController` - 8 edges
7. `CustomerRepository` - 6 edges
8. `CustomerEventPublisher` - 5 edges
9. `CustomerControllerTest` - 5 edges
10. `BusinessException` - 4 edges

## Surprising Connections (you probably didn't know these)
- None detected - all connections are within the same source files.

## Communities

### Community 0 - "Community 0"
Cohesion: 0.20
Nodes (1): CustomerService

### Community 1 - "Community 1"
Cohesion: 0.13
Nodes (6): Customer, CustomerResponseMapper, ReactiveMongoRepository, CustomerRepository, AdditionalCustomerServiceTests, String

### Community 2 - "Community 2"
Cohesion: 0.22
Nodes (1): CustomerServiceTest

### Community 3 - "Community 3"
Cohesion: 0.42
Nodes (1): CustomerCatalogCacheService

### Community 4 - "Community 4"
Cohesion: 0.28
Nodes (1): CustomerCatalogCacheServiceTest

### Community 5 - "Community 5"
Cohesion: 0.22
Nodes (1): CustomerController

### Community 6 - "Community 6"
Cohesion: 0.39
Nodes (1): GlobalExceptionHandler

### Community 7 - "Community 7"
Cohesion: 0.33
Nodes (1): CustomerControllerTest

### Community 8 - "Community 8"
Cohesion: 0.53
Nodes (1): CustomerEventPublisher

### Community 9 - "Community 9"
Cohesion: 0.40
Nodes (2): BusinessException, RuntimeException

### Community 10 - "Community 10"
Cohesion: 0.40
Nodes (1): CustomerEventPublisherTest

### Community 11 - "Community 11"
Cohesion: 0.67
Nodes (1): CustomerSearchAdapter

### Community 12 - "Community 12"
Cohesion: 0.67
Nodes (1): OpenApiConfig

### Community 13 - "Community 13"
Cohesion: 0.67
Nodes (1): CustomersServiceApplication

### Community 14 - "Community 14"
Cohesion: 0.67
Nodes (1): ErrorCodes

### Community 15 - "Community 15"
Cohesion: 0.67
Nodes (1): Topics

### Community 16 - "Community 16"
Cohesion: 1.00
Nodes (1): CacheProperties

### Community 23 - "Community 23"
Cohesion: 1.00
Nodes (1): Customer

## Knowledge Gaps
- **2 isolated node(s):** `Customer`, `CacheProperties`
  These have ≤1 connection - possible missing edges or undocumented components.
- **Thin community `Community 0`** (1 nodes): `CustomerService`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 2`** (1 nodes): `CustomerServiceTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 3`** (1 nodes): `CustomerCatalogCacheService`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 4`** (1 nodes): `CustomerCatalogCacheServiceTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 5`** (1 nodes): `CustomerController`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 6`** (1 nodes): `GlobalExceptionHandler`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 7`** (1 nodes): `CustomerControllerTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 8`** (1 nodes): `CustomerEventPublisher`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 9`** (2 nodes): `BusinessException`, `RuntimeException`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 10`** (1 nodes): `CustomerEventPublisherTest`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 11`** (1 nodes): `CustomerSearchAdapter`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 12`** (1 nodes): `OpenApiConfig`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 13`** (1 nodes): `CustomersServiceApplication`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 14`** (1 nodes): `ErrorCodes`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 15`** (1 nodes): `Topics`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 16`** (1 nodes): `CacheProperties`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.
- **Thin community `Community 23`** (1 nodes): `Customer`
  Too small to be a meaningful cluster - may be noise or needs more connections extracted.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `CustomerService` connect `Community 0` to `Community 1`?**
  _High betweenness centrality (0.102) - this node is a cross-community bridge._
- **Why does `CustomerCatalogCacheServiceTest` connect `Community 4` to `Community 1`?**
  _High betweenness centrality (0.046) - this node is a cross-community bridge._
- **What connects `Customer`, `CacheProperties` to the rest of the system?**
  _2 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `Community 1` be split into smaller, more focused modules?**
  _Cohesion score 0.13071895424836602 - nodes in this community are weakly interconnected._