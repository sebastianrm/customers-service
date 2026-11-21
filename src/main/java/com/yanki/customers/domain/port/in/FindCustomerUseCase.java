package com.yanki.customers.domain.port.in;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerCatalogs;
import com.yanki.customers.domain.model.CustomerPage;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.service.CustomerFilter;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de entrada para consultar clientes.
 */
public interface FindCustomerUseCase {

    /**
     * Busca un cliente por su identificador.
     *
     * @param id identificador del cliente
     * @return cliente encontrado
     */
    Single<Customer> findById(String id);

    /**
     * Busca un cliente por tipo y numero de documento.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @return cliente encontrado
     */
    Single<Customer> findByDocument(DocumentType documentType, String documentNumber);

    /**
     * Lista los clientes que cumplen los filtros indicados.
     *
     * @param filter filtros y paginacion de la busqueda
     * @return pagina de clientes
     */
    Single<CustomerPage> findAll(CustomerFilter filter);

    /**
     * Obtiene los catalogos maestros.
     *
     * @return catalogos de tipos de cliente, perfiles y tipos de documento
     */
    Single<CustomerCatalogs> getCatalogs();
}
