package utfpr.edu.br.coleta.generics;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Implementação genérica de ICrudService utilizando JpaRepository.
 * Fornece operações básicas de CRUD com possibilidade de personalização
 * nos métodos preSave e postSave.
 *
 * @param <T> tipo da entidade
 * @param <I> tipo do identificador da entidade
 *
 * Autor: Luiz Alberto dos Passos
 */
public abstract class CrudServiceImpl<T, I extends Serializable> implements ICrudService<T, I> {

  /**
   * Fornece o repositório JPA responsável pelas operações da entidade.
   *
   * @return repositório JPA da entidade
   */
  protected abstract JpaRepository<T, I> getRepository();

  /**
   * Retorna todas as entidades do repositório.
   *
   * @return lista com todas as entidades
   */
  @Override
  public List<T> findAll() {
    return getRepository().findAll();
  }

  /**
   * Retorna todas as entidades aplicando critério de ordenação.
   *
   * @param sort critério de ordenação
   * @return lista de entidades ordenadas
   */
  @Override
  public List<T> findAll(Sort sort) {
    return getRepository().findAll(sort);
  }

  /**
   * Retorna uma página de entidades de acordo com os critérios de paginação e ordenação.
   *
   * @param pageable objeto com paginação e ordenação
   * @return página de entidades
   */
  @Override
  public Page<T> findAll(Pageable pageable) {
    return getRepository().findAll(pageable);
  }

  /**
   * Salva uma entidade, aplicando validações antes e lógica adicional após.
   * Executa preSave antes de salvar e postSave após salvar.
   *
   * @param entity entidade a ser salva
   * @return entidade salva
   * @throws IllegalArgumentException se a entidade for nula
   */
  @Override
  @Transactional
  public T save(T entity) {
    if (entity == null) {
      throw new IllegalArgumentException("O conteúdo a ser salvo não pode ser vazio.");
    }
    entity = preSave(entity);
    T savedEntity = getRepository().save(entity);
    savedEntity = postSave(savedEntity);
    return savedEntity;
  }

  /**
   * Permite customizar ou validar a entidade antes de salvar.
   * Pode ser sobrescrito em implementações específicas.
   *
   * @param entity entidade a ser processada
   * @return entidade pronta para ser salva
   */
  public T preSave(T entity) {
    return entity;
  }

  /**
   * Executa lógica adicional após o salvamento.
   * Pode ser sobrescrito em implementações específicas.
   *
   * @param entity entidade recém-salva
   * @return entidade possivelmente modificada
   */
  public T postSave(T entity) {
    return entity;
  }

  /**
   * Salva a entidade e força sincronização imediata com o banco.
   *
   * @param entity entidade a ser salva
   * @return entidade salva
   */
  @Override
  @Transactional
  public T saveAndFlush(T entity) {
    return getRepository().saveAndFlush(entity);
  }

  /**
   * Salva em lote todas as entidades fornecidas.
   *
   * @param iterable coleção de entidades
   * @return entidades salvas
   */
  @Override
  @Transactional
  public Iterable<T> save(Iterable<T> iterable) {
    return getRepository().saveAll(iterable);
  }

  /**
   * Força sincronização imediata com o banco.
   */
  @Override
  public void flush() {
    getRepository().flush();
  }

  /**
   * Busca uma entidade pelo identificador.
   *
   * @param i identificador
   * @return entidade encontrada
   * @throws EntityNotFoundException se não existir entidade com o id informado
   */
  @Override
  public T findOne(I i) {
    return getRepository()
            .findById(i)
            .orElseThrow(() -> new EntityNotFoundException("Não há entidade com o id " + i));
  }

  /**
   * Verifica se existe entidade com o identificador informado.
   *
   * @param i identificador
   * @return true se existir, false caso contrário
   */
  @Override
  public boolean exists(I i) {
    return getRepository().existsById(i);
  }

  /**
   * Retorna a quantidade total de registros da entidade.
   *
   * @return número total de entidades
   */
  @Override
  @Transactional(readOnly = true)
  public long count() {
    return getRepository().count();
  }

  /**
   * Remove a entidade pelo identificador.
   *
   * @param i identificador da entidade
   */
  @Override
  @Transactional
  public void delete(I i) {
    getRepository().deleteById(i);
  }

  /**
   * Remove todas as entidades fornecidas.
   *
   * @param iterable coleção de entidades
   */
  @Override
  @Transactional
  public void delete(Iterable<? extends T> iterable) {
    getRepository().deleteAll(iterable);
  }

  /**
   * Remove permanentemente todas as entidades do repositório.
   */
  @Override
  @Transactional
  public void deleteAll() {
    getRepository().deleteAll();
  }
}