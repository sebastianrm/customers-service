package com.yanki.customers.interfaces.rest.dto;

import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Respuesta con los datos de un cliente.
 *
 * <p>Nunca incluye la contrasena temporal: esa credencial solo se devuelve en
 * la respuesta de creacion.</p>
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
 */
@Schema(name = "CustomerResponse", description = "Datos de un cliente del maestro")
public record CustomerResponse(
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
        LocalDateTime updatedAt) {
}
