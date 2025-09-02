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

import java.io.Serializable;
import java.util.List;

/**
 * Controlador genérico que provê operações CRUD (Create, Read, Update, Delete)
 * para entidades que estendem BaseEntity, utilizando DTOs para entrada e saída.
 *
 * @param <T> tipo da entidade persistente
 * @param <D> tipo do DTO utilizado
 * @param <I> tipo do identificador da entidade
 *
 * Autor: Luiz Alberto dos Passos
 */
@RestController
public abstract class CrudController<T extends BaseEntity, D, I extends Serializable> {

  /**
   * Retorna o serviço CRUD responsável pelas operações de persistência da entidade.
   *
   * @return implementação de ICrudService correspondente
   */
  protected abstract ICrudService<T, I> getService();

  /**
   * Fornece a instância de ModelMapper usada para conversão entre entidades e DTOs.
   *
   * @return instância de ModelMapper
   */
  protected abstract ModelMapper getModelMapper();

  /** Classe da entidade gerenciada pelo controlador. */
  private final Class<T> typeClass;

  /** Classe do DTO correspondente à entidade. */
  private final Class<D> typeDtoClass;

  /**
   * Construtor que inicializa o controlador CRUD.
   *
   * @param typeClass classe da entidade
   * @param typeDtoClass classe do DTO
   */
  protected CrudController(Class<T> typeClass, Class<D> typeDtoClass) {
    this.typeClass = typeClass;
    this.typeDtoClass = typeDtoClass;
  }

  /**
   * Retorna a classe do tipo DTO.
   *
   * @return classe do DTO
   */
  public Class<D> getTypeDtoClass() {
    return typeDtoClass;
  }

  /**
   * Converte uma entidade em DTO.
   *
   * @param entity entidade a ser convertida
   * @return DTO correspondente
   */
  private D convertToDto(T entity) {
    return getModelMapper().map(entity, this.typeDtoClass);
  }

  /**
   * Converte um DTO em entidade.
   *
   * @param entityDto DTO a ser convertido
   * @return entidade correspondente
   */
  protected T convertToEntity(D entityDto) {
    return getModelMapper().map(entityDto, this.typeClass);
  }

  /**
   * Retorna todos os registros convertidos para DTOs.
   *
   * @return lista de DTOs com status HTTP 200
   */
  @GetMapping
  @Operation(summary = "Retorna uma lista de todos os registros")
  public ResponseEntity<List<D>> findAll() {
    return ResponseEntity.ok(getService().findAll().stream().map(this::convertToDto).toList());
  }

  /**
   * Retorna uma página de registros com paginação e ordenação.
   *
   * @param page número da página
   * @param size quantidade de itens por página
   * @param order campo opcional de ordenação
   * @param asc define se a ordenação é ascendente
   * @return página de DTOs
   */
  @GetMapping("page")
  @Operation(summary = "Retorna um paginável com os registros de acordo com os critérios fornecidos")
  public ResponseEntity<Page<D>> findAll(
          @RequestParam int page,
          @RequestParam int size,
          @RequestParam(required = false) String order,
          @RequestParam(required = false) Boolean asc) {
    PageRequest pageRequest = PageRequest.of(page, size);
    if (order != null && asc != null) {
      pageRequest =
              PageRequest.of(page, size, asc ? Sort.Direction.ASC : Sort.Direction.DESC, order);
    }
    return ResponseEntity.ok(getService().findAll(pageRequest).map(this::convertToDto));
  }

  /**
   * Busca uma entidade pelo identificador.
   *
   * @param i identificador da entidade
   * @return DTO correspondente ou 404 se não encontrada
   */
  @GetMapping("{i}")
  @Operation(summary = "Retorna um registro de acordo com o identificador fornecido")
  public ResponseEntity<D> findOne(@PathVariable I i) {
    try {
      T entity = getService().findOne(i);
      return ResponseEntity.ok(convertToDto(entity));
    } catch (EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Cria uma nova entidade a partir do DTO.
   *
   * @param entity DTO com os dados
   * @return DTO salvo com status HTTP 201
   */
  @PostMapping
  @Operation(summary = "Cria um novo registro com os dados fornecidos")
  public ResponseEntity<D> create(@RequestBody @Valid D entity) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(convertToDto(getService().save(convertToEntity(entity))));
  }

  /**
   * Atualiza uma entidade existente.
   *
   * @param i identificador da entidade
   * @param entity DTO com os dados atualizados
   * @return DTO atualizado ou erro 400 se os IDs divergirem
   */
  @PutMapping("{i}")
  @Operation(summary = "Atualiza um registro de acordo com o identificador fornecido")
  public ResponseEntity<D> update(@PathVariable I i, @RequestBody @Valid D entity) {
    T entityToUpdate = convertToEntity(entity);
    I entityI = (I) entityToUpdate.getId();
    if (!i.equals(entityI)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    return ResponseEntity.status(HttpStatus.OK)
            .body(convertToDto(getService().save(entityToUpdate)));
  }

  /**
   * Verifica se existe uma entidade pelo identificador.
   *
   * @param i identificador da entidade
   * @return true se existir, false caso contrário
   */
  @GetMapping("exists/{i}")
  @Operation(summary = "Verifica se um registro existe de acordo com o identificador fornecido")
  public ResponseEntity<Boolean> exists(@PathVariable I i) {
    return ResponseEntity.ok(getService().exists(i));
  }

  /**
   * Retorna a quantidade total de registros.
   *
   * @return total de registros
   */
  @GetMapping("count")
  @Operation(summary = "Retorna a quantidade total de registros")
  public ResponseEntity<Long> count() {
    return ResponseEntity.ok(getService().count());
  }

  /**
   * Exclui a entidade pelo identificador.
   *
   * @param i identificador da entidade
   * @return resposta sem conteúdo com status 204
   */
  @DeleteMapping("{i}")
  @Operation(summary = "Exclui um registro de acordo com o identificador fornecido")
  public ResponseEntity<Void> delete(@PathVariable I i) {
    getService().delete(i);
    return ResponseEntity.noContent().build();
  }
}