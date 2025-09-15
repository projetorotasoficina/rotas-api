package utfpr.edu.br.coleta.rota;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utfpr.edu.br.coleta.generics.BaseEntity;

/**
 * Entidade que representa uma Rota no sistema de coleta.
 *
 * Contém informações pessoais e operacionais necessárias
 * para vinculação em trajetos.
 *
 * Autor: Pedro Henrique Sauthier
 */
@Entity
@Table(name = "tb_rota")
@Getter
@Setter
@NoArgsConstructor
public class Rota extends BaseEntity {

}