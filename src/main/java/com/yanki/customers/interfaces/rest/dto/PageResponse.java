package com.yanki.customers.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Pagina estandar de la API.
 *
 * <p>Sustituye a {@code org.springframework.data.domain.Page} para no exponer
 * tipos internos del framework en el contrato publico.</p>
 *
 * @param content elementos de la pagina
 * @param page numero de pagina (base cero)
 * @param size cantidad de elementos por pagina
 * @param totalElements cantidad total de elementos que cumplen el filtro
 * @param totalPages cantidad total de paginas
 * @param <T> tipo de los elementos
 */
@Schema(name = "PageResponse", description = "Pagina de resultados")
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    /**
     * Crea una pagina de resultados calculando el total de paginas.
     *
     * @param content elementos de la pagina
     * @param page numero de pagina (base cero)
     * @param size cantidad de elementos por pagina
     * @param totalElements cantidad total de elementos que cumplen el filtro
     * @param <T> tipo de los elementos
     * @return pagina de resultados
     */
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        int totalPages = size > 0 ? (int) Math.ceil((double) totalElements / size) : 0;
        return new PageResponse<>(content, page, size, totalElements, totalPages);
    }
}
