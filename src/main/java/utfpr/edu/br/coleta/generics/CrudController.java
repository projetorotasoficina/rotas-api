package utfpr.edu.br.coleta.generics;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador genérico que provê operações CRUD (Create, Read, Update, Delete)
 * para entidades que estendem BaseEntity, utilizando DTOs para entrada e saída.
 *
 * @param <T> tipo da entidade persistente (deve estender BaseEntity)
 * @param <D> tipo do DTO utilizado
 *
 * @author Luiz Alberto dos Passos
 */
public abstract class CrudController<T extends BaseEntity, D> {

  /** Serviço CRUD a ser implementado pelo controller específico */
  protected abstract ICrudService<T, Long> getService();

  protected abstract ModelMapper getModelMapper();

  private final Class<T> typeClass;
  private final Class<D> typeDtoClass;

  protected CrudController(Class<T> typeClass, Class<D> typeDtoClass) {
    this.typeClass = typeClass;
    this.typeDtoClass = typeDtoClass;
  }

  protected D convertToDto(T entity) {
    return getModelMapper().map(entity, this.typeDtoClass);
  }

  protected T convertToEntity(D entityDto) {
    return getModelMapper().map(entityDto, this.typeClass);
  }

  @GetMapping
  @Operation(summary = "Retorna uma lista de todos os registros")
  public ResponseEntity<List<D>> findAll() {
    return ResponseEntity.ok(getService().findAll().stream().map(this::convertToDto).toList());
  }

  @GetMapping("page")
  @Operation(summary = "Retorna uma página de registros")
  public ResponseEntity<Page<D>> findAll(
          @RequestParam int page,
          @RequestParam int size,
          @RequestParam(required = false) String order,
          @RequestParam(required = false) Boolean asc,
          @RequestParam(required = false) String search) {

    PageRequest pageRequest = PageRequest.of(page, size);
    if (order != null && asc != null) {
      pageRequest = PageRequest.of(page, size, asc ? Sort.Direction.ASC : Sort.Direction.DESC, order);
    }

    return ResponseEntity.ok(getService().findAll(pageRequest, search).map(this::convertToDto));
  }

  @GetMapping("{id}")
  @Operation(summary = "Busca por id")
  public ResponseEntity<D> findOne(@PathVariable Long id) {
    // MODIFICAÇÃO 1: Remoção do try-catch local.
    // A exceção EntityNotFoundException será tratada pelo GlobalExceptionHandler.
    T entity = getService().findOne(id);
    return ResponseEntity.ok(convertToDto(entity));
  }

  @PostMapping
  @Operation(summary = "Cria um novo registro")
  public ResponseEntity<D> create(@RequestBody @Valid D entity) {
    T savedEntity = getService().save(convertToEntity(entity));
    return ResponseEntity.status(HttpStatus.CREATED).body(convertToDto(savedEntity));
  }

  @PutMapping("{id}")
  @Operation(summary = "Atualiza um registro existente")
  public ResponseEntity<D> update(@PathVariable Long id, @RequestBody @Valid D entity) {
    T entityToUpdate = convertToEntity(entity);

    // Substituir o retorno ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
    // por uma exceção IllegalArgumentException, que será tratada pelo GlobalExceptionHandler
    // e retornará um JSON de erro padronizado (400 BAD_REQUEST).
    if (!id.equals(entityToUpdate.getId())) {
      throw new IllegalArgumentException("O ID do recurso na URL (" + id + ") não corresponde ao ID da entidade no corpo da requisição (" + entityToUpdate.getId() + ").");
    }

    T updatedEntity = getService().save(entityToUpdate);
    return ResponseEntity.ok(convertToDto(updatedEntity));
  }

  @GetMapping("exists/{id}")
  @Operation(summary = "Verifica se um registro existe")
  public ResponseEntity<Boolean> exists(@PathVariable Long id) {
    return ResponseEntity.ok(getService().exists(id));
  }

  @GetMapping("count")
  @Operation(summary = "Retorna a contagem de registros")
  public ResponseEntity<Long> count() {
    return ResponseEntity.ok(getService().count());
  }

  @DeleteMapping("{id}")
  @Operation(summary = "Deleta um registro")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    getService().delete(id);
    return ResponseEntity.noContent().build();
  }
}