package utfpr.edu.br.coleta.aplicativoandroid.apptoken;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import utfpr.edu.br.coleta.generics.BaseEntity;

import java.time.LocalDateTime;

/**
 * Entidade que representa um token permanente de acesso para aplicativos Android.
 * 
 * Após ativar com um código, o app Android recebe um token permanente
 * que é usado em todas as requisições subsequentes.
 * 
 * @author Luiz Alberto dos Passos
 */
@Entity
@Table(name = "tb_app_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AppToken extends BaseEntity {

    /**
     * Token único de acesso (UUID).
     */
    @Column(nullable = false, unique = true, length = 255)
    private String token;

    /**
     * ID único do dispositivo Android.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String deviceId;

    /**
     * Indica se o token está ativo.
     */
    @Builder.Default
    @Column(nullable = false)
    private Boolean ativo = true;

    /**
     * Data e hora de criação do token.
     */
    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    /**
     * Data e hora do último acesso usando este token.
     */
    @Column
    private LocalDateTime ultimoAcesso;

    /**
     * Contador de acessos realizados com este token.
     */
    @Builder.Default
    @Column(nullable = false)
    private Long totalAcessos = 0L;
}

