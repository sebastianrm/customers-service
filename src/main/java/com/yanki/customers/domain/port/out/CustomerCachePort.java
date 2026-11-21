package com.yanki.customers.domain.port.out;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerCatalogs;
import com.yanki.customers.domain.model.DocumentType;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

/**
 * Puerto de salida para la cache de clientes y catalogos.
 */
public interface CustomerCachePort {

    /**
     * Obtiene los catalogos maestros desde la cache.
     *
     * @return catalogos cacheados o vacio
     */
    Maybe<CustomerCatalogs> getCatalogs();

    /**
     * Guarda los catalogos maestros en la cache.
     *
     * @param catalogs catalogos a guardar
     * @return completable cuando se completa
     */
    Completable putCatalogs(CustomerCatalogs catalogs);

    /**
     * Obtiene un cliente desde la cache por documento.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @return cliente cacheado o vacio
     */
    Maybe<Customer> getCustomerByDocument(DocumentType documentType, String documentNumber);

    /**
     * Guarda un cliente en la cache por documento.
     *
     * @param customer cliente a guardar
     * @return cliente almacenado
     */
    Maybe<Customer> putCustomerByDocument(Customer customer);

    /**
     * Invalida la cache de un cliente por documento.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @return completable cuando se completa
     */
    Completable evictCustomerByDocument(DocumentType documentType, String documentNumber);
}
