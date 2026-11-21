package com.yanki.customers.domain.port.in;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.service.CustomerCommand;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de entrada para modificar clientes existentes.
 */
public interface ModifyCustomerUseCase {

    /**
     * Actualiza un cliente y publica el evento de actualizacion.
     *
     * @param id identificador del cliente
     * @param command datos nuevos del cliente
     * @return cliente actualizado
     */
    Single<Customer> executeUpdate(String id, CustomerCommand command);

    /**
     * Elimina logicamente un cliente y publica el evento de eliminacion.
     *
     * @param id identificador del cliente
     * @return completable que se completa cuando el cliente fue eliminado
     */
    Completable executeDelete(String id);
}
