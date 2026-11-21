package com.yanki.customers.interfaces.rest.dto;

import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Respuesta de la creacion de un cliente.
 *
 * <p>Incluye la contrasena temporal que el cliente debera cambiar en su primer
 * acceso. Esta credencial no se devuelve en ninguna otra operacion.</p>
 *
 * @param customerId identificador del cliente
 * @param type tipo de cliente
 * @param profile perfil del cliente
 * @param status estado del cliente
 * @param documentType tipo de documento
 * @param documentNumber numero de documento
 * @param businessName nombre comercial (solo clientes empresariales)
 * @param firstName nombres (solo clientes personales)
 * @param lastName apellidos (solo clientes personales)
 * @param email correo electronico
 * @param phone telefono de contacto
 * @param address direccion
 * @param birthDate fecha de nacimiento (solo clientes personales)
 * @param active indicador de operacion
 * @param createdAt fecha de creacion
 * @param updatedAt fecha de la ultima actualizacion
 * @param temporaryPassword contrasena temporal generada para el cliente
 */
@Schema(name = "CustomerCreatedResponse", description = "Cliente creado con su contrasena temporal")
public record CustomerCreatedResponse(
        String customerId,
        CustomerType type,
        CustomerProfile profile,
        CustomerStatus status,
        DocumentType documentType,
        String documentNumber,
        String businessName,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDate birthDate,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String temporaryPassword) {

    /**
     * Construye la respuesta de creacion a partir de los datos del cliente.
     *
     * @param customer datos del cliente creado
     * @param temporaryPassword contrasena temporal generada
     * @return respuesta de creacion
     */
    public static CustomerCreatedResponse of(CustomerResponse customer, String temporaryPassword) {
        return new CustomerCreatedResponse(customer.customerId(), customer.type(), customer.profile(), customer.status(),
                customer.documentType(), customer.documentNumber(), customer.businessName(), customer.firstName(),
                customer.lastName(), customer.email(), customer.phone(), customer.address(), customer.birthDate(),
                customer.active(), customer.createdAt(), customer.updatedAt(), temporaryPassword);
    }
}
