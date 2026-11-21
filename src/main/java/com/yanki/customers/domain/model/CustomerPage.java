package com.yanki.customers.domain.model;

import java.util.List;

/**
 * Pagina de clientes preparada por la capa de dominio.
 *
 * <p>Evita exponer la clase {@code Page} de Spring Data en la API publica
 * del microservicio.</p>
 *
 * @param content clientes de la pagina
 * @param page numero de pagina (base cero)
 * @param size cantidad de elementos por pagina
 * @param totalElements cantidad total de clientes que cumplen el filtro
 * @param totalPages cantidad total de paginas
 */
public record CustomerPage(List<Customer> content, int page, int size, long totalElements, int totalPages) {

    /**
     * Crea una pagina de clientes calculando el total de paginas.
     *
     * @param content clientes de la pagina
     * @param page numero de pagina (base cero)
     * @param size cantidad de elementos por pagina
     * @param totalElements cantidad total de clientes que cumplen el filtro
     * @return pagina de clientes
     */
    public static CustomerPage of(List<Customer> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new CustomerPage(content, page, size, totalElements, totalPages);
    }
}
