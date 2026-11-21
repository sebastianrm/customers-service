package com.yanki.customers.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Maestro de clientes de la plataforma Yanki.
 *
 * <p>Es la unica fuente de verdad sobre clientes. El resto de microservicios
 * no consulta esta base de datos: se enteran de los cambios mediante los
 * eventos Kafka publicados por este servicio.</p>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    /** Identificador unico y auto generado del cliente. */
    private String customerId;

    /** Tipo de cliente (personal o empresarial). */
    private CustomerType type;

    /** Perfil del cliente. */
    private CustomerProfile profile;

    /** Estado actual del cliente. */
    private CustomerStatus status;

    /** Tipo de documento de identidad. */
    private DocumentType documentType;

    /** Numero de documento, unico en toda la plataforma. */
    private String documentNumber;

    /** Razon social, solo para clientes empresariales. */
    private String businessName;

    /** Nombres, solo para clientes personales. */
    private String firstName;

    /** Apellidos, solo para clientes personales. */
    private String lastName;

    /** Correo electronico del cliente. */
    private String email;

    /** Telefono de contacto del cliente. */
    private String phone;

    /** Direccion del cliente. */
    private String address;

    /** Fecha de nacimiento, solo para clientes personales. */
    private LocalDate birthDate;

    /** Indica si el cliente esta habilitado para operar. */
    private boolean active;

    /** Fecha de creacion del registro. */
    private LocalDateTime createdAt;

    /** Fecha de la ultima actualizacion del registro. */
    private LocalDateTime updatedAt;
}
