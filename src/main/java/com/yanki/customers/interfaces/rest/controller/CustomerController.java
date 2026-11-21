package com.yanki.customers.interfaces.rest.controller;

import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.port.in.CreateCustomerUseCase;
import com.yanki.customers.domain.port.in.FindCustomerUseCase;
import com.yanki.customers.domain.port.in.ModifyCustomerUseCase;
import com.yanki.customers.domain.service.CustomerFilter;
import com.yanki.customers.interfaces.rest.dto.CustomerCatalogsResponse;
import com.yanki.customers.interfaces.rest.dto.CustomerCreatedResponse;
import com.yanki.customers.interfaces.rest.dto.CustomerRequest;
import com.yanki.customers.interfaces.rest.dto.CustomerResponse;
import com.yanki.customers.interfaces.rest.dto.CustomerResponseMapper;
import com.yanki.customers.interfaces.rest.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * API REST del maestro de clientes de la plataforma Yanki.
 *
 * <p>Es el unico servicio que expone datos de clientes: los demas
 * microservicios se enteran de los cambios mediante eventos Kafka.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Maestro de clientes de la plataforma bancaria Yanki")
public class CustomerController {

    /** Puerto de entrada para crear clientes. */
    private final CreateCustomerUseCase createCustomerUseCase;

    /** Puerto de entrada para consultar clientes. */
    private final FindCustomerUseCase findCustomerUseCase;

    /** Puerto de entrada para modificar clientes. */
    private final ModifyCustomerUseCase modifyCustomerUseCase;

    /** Conversor de entidad de dominio a DTO de respuesta. */
    private final CustomerResponseMapper customerResponseMapper;

    /**
     * Registra un cliente nuevo.
     *
     * @param request datos del cliente
     * @return cliente creado con su contrasena temporal
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un cliente",
            description = "Registra un cliente en el maestro y devuelve la contrasena temporal de primer acceso")
    public Mono<CustomerCreatedResponse> create(@Valid @RequestBody CustomerRequest request) {
        log.info("Solicitud POST /api/customers: creando cliente tipo={}, perfil={}, documento={}",
                request.type(), request.profile(), request.documentNumber());
        log.debug("Body de creacion: type={}, profile={}, documentType={}, documentNumber={}, email={}",
                request.type(), request.profile(), request.documentType(), request.documentNumber(), request.email());
        return Mono.from(createCustomerUseCase.execute(request.toCommand())
                .toFlowable()
                .map(result -> {
                    log.info("Cliente creado: id={}, documento={}", result.customer().getCustomerId(), result.customer().getDocumentNumber());
                    return customerResponseMapper.toCreatedResponse(result.customer(), result.temporaryPassword());
                }))
                .doOnError(error -> log.error("Error al crear cliente: {}", error.getMessage(), error));
    }

    /**
     * Lista los clientes con filtros y paginacion opcionales.
     *
     * @param type tipo de cliente opcional
     * @param profile perfil del cliente opcional
     * @param status estado del cliente opcional
     * @param page numero de pagina (base cero)
     * @param size cantidad de elementos por pagina
     * @return pagina de clientes
     */
    @GetMapping
    @Operation(summary = "Lista clientes",
            description = "Lista los clientes del maestro con filtros opcionales por tipo, perfil y estado")
    public Mono<PageResponse<CustomerResponse>> list(
            @Parameter(description = "Tipo de cliente")
            @RequestParam(required = false) CustomerType type,
            @Parameter(description = "Perfil del cliente")
            @RequestParam(required = false) CustomerProfile profile,
            @Parameter(description = "Estado del cliente")
            @RequestParam(required = false) CustomerStatus status,
            @Parameter(description = "Numero de pagina (base cero)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Cantidad de elementos por pagina")
            @RequestParam(defaultValue = "20") int size) {
        log.info("Solicitud GET /api/customers: tipo={}, perfil={}, status={}, pagina={}, tamaño={}",
                type, profile, status, page, size);
        CustomerFilter filter = CustomerFilter.of(type, profile, status, page, size);
        return Mono.from(findCustomerUseCase.findAll(filter).toFlowable()
                .map(result -> PageResponse.of(
                        result.content().stream().map(customerResponseMapper::toResponse).toList(),
                        result.page(), result.size(), result.totalElements())))
                .doOnSuccess(response -> log.debug("Listado de clientes completado: {} resultados de {} total",
                        response.content().size(), response.totalElements()))
                .doOnError(error -> log.error("Error al listar clientes: {}", error.getMessage(), error));
    }

    /**
     * Busca un cliente por su identificador.
     *
     * @param id identificador del cliente
     * @return cliente encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consulta un cliente por identificador")
    public Mono<CustomerResponse> findById(@Parameter(description = "Identificador del cliente") @PathVariable String id) {
        log.info("Solicitud GET /api/customers/{}: consultando cliente por id", id);
        return Mono.from(findCustomerUseCase.findById(id).toFlowable()
                .map(customer -> {
                    log.debug("Cliente encontrado: id={}, documento={}", id, customer.getDocumentNumber());
                    return customerResponseMapper.toResponse(customer);
                }))
                .doOnError(error -> log.error("Error al consultar cliente por id={}: {}", id, error.getMessage(), error));
    }

    /**
     * Actualiza un cliente.
     *
     * @param id identificador del cliente
     * @param request datos nuevos del cliente
     * @return cliente actualizado
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualiza un cliente")
    public Mono<CustomerResponse> update(@Parameter(description = "Identificador del cliente") @PathVariable String id,
                                         @Valid @RequestBody CustomerRequest request) {
        log.info("Solicitud PUT /api/customers/{}: actualizando cliente tipo={}, documento={}",
                id, request.type(), request.documentNumber());
        log.debug("Body de actualizacion para cliente={}: type={}, profile={}, documentType={}, documentNumber={}, email={}",
                id, request.type(), request.profile(), request.documentType(), request.documentNumber(), request.email());
        return Mono.from(modifyCustomerUseCase.executeUpdate(id, request.toCommand()).toFlowable()
                .map(customer -> {
                    log.info("Cliente actualizado: id={}, documento={}", id, customer.getDocumentNumber());
                    return customerResponseMapper.toResponse(customer);
                }))
                .doOnError(error -> log.error("Error al actualizar cliente id={}: {}", id, error.getMessage(), error));
    }

    /**
     * Elimina logicamente un cliente.
     *
     * @param id identificador del cliente
     * @return completable vacio cuando la eliminacion concluded
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Elimina logicamente un cliente",
            description = "Marca el cliente como DELETED y publica el evento de eliminacion")
    public Mono<Void> delete(@Parameter(description = "Identificador del cliente") @PathVariable String id) {
        log.info("Solicitud DELETE /api/customers/{}: eliminando logicamente cliente", id);
        return Mono.from(modifyCustomerUseCase.executeDelete(id).toFlowable())
                .doOnSuccess(v -> log.info("Cliente eliminado logicamente: id={}", id))
                .doOnError(error -> log.error("Error al eliminar cliente id={}: {}", id, error.getMessage(), error))
                .then(Mono.<Void>empty());
    }

    /**
     * Busca un cliente por tipo y numero de documento.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @return cliente encontrado
     */
    @GetMapping("/document/{documentType}/{documentNumber}")
    @Operation(summary = "Consulta un cliente por documento",
            description = "Consulta el maestro por tipo y numero de documento usando la cache de Redis")
    public Mono<CustomerResponse> findByDocument(
            @Parameter(description = "Tipo de documento") @PathVariable DocumentType documentType,
            @Parameter(description = "Numero de documento") @PathVariable String documentNumber) {
        log.info("Solicitud GET /api/customers/document/{}/{}: consultando por documento", documentType, documentNumber);
        return Mono.from(findCustomerUseCase.findByDocument(documentType, documentNumber).toFlowable()
                .map(customer -> {
                    log.debug("Cliente encontrado por documento: id={}, tipo={}, numero={}",
                            customer.getCustomerId(), documentType, documentNumber);
                    return customerResponseMapper.toResponse(customer);
                }))
                .doOnError(error -> log.error("Error al consultar cliente por documento {}/{}: {}",
                        documentType, documentNumber, error.getMessage(), error));
    }

    /**
     * Obtiene los catalogos maestros del servicio.
     *
     * @return catalogos de tipos de cliente, perfiles y tipos de documento
     */
    @GetMapping("/catalogs")
    @Operation(summary = "Consulta los catalogos maestros",
            description = "Devuelve los catalogos maestros servidos desde la cache de Redis")
    public Mono<CustomerCatalogsResponse> catalogs() {
        log.debug("Solicitud GET /api/customers/catalogs: consultando catalogos maestros");
        return Mono.from(findCustomerUseCase.getCatalogs().toFlowable().map(CustomerCatalogsResponse::from))
                .doOnSuccess(response -> log.debug("Catalogos maestros obtenidos: {} tipos de cliente, {} perfiles, {} tipos de documento",
                        response.customerTypes().size(), response.profiles().size(), response.documentTypes().size()))
                .doOnError(error -> log.error("Error al obtener catalogos maestros: {}", error.getMessage(), error));
    }
}
