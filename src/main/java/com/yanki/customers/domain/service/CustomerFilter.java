package com.yanki.customers.domain.service;

import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;

/**
 * Filtro de busqueda y paginacion de clientes.
 *
 * @param type tipo de cliente opcional
 * @param profile perfil del cliente opcional
 * @param status estado del cliente opcional
 * @param page numero de pagina (base cero)
 * @param size cantidad de elementos por pagina
 */
public record CustomerFilter(
        CustomerType type,
        CustomerProfile profile,
        CustomerStatus status,
        int page,
        int size) {

    /** Cantidad maxima de elementos admitidos por pagina. */
    public static final int MAX_SIZE = 100;

    /** Cantidad de elementos por pagina por defecto. */
    public static final int DEFAULT_SIZE = 20;

    /**
     * Crea un filtro normalizando la paginacion.
     *
     * @param type tipo de cliente opcional
     * @param profile perfil del cliente opcional
     * @param status estado del cliente opcional
     * @param page numero de pagina (base cero)
     * @param size cantidad de elementos por pagina
     * @return filtro con la paginacion normalizada
     */
    public static CustomerFilter of(CustomerType type, CustomerProfile profile, CustomerStatus status, int page, int size) {
        int normalizedPage = Math.max(page, 0);
        int normalizedSize = Math.min(Math.max(size, 1), MAX_SIZE);
        return new CustomerFilter(type, profile, status, normalizedPage, normalizedSize);
    }
}
