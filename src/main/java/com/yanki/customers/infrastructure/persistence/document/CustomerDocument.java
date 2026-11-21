package com.yanki.customers.infrastructure.persistence.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Documento MongoDB para la coleccion de clientes.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class CustomerDocument {

    /** Identificador unico y auto generado del cliente. */
    @Id
    private String customerId;

    /** Tipo de cliente (personal o empresarial). */
    private String type;

    /** Perfil del cliente. */
    private String profile;

    /** Estado actual del cliente. */
    private String status;

    /** Tipo de documento de identidad. */
    private String documentType;

    /** Numero de documento, unico en toda la plataforma. */
    @Indexed(name = "documentNumber_1", unique = true)
    private String documentNumber;

    /** Razon social, solo para clientes empresariales. */
    private String businessName;

    /** Nombres, solo para clientes personales. */
    private String firstName;

    /** Apellidos, solo para clientes personales. */
    private String lastName;

    /** Correo electronico del cliente. */
    @Indexed(name = "email_1")
    private String email;

    /** Telefono de contacto del cliente. */
    @Indexed(name = "phone_1")
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
