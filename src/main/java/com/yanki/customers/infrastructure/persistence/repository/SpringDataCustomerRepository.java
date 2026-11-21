package com.yanki.customers.infrastructure.persistence.repository;

import com.yanki.customers.infrastructure.persistence.document.CustomerDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Repositorio reactivo de Spring Data para clientes.
 */
public interface SpringDataCustomerRepository extends ReactiveMongoRepository<CustomerDocument, String> {

    /**
     * Busca un cliente por su numero de documento.
     *
     * @param documentNumber numero de documento
     * @return documento encontrado o vacio
     */
    Mono<CustomerDocument> findByDocumentNumber(String documentNumber);

    /**
     * Busca un cliente por tipo y numero de documento.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @return documento encontrado o vacio
     */
    Mono<CustomerDocument> findByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);
}
