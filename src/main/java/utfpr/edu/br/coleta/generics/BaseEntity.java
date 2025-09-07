package utfpr.edu.br.coleta.generics;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * Classe base abstrata para todas as entidades persistentes do sistema.
 * <p>
 * Fornece uma propriedade {@code id} gerada automaticamente,
 * que serve como chave primária para as entidades filhas.
 * <br>
 * Essa classe utiliza o padrão de herança com {@link MappedSuperclass},
 * permitindo que atributos e mapeamentos sejam herdados,
 * mas sem criar uma tabela própria no banco de dados.
 * </p>
 *
 * <p><b>Autor:</b> Luiz Alberto dos Passos</p>
 */
@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@SuperBuilder
public abstract class BaseEntity implements Serializable {

  /**
   * Identificador único da entidade.
   * <p>
   * É a chave primária da tabela correspondente,
   * gerada automaticamente pelo banco de dados
   * usando a estratégia {@link GenerationType#IDENTITY}.
   * </p>
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;
}