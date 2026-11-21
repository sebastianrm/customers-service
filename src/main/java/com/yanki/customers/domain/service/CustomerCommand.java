package com.yanki.customers.domain.service;

import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import java.time.LocalDate;

/**
 * Comando de creacion y actualizacion de un cliente.
 *
 * <p>El estado y el indicador {@code active} son opcionales y solo se toman
 * en cuenta durante la actualizacion: al crear, el cliente queda siempre
 * {@code ACTIVE} y habilitado.</p>
 *
 * @param type tipo de cliente
 * @param profile perfil del cliente
 * @param documentType tipo de documento
 * @param documentNumber numero de documento
 * @param businessName razon social (solo empresarial)
 * @param firstName nombres (solo personal)
 * @param lastName apellidos (solo personal)
 * @param email correo electronico
 * @param phone telefono de contacto
 * @param address direccion
 * @param birthDate fecha de nacimiento (solo personal)
 * @param status estado deseado (solo actualizacion)
 * @param active indicador de operacion (solo actualizacion)
 */
public record CustomerCommand(
        CustomerType type,
        CustomerProfile profile,
        DocumentType documentType,
        String documentNumber,
        String businessName,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDate birthDate,
        CustomerStatus status,
        Boolean active) {
}
