package com.yanki.customers.domain.model;

import java.util.Arrays;
import java.util.List;

/**
 * Catalogo de datos maestros del servicio de clientes.
 *
 * <p>Se entrega desde la cache de Redis para no recalcular los catalogos
 * en cada consulta.</p>
 *
 * @param customerTypes tipos de cliente soportados
 * @param profiles perfiles soportados
 * @param documentTypes tipos de documento soportados
 */
public record CustomerCatalogs(List<String> customerTypes, List<String> profiles, List<String> documentTypes) {

    /**
     * Construye el catalogo maestro a partir de los enums del dominio.
     *
     * @return catalogo con los valores soportados por el dominio
     */
    public static CustomerCatalogs defaults() {
        return new CustomerCatalogs(
                Arrays.stream(CustomerType.values()).map(CustomerType::name).toList(),
                Arrays.stream(CustomerProfile.values()).map(CustomerProfile::name).toList(),
                Arrays.stream(DocumentType.values()).map(DocumentType::name).toList());
    }
}
