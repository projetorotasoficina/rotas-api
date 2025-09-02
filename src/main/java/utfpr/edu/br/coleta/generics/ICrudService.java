package utfpr.edu.br.coleta.generics;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serializable;
import java.util.List;

/**
 * Interface genérica para operações CRUD básicas.
 *
 * @param <T> tipo da entidade
 * @param <I> tipo do identificador da entidade
 *
 * Autor: Luiz Alberto dos Passos
 */
public interface ICrudService<T, I extends Serializable> {

  /**
   * Retorna todas as entidades persistidas.
   *
   * @return lista com todas as entidades
   */
  List<T> findAll();

  /**
   * Retorna todas as entidades aplicando ordenação.
   *
   * @param sort critério de ordenação
   * @return lista de entidades ordenadas
   */
  List<T> findAll(Sort sort);

  /**
   * Retorna uma página de entidades conforme os critérios de paginação e ordenação.
   *
   * @param pageable informações de paginação e ordenação
   * @return página de entidades
   */
  Page<T> findAll(Pageable pageable);

  /**
   * Salva a entidade e retorna a instância persistida.
   *
   * @param entity entidade a ser salva
   * @return entidade persistida
   */
  T save(T entity);

  /**
   * Salva a entidade e sincroniza imediatamente com o banco de dados.
   *
   * @param entity entidade a ser salva
   * @return entidade persistida
   */
  T saveAndFlush(T entity);

  /**
   * Salva uma coleção de entidades.
   *
   * @param iterable coleção de entidades
   * @return entidades persistidas
   */
  Iterable<T> save(Iterable<T> iterable);

  /**
   * Força a sincronização imediata das alterações pendentes com o banco de dados.
   */
  void flush();

  /**
   * Busca uma entidade pelo identificador.
   *
   * @param i identificador da entidade
   * @return entidade correspondente
   */
  T findOne(I i);

  /**
   * Verifica se existe uma entidade com o identificador informado.
   *
   * @param i identificador da entidade
   * @return true se existir, false caso contrário
   */
  boolean exists(I i);

  /**
   * Retorna a quantidade total de registros.
   *
   * @return número de entidades persistidas
   */
  long count();

  /**
   * Remove a entidade pelo identificador.
   *
   * @param i identificador da entidade
   */
  void delete(I i);

  /**
   * Remove todas as entidades fornecidas.
   *
   * @param iterable coleção de entidades
   */
  void delete(Iterable<? extends T> iterable);

  /**
   * Remove todas as entidades persistidas.
   */
  void deleteAll();
}