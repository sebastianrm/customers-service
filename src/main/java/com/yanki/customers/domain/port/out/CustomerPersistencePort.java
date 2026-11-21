package com.yanki.customers.domain.port.out;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerPage;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.service.CustomerFilter;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de salida para la persistencia de clientes.
 */
public interface CustomerPersistencePort {

    /**
     * Busca un cliente por su identificador.
     *
     * @param id identificador del cliente
     * @return cliente encontrado o vacio
     */
    Maybe<Customer> findById(String id);

    /**
     * Busca un cliente por su numero de documento.
     *
     * @param documentNumber numero de documento
     * @return cliente encontrado o vacio
     */
    Maybe<Customer> findByDocumentNumber(String documentNumber);

    /**
     * Busca un cliente por tipo y numero de documento.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @return cliente encontrado o vacio
     */
    Maybe<Customer> findByDocumentTypeAndDocumentNumber(DocumentType documentType, String documentNumber);

    /**
     * Guarda o actualiza un cliente en el almacenamiento.
     *
     * @param customer entidad de dominio a persistir
     * @return customer persistido
     */
    Single<Customer> save(Customer customer);

    /**
     * Busca clientes con filtros dinamicos y paginacion.
     *
     * @param filter filtros y paginacion
     * @return pagina de clientes
     */
    Single<CustomerPage> search(CustomerFilter filter);
}
