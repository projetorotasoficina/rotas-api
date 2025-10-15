package utfpr.edu.br.coleta.aplicativoandroid.codigoativacao;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import utfpr.edu.br.coleta.generics.BaseEntity;

import java.time.LocalDateTime;

/**
 * Entidade que representa um código de ativação para aplicativos Android.
 * 
 * Um código de ativação é gerado pelo administrador e usado uma única vez
 * pelo aplicativo Android para obter um token permanente de acesso.
 * 
 * @author Luiz Alberto dos Passos
 */
@Entity
@Table(name = "tb_codigo_ativacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CodigoAtivacao extends BaseEntity {

    /**
     * Código único de ativação (24 caracteres alfanuméricos).
     */
    @Column(nullable = false, unique = true, length = 24)
    private String codigo;

    /**
     * Indica se o código já foi usado.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean usado = false;

    /**
     * Data e hora em que o código foi gerado.
     */
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime dataGeracao = LocalDateTime.now();

    /**
     * Data e hora em que o código foi usado (null se ainda não foi usado).
     */
    @Column
    private LocalDateTime dataUso;

    /**
     * ID do dispositivo que usou este código.
     */
    @Column(length = 255)
    private String deviceId;

    /**
     * Indica se o código está ativo (pode ser desativado pelo admin).
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;
}

