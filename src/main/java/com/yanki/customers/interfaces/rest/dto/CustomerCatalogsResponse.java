package com.yanki.customers.interfaces.rest.dto;

import com.yanki.customers.domain.model.CustomerCatalogs;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Respuesta con los catalogos maestros del servicio.
 *
 * @param customerTypes tipos de cliente soportados
 * @param profiles perfiles soportados
 * @param documentTypes tipos de documento soportados
 */
@Schema(name = "CustomerCatalogsResponse", description = "Catalogos maestros de clientes")
public record CustomerCatalogsResponse(
        List<String> customerTypes,
        List<String> profiles,
        List<String> documentTypes) {

    /**
     * Convierte el catalogo de dominio en la respuesta de la API.
     *
     * @param catalogs catalogo de dominio
     * @return respuesta con los catalogos maestros
     */
    public static CustomerCatalogsResponse from(CustomerCatalogs catalogs) {
        return new CustomerCatalogsResponse(catalogs.customerTypes(), catalogs.profiles(), catalogs.documentTypes());
    }
}
