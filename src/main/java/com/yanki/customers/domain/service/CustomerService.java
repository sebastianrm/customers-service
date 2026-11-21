package com.yanki.customers.domain.service;

import com.yanki.customers.domain.event.CustomerDeletedEvent;
import com.yanki.customers.domain.event.CustomerRegisteredEvent;
import com.yanki.customers.domain.event.CustomerUpdatedEvent;
import com.yanki.customers.domain.exception.BusinessException;
import com.yanki.customers.domain.exception.ErrorCodes;
import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerCatalogs;
import com.yanki.customers.domain.model.CustomerPage;
import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.port.in.CreateCustomerUseCase;
import com.yanki.customers.domain.port.in.FindCustomerUseCase;
import com.yanki.customers.domain.port.in.ModifyCustomerUseCase;
import com.yanki.customers.domain.port.out.CustomerCachePort;
import com.yanki.customers.domain.port.out.CustomerEventPublisherPort;
import com.yanki.customers.domain.port.out.CustomerPersistencePort;
import com.yanki.customers.domain.port.out.TemporaryPasswordPort;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio del maestro de clientes.
 *
 * <p>Implementa los casos de uso (puertos de entrada) y orquesta unicamente
 * puertos de salida. Contiene todas las reglas de negocio del servicio.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService
        implements CreateCustomerUseCase, FindCustomerUseCase, ModifyCustomerUseCase {

    /** Rol base de todo cliente de la plataforma. */
    private static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";

    /** Rol adicional de los clientes empresariales. */
    private static final String ROLE_ENTERPRISE = "ROLE_ENTERPRISE";

    /** Puerto de salida para persistencia de clientes. */
    private final CustomerPersistencePort persistencePort;

    /** Puerto de salida para publicacion de eventos Kafka. */
    private final CustomerEventPublisherPort eventPublisherPort;

    /** Puerto de salida para cache de clientes y catalogos. */
    private final CustomerCachePort cachePort;

    /** Puerto de salida para generacion de contrasenas temporales. */
    private final TemporaryPasswordPort passwordPort;

    @Override
    public Single<CustomerResult> execute(CustomerCommand command) {
        log.info("Creando cliente nuevo: tipo={}, perfil={}, documento={}", command.type(), command.profile(), command.documentNumber());
        return Single.defer(() -> {
            try {
                validateCommand(command);
                log.debug("Comando de creacion validado exitosamente para documento={}", command.documentNumber());
            } catch (BusinessException e) {
                log.warn("Validacion fallida para creacion de cliente: tipo={}, perfil={}, documento={}: {}",
                        command.type(), command.profile(), command.documentNumber(), e.getMessage());
                throw e;
            }
            String documentNumber = normalizeDocumentNumber(command.documentNumber());
            String email = normalizeEmail(command.email());
            log.debug("Buscando cliente existente por documento={}", documentNumber);
            return persistencePort.findByDocumentNumber(documentNumber)
                    .flatMapSingle(existing -> {
                        log.warn("Intento de creacion duplicado: documento={} ya registrado como cliente={}",
                                documentNumber, existing.getCustomerId());
                        return Single.<CustomerResult>error(new BusinessException(
                                ErrorCodes.CUSTOMER_ALREADY_EXISTS,
                                "El cliente con documento " + documentNumber + " ya esta registrado"));
                    })
                    .switchIfEmpty(Single.defer(() -> {
                        log.debug("No existe cliente con documento={}, procediendo con la creacion", documentNumber);
                        return persistNewCustomer(command, documentNumber, email);
                    }));
        });
    }

    @Override
    public Single<Customer> findById(String id) {
        log.debug("Buscando cliente por id={}", id);
        return persistencePort.findById(id)
                .switchIfEmpty(Single.<Customer>error(() -> {
                    log.warn("Cliente no encontrado por id={}", id);
                    return new BusinessException(ErrorCodes.CUSTOMER_NOT_FOUND,
                            "No existe un cliente con id " + id);
                }));
    }

    @Override
    public Single<CustomerPage> findAll(CustomerFilter filter) {
        log.info("Listando clientes con filtros: tipo={}, perfil={}, status={}, pagina={}, tamaño={}",
                filter.type(), filter.profile(), filter.status(), filter.page(), filter.size());
        return persistencePort.search(filter)
                .doOnSuccess(page -> log.debug("Se encontraron {} clientes (total={})", page.content().size(), page.totalElements()))
                .doOnError(error -> log.error("Error al listar clientes: {}", error.getMessage(), error));
    }

    @Override
    public Single<Customer> findByDocument(DocumentType documentType, String documentNumber) {
        log.info("Buscando cliente por documento: tipo={}, numero={}", documentType, documentNumber);
        return Single.defer(() -> {
            if (documentType == null) {
                log.warn("Busqueda por documento fallida: tipo de documento nulo");
                return Single.<Customer>error(new BusinessException(ErrorCodes.INVALID_DOCUMENT_TYPE,
                        "El tipo de documento es obligatorio"));
            }
            if (isBlank(documentNumber)) {
                log.warn("Busqueda por documento fallida: numero de documento vacio");
                return Single.<Customer>error(new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA,
                        "El numero de documento es obligatorio"));
            }
            String normalizedNumber = normalizeDocumentNumber(documentNumber);
            log.debug("Consultando cache para documento: tipo={}, numero={}", documentType, normalizedNumber);
            Maybe<Customer> fromCache = cachePort.getCustomerByDocument(documentType, normalizedNumber);
            Maybe<Customer> fromDb = Maybe.defer(() -> {
                log.debug("Cache MISS para documento={}, consultando MongoDB", normalizedNumber);
                return persistencePort.findByDocumentTypeAndDocumentNumber(documentType, normalizedNumber)
                        .flatMap(customer -> {
                            log.debug("Cliente encontrado en MongoDB, guardando en cache: documento={}", normalizedNumber);
                            return cachePort.putCustomerByDocument(customer);
                        });
            });
            return fromCache.switchIfEmpty(fromDb)
                    .doOnSuccess(customer -> log.info("Cliente encontrado por documento: id={}, documento={}", customer.getCustomerId(), normalizedNumber))
                    .toSingle()
                    .onErrorResumeNext(e -> {
                        if (e instanceof java.util.NoSuchElementException) {
                            log.warn("Cliente no encontrado por documento: tipo={}, numero={}", documentType, normalizedNumber);
                            return Single.<Customer>error(new BusinessException(
                                    ErrorCodes.CUSTOMER_NOT_FOUND,
                                    "No existe un cliente con documento " + normalizedNumber));
                        }
                        log.error("Error inesperado al buscar cliente por documento: tipo={}, numero={}: {}",
                                documentType, normalizedNumber, e.getMessage(), e);
                        return Single.error(e);
                    });
        });
    }

    @Override
    public Single<CustomerCatalogs> getCatalogs() {
        log.debug("Consultando catalogos maestros");
        return cachePort.getCatalogs()
                .doOnSuccess(catalogs -> log.debug("Catalogos obtenidos desde cache de Redis"))
                .switchIfEmpty(Single.defer(() -> {
                    log.debug("Cache MISS para catalogos, cargando valores por defecto");
                    CustomerCatalogs catalogs = CustomerCatalogs.defaults();
                    return cachePort.putCatalogs(catalogs)
                            .doOnComplete(() -> log.info("Catalogos maestros cargados y guardados en cache"))
                            .andThen(Single.just(catalogs));
                }))
                .doOnError(error -> log.error("Error al obtener catalogos maestros: {}", error.getMessage(), error));
    }

    @Override
    public Single<Customer> executeUpdate(String id, CustomerCommand command) {
        log.info("Actualizando cliente: id={}, tipo={}, perfil={}, documento={}", id, command.type(), command.profile(), command.documentNumber());
        return findById(id)
                .flatMap(existing -> Single.defer(() -> {
                    try {
                        validateCommand(command);
                        validateStatusChange(command);
                        log.debug("Comando de actualizacion validado para cliente={}", id);
                    } catch (BusinessException e) {
                        log.warn("Validacion fallida para actualizacion de cliente id={}: {}", id, e.getMessage());
                        throw e;
                    }
                    String documentNumber = normalizeDocumentNumber(command.documentNumber());
                    if (existing.getDocumentNumber() != null
                            && existing.getDocumentNumber().equalsIgnoreCase(documentNumber)) {
                        log.debug("Documento sin cambio para cliente={}, aplicando actualizacion", id);
                        return applyUpdate(existing, command, documentNumber);
                    }
                    log.debug("Documento cambiado para cliente={}, verificando disponibilidad del nuevo documento={}", id, documentNumber);
                    return persistencePort.findByDocumentNumber(documentNumber)
                            .flatMapSingle(found -> {
                                log.warn("Intento de actualizar documento a {} (cliente={}), pero ya esta en uso por cliente={}",
                                        documentNumber, id, found.getCustomerId());
                                return Single.<Customer>error(new BusinessException(
                                        ErrorCodes.CUSTOMER_ALREADY_EXISTS,
                                        "El cliente con documento " + documentNumber + " ya esta registrado"));
                            })
                            .switchIfEmpty(Single.defer(() -> applyUpdate(existing, command, documentNumber)));
                }));
    }

    @Override
    public Completable executeDelete(String id) {
        log.info("Eliminando logicamente cliente: id={}", id);
        return findById(id)
                .flatMapCompletable(existing -> {
                    if (existing.getStatus() == CustomerStatus.DELETED) {
                        log.warn("Intento de eliminar cliente ya eliminado: id={}", id);
                        return Completable.error(new BusinessException(ErrorCodes.CUSTOMER_ALREADY_DELETED,
                                "El cliente ya fue eliminado logicamente"));
                    }
                    LocalDateTime occurredAt = LocalDateTime.now();
                    existing.setStatus(CustomerStatus.DELETED);
                    existing.setActive(false);
                    existing.setUpdatedAt(occurredAt);
                    log.debug("Marcando cliente como DELETED: id={}, documento={}", id, existing.getDocumentNumber());
                    return persistencePort.save(existing)
                            .flatMapCompletable(saved -> {
                                log.info("Cliente eliminado logicamente: id={}, documento={}", saved.getCustomerId(), saved.getDocumentNumber());
                                return eventPublisherPort
                                        .publishDeleted(new CustomerDeletedEvent(saved.getCustomerId(), occurredAt))
                                        .andThen(cachePort
                                                .evictCustomerByDocument(saved.getDocumentType(), saved.getDocumentNumber()))
                                        .doOnComplete(() -> log.debug("Evento de eliminacion publicado y cache invalidada para cliente={}", id))
                                        .doOnError(error -> log.error("Error al publicar evento de eliminacion para cliente={}: {}", id, error.getMessage(), error));
                            });
                })
                .doOnError(error -> log.error("Error al eliminar cliente id={}: {}", id, error.getMessage(), error));
    }

    /**
     * Persiste un cliente nuevo y publica el evento de registro.
     *
     * @param command datos del cliente
     * @param documentNumber numero de documento normalizado
     * @param email correo electronico normalizado
     * @return cliente creado con su contrasena temporal
     */
    private Single<CustomerResult> persistNewCustomer(CustomerCommand command, String documentNumber, String email) {
        log.debug("Construyendo entidad de cliente nuevo: documento={}, email={}", documentNumber, email);
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder()
                .type(command.type())
                .profile(command.profile())
                .status(CustomerStatus.ACTIVE)
                .documentType(command.documentType())
                .documentNumber(documentNumber)
                .businessName(trimToNull(command.businessName()))
                .firstName(trimToNull(command.firstName()))
                .lastName(trimToNull(command.lastName()))
                .email(email)
                .phone(trimToNull(command.phone()))
                .address(trimToNull(command.address()))
                .birthDate(command.birthDate())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        String temporaryPassword = passwordPort.generate();
        log.debug("Contrasena temporal generada para cliente: documento={}", documentNumber);
        return persistencePort.save(customer)
                .onErrorResumeNext(error -> {
                    if (error instanceof DuplicateKeyException) {
                        log.warn("Clave duplicada al persistir cliente: documento={}", documentNumber);
                        return Single.<Customer>error(new BusinessException(ErrorCodes.CUSTOMER_ALREADY_EXISTS,
                                "El cliente con documento " + documentNumber + " ya esta registrado"));
                    }
                    log.error("Error de persistencia al crear cliente: documento={}: {}", documentNumber, error.getMessage(), error);
                    return Single.error(error);
                })
                .flatMap(saved -> {
                    log.info("Cliente creado exitosamente: id={}, tipo={}, perfil={}, documento={}",
                            saved.getCustomerId(), saved.getType(), saved.getProfile(), saved.getDocumentNumber());
                    CustomerRegisteredEvent event = new CustomerRegisteredEvent(saved.getCustomerId(), saved.getEmail(),
                            saved.getEmail(), temporaryPassword, rolesFor(saved.getType()),
                            saved.getType() != null ? saved.getType().name() : null,
                            saved.getStatus() != null ? saved.getStatus().name() : null);
                    return eventPublisherPort.publishRegistered(event)
                            .doOnComplete(() -> log.debug("Evento de registro publicado para cliente={}", saved.getCustomerId()))
                            .doOnError(err -> log.error("Error al publicar evento de registro para cliente={}: {}",
                                    saved.getCustomerId(), err.getMessage(), err))
                            .andThen(Single.just(new CustomerResult(saved, temporaryPassword)));
                });
    }

    /**
     * Aplica los cambios de una actualizacion sobre el cliente existente.
     *
     * @param existing cliente almacenado
     * @param command datos nuevos del cliente
     * @param documentNumber numero de documento normalizado
     * @return cliente actualizado
     */
    private Single<Customer> applyUpdate(Customer existing, CustomerCommand command, String documentNumber) {
        DocumentType previousDocumentType = existing.getDocumentType();
        String previousDocumentNumber = existing.getDocumentNumber();
        log.debug("Aplicando actualizacion a cliente={}: documento anterior={}, nuevo={}",
                existing.getCustomerId(), previousDocumentNumber, documentNumber);
        LocalDateTime now = LocalDateTime.now();
        existing.setType(command.type());
        existing.setProfile(command.profile());
        existing.setDocumentType(command.documentType());
        existing.setDocumentNumber(documentNumber);
        existing.setBusinessName(trimToNull(command.businessName()));
        existing.setFirstName(trimToNull(command.firstName()));
        existing.setLastName(trimToNull(command.lastName()));
        existing.setEmail(normalizeEmail(command.email()));
        existing.setPhone(trimToNull(command.phone()));
        existing.setAddress(trimToNull(command.address()));
        existing.setBirthDate(command.birthDate());
        if (command.status() != null) {
            existing.setStatus(command.status());
        }
        if (command.active() != null) {
            existing.setActive(command.active());
        }
        existing.setUpdatedAt(now);
        return persistencePort.save(existing)
                .onErrorResumeNext(error -> {
                    if (error instanceof DuplicateKeyException) {
                        log.warn("Clave duplicada al actualizar cliente: documento={}", documentNumber);
                        return Single.<Customer>error(new BusinessException(ErrorCodes.CUSTOMER_ALREADY_EXISTS,
                                "El cliente con documento " + documentNumber + " ya esta registrado"));
                    }
                    log.error("Error de persistencia al actualizar cliente id={}: {}", existing.getCustomerId(), error.getMessage(), error);
                    return Single.error(error);
                })
                .flatMap(saved -> {
                    log.info("Cliente actualizado exitosamente: id={}, tipo={}, perfil={}, documento={}",
                            saved.getCustomerId(), saved.getType(), saved.getProfile(), saved.getDocumentNumber());
                    CustomerUpdatedEvent event = new CustomerUpdatedEvent(saved.getCustomerId(), saved.getType().name(),
                            saved.getProfile().name(), saved.getStatus().name(), now);
                    Completable eviction = evictDocumentKeys(previousDocumentType,
                            previousDocumentNumber, saved.getDocumentType(), saved.getDocumentNumber());
                    return eventPublisherPort.publishUpdated(event)
                            .doOnComplete(() -> log.debug("Evento de actualizacion publicado para cliente={}", saved.getCustomerId()))
                            .doOnError(err -> log.error("Error al publicar evento de actualizacion para cliente={}: {}",
                                    saved.getCustomerId(), err.getMessage(), err))
                            .andThen(eviction)
                            .andThen(Single.just(saved));
                });
    }

    /**
     * Invalida en Redis las claves de documento del cliente antes y despues del cambio.
     *
     * @param previousDocumentType tipo de documento anterior
     * @param previousDocumentNumber numero de documento anterior
     * @param currentDocumentType tipo de documento actual
     * @param currentDocumentNumber numero de documento actual
     * @return completable que se completa cuando las claves fueron invalidadas
     */
    private Completable evictDocumentKeys(DocumentType previousDocumentType, String previousDocumentNumber,
                                          DocumentType currentDocumentType, String currentDocumentNumber) {
        Completable previous = previousDocumentType == null
                ? Completable.complete()
                : cachePort.evictCustomerByDocument(previousDocumentType, previousDocumentNumber);
        Completable current = currentDocumentType == null
                ? Completable.complete()
                : cachePort.evictCustomerByDocument(currentDocumentType, currentDocumentNumber);
        return previous.andThen(current);
    }

    /**
     * Valida los datos de un comando de creacion o actualizacion.
     *
     * @param command datos del cliente
     */
    private void validateCommand(CustomerCommand command) {
        if (command == null) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA, "Los datos del cliente son obligatorios");
        }
        validateDocumentType(command.documentType());
        validateProfile(command.type(), command.profile());
        validateTypeSpecificFields(command);
        if (isBlank(command.documentNumber())) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA, "El numero de documento es obligatorio");
        }
        if (isBlank(command.email())) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA, "El correo electronico es obligatorio");
        }
    }

    /**
     * Valida que el tipo de documento sea uno de los soportados por el banco.
     *
     * @param documentType tipo de documento
     */
    private void validateDocumentType(DocumentType documentType) {
        if (documentType == null) {
            throw new BusinessException(ErrorCodes.INVALID_DOCUMENT_TYPE, "El tipo de documento es obligatorio");
        }
        if (!Arrays.asList(DocumentType.values()).contains(documentType)) {
            throw new BusinessException(ErrorCodes.INVALID_DOCUMENT_TYPE,
                    "El tipo de documento " + documentType + " no es valido");
        }
    }

    /**
     * Valida que el perfil sea compatible con el tipo de cliente.
     *
     * @param type tipo de cliente
     * @param profile perfil del cliente
     */
    private void validateProfile(CustomerType type, CustomerProfile profile) {
        if (type == null || profile == null) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_PROFILE,
                    "El tipo y el perfil del cliente son obligatorios");
        }
        boolean valid = switch (type) {
            case PERSONAL -> profile == CustomerProfile.STANDARD || profile == CustomerProfile.VIP;
            case ENTERPRISE -> profile == CustomerProfile.STANDARD || profile == CustomerProfile.PYME;
            default -> false;
        };
        if (!valid) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_PROFILE,
                    "El perfil " + profile + " no es valido para un cliente de tipo " + type);
        }
    }

    /**
     * Valida los campos que dependen del tipo de cliente.
     *
     * @param command datos del cliente
     */
    private void validateTypeSpecificFields(CustomerCommand command) {
        if (command.type() == CustomerType.ENTERPRISE) {
            if (isBlank(command.businessName())) {
                throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA,
                        "El nombre comercial es obligatorio para clientes empresariales");
            }
            if (!isBlank(command.firstName()) || !isBlank(command.lastName()) || command.birthDate() != null) {
                throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA,
                        "Los clientes empresariales no admiten nombres, apellidos ni fecha de nacimiento");
            }
            return;
        }
        if (isBlank(command.firstName()) || isBlank(command.lastName())) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA,
                    "El nombre y el apellido son obligatorios para clientes personales");
        }
        if (!isBlank(command.businessName())) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_DATA,
                    "El nombre comercial solo aplica a clientes empresariales");
        }
    }

    /**
     * Impide asignar el estado {@code DELETED} mediante una actualizacion.
     *
     * @param command datos del cliente
     */
    private void validateStatusChange(CustomerCommand command) {
        if (command.status() == CustomerStatus.DELETED) {
            throw new BusinessException(ErrorCodes.INVALID_CUSTOMER_STATUS,
                    "El cliente se elimina mediante la operacion DELETE");
        }
    }

    /**
     * Calcula los roles del cliente segun su tipo.
     *
     * @param type tipo de cliente
     * @return roles asignados al cliente
     */
    private List<String> rolesFor(CustomerType type) {
        if (type == CustomerType.ENTERPRISE) {
            return List.of(ROLE_CUSTOMER, ROLE_ENTERPRISE);
        }
        return List.of(ROLE_CUSTOMER);
    }

    /**
     * Indica si un texto es nulo o contiene solo espacios.
     *
     * @param value texto a evaluar
     * @return {@code true} si el texto es nulo o esta en blanco
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Normaliza un texto opcional, devolviendo {@code null} si esta en blanco.
     *
     * @param value texto a normalizar
     * @return texto recortado o {@code null}
     */
    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    /**
     * Normaliza el numero de documento para las busquedas y el indice unico.
     *
     * @param documentNumber numero de documento
     * @return numero de documento recortado y en mayusculas
     */
    private String normalizeDocumentNumber(String documentNumber) {
        return documentNumber.trim().toUpperCase(Locale.ROOT);
    }

    /**
     * Normaliza el correo electronico y deriva de el el usuario de acceso.
     *
     * @param email correo electronico
     * @return correo electronico recortado y en minusculas
     */
    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
