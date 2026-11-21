package com.yanki.customers.domain.port.in;

import com.yanki.customers.domain.service.CustomerCommand;
import com.yanki.customers.domain.service.CustomerResult;
import io.reactivex.rxjava3.core.Single;

/**
 * Puerto de entrada para registrar un nuevo cliente en la plataforma.
 */
public interface CreateCustomerUseCase {

    /**
     * Registra un cliente nuevo y publica el evento de registro.
     *
     * @param command datos del cliente a registrar
     * @return cliente creado con su contrasena temporal
     */
    Single<CustomerResult> execute(CustomerCommand command);
}
