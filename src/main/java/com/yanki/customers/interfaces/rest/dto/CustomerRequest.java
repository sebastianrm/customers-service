package com.yanki.customers.interfaces.rest.dto;

import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.service.CustomerCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Datos de entrada para registrar o actualizar un cliente.
 *
 * @param type tipo de cliente
 * @param profile perfil del cliente
 * @param documentType tipo de documento
 * @param documentNumber numero de documento
 * @param businessName nombre comercial (solo clientes empresariales)
 * @param firstName nombres (solo clientes personales)
 * @param lastName apellidos (solo clientes personales)
 * @param email correo electronico
 * @param phone telefono de contacto
 * @param address direccion
 * @param birthDate fecha de nacimiento (solo clientes personales)
 * @param status estado deseado (solo se aplica en la actualizacion)
 * @param active indicador de operacion (solo se aplica en la actualizacion)
 */
@Schema(name = "CustomerRequest", description = "Datos para registrar o actualizar un cliente")
public record CustomerRequest(
        @Schema(description = "Tipo de cliente", example = "PERSONAL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El tipo de cliente es obligatorio")
        CustomerType type,

        @Schema(description = "Perfil del cliente", example = "STANDARD", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El perfil del cliente es obligatorio")
        CustomerProfile profile,

        @Schema(description = "Tipo de documento", example = "DNI", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "El tipo de documentacion es obligatorio")
        DocumentType documentType,

        @Schema(description = "Numero de documento", example = "70123456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El numero de documento es obligatorio")
        String documentNumber,

        @Schema(description = "Nombre comercial del cliente empresarial", example = "Acme S.A.C.")
        String businessName,

        @Schema(description = "Nombres del cliente personal", example = "Juan")
        String firstName,

        @Schema(description = "Apellidos del cliente personal", example = "Perez")
        String lastName,

        @Schema(description = "Correo electronico", example = "juan.perez@correo.com",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El correo electronico es obligatorio")
        @Email(message = "El correo electronico no tiene un formato valido")
        String email,

        @Schema(description = "Telefono de contacto", example = "+51987654321")
        String phone,

        @Schema(description = "Direccion del cliente", example = "Av. Javier Prado Este 4200")
        String address,

        @Schema(description = "Fecha de nacimiento del cliente personal", example = "1990-05-20")
        LocalDate birthDate,

        @Schema(description = "Estado del cliente, solo se aplica en la actualizacion", example = "ACTIVE")
        CustomerStatus status,

        @Schema(description = "Indicador de operacion, solo se aplica en la actualizacion", example = "true")
        Boolean active) {

    /**
     * Convierte la solicitud en el comando de dominio.
     *
     * @return comando con los datos del cliente
     */
    public CustomerCommand toCommand() {
        return new CustomerCommand(type, profile, documentType, documentNumber, businessName, firstName, lastName,
                email, phone, address, birthDate, status, active);
    }
}
