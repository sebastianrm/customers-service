package com.yanki.customers.infrastructure.persistence.adapter;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerPage;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.port.out.CustomerPersistencePort;
import com.yanki.customers.domain.service.CustomerFilter;
import com.yanki.customers.infrastructure.persistence.mapper.CustomerDocumentMapper;
import com.yanki.customers.infrastructure.persistence.repository.SpringDataCustomerRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Adaptador de salida que implementa la persistencia de clientes sobre MongoDB Reactivo.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerMongoAdapter implements CustomerPersistencePort {

    /** Repositorio Spring Data de clientes. */
    private final SpringDataCustomerRepository repository;

    /** Plantilla reactiva de MongoDB para consultas dinamicas. */
    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Maybe<Customer> findById(String id) {
        log.debug("MongoDB: buscando cliente por id={}", id);
        return Maybe.fromPublisher(repository.findById(id).map(CustomerDocumentMapper::toDomain))
                .doOnSuccess(customer -> log.debug("MongoDB: cliente encontrado por id={}: documento={}", id, customer.getDocumentNumber()))
                .doOnError(error -> log.error("MongoDB: error al buscar cliente por id={}: {}", id, error.getMessage(), error));
    }

    @Override
    public Maybe<Customer> findByDocumentNumber(String documentNumber) {
        log.debug("MongoDB: buscando cliente por documento={}", documentNumber);
        return Maybe.fromPublisher(repository.findByDocumentNumber(documentNumber).map(CustomerDocumentMapper::toDomain))
                .doOnSuccess(customer -> log.debug("MongoDB: cliente encontrado por documento={}: id={}", documentNumber, customer.getCustomerId()))
                .doOnError(error -> log.error("MongoDB: error al buscar cliente por documento={}: {}", documentNumber, error.getMessage(), error));
    }

    @Override
    public Maybe<Customer> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber) {
        log.debug("MongoDB: buscando cliente por tipo={}, documento={}", documentType, documentNumber);
        return Maybe.fromPublisher(repository.findByDocumentTypeAndDocumentNumber(documentType.name(), documentNumber)
                .map(CustomerDocumentMapper::toDomain))
                .doOnSuccess(customer -> log.debug("MongoDB: cliente encontrado por tipo={}, documento={}: id={}",
                        documentType, documentNumber, customer.getCustomerId()))
                .doOnError(error -> log.error("MongoDB: error al buscar cliente por tipo={}, documento={}: {}",
                        documentType, documentNumber, error.getMessage(), error));
    }

    @Override
    public Single<Customer> save(Customer customer) {
        log.debug("MongoDB: guardando cliente id={}, documento={}", customer.getCustomerId(), customer.getDocumentNumber());
        return Single.fromPublisher(repository.save(CustomerDocumentMapper.toDocument(customer))
                .map(CustomerDocumentMapper::toDomain))
                .doOnSuccess(saved -> log.debug("MongoDB: cliente guardado exitosamente: id={}", saved.getCustomerId()))
                .doOnError(error -> log.error("MongoDB: error al guardar cliente id={}: {}", customer.getCustomerId(), error.getMessage(), error));
    }

    @Override
    public Single<CustomerPage> search(CustomerFilter filter) {
        log.info("MongoDB: buscando clientes con filtros: tipo={}, perfil={}, status={}, pagina={}, tamaño={}",
                filter.type(), filter.profile(), filter.status(), filter.page(), filter.size());
        Criteria criteria = buildCriteria(filter);
        log.debug("MongoDB: criterio de busqueda construido: {}", criteria);
        Query countQuery = new Query(criteria);
        Query dataQuery = new Query(criteria)
                .with(PageRequest.of(filter.page(), filter.size(), Sort.by(Sort.Direction.DESC, "createdAt")));
        Mono<Long> total = mongoTemplate.count(countQuery, com.yanki.customers.infrastructure.persistence.document.CustomerDocument.class);
        Mono<List<Customer>> content = mongoTemplate.find(dataQuery,
                com.yanki.customers.infrastructure.persistence.document.CustomerDocument.class)
                .map(CustomerDocumentMapper::toDomain).collectList();
        Mono<CustomerPage> result = content.zipWith(total, (customers, totalElements) ->
                CustomerPage.of(customers, filter.page(), filter.size(), totalElements));
        return Single.fromPublisher(result)
                .doOnSuccess(page -> log.info("MongoDB: busqueda completada: {} resultados de {} total", page.content().size(), page.totalElements()))
                .doOnError(error -> log.error("MongoDB: error al buscar clientes con filtros: {}", error.getMessage(), error));
    }

    /**
     * Construye el criterio de busqueda a partir de los filtros no nulos.
     *
     * @param filter filtros de la busqueda
     * @return criterio de MongoDB
     */
    private Criteria buildCriteria(CustomerFilter filter) {
        Criteria criteria = new Criteria();
        if (filter.type() != null) {
            criteria = criteria.and("type").is(filter.type().name());
        }
        if (filter.profile() != null) {
            criteria = criteria.and("profile").is(filter.profile().name());
        }
        if (filter.status() != null) {
            criteria = criteria.and("status").is(filter.status().name());
        }
        return criteria;
    }
}
