package utfpr.edu.br.coleta.usuario.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoradorLogadoDTO {
    private Long id;
    private String nome;
    private String cpf;

    private String email;
    private String telefone;
    private Boolean ativo;

    private String endereco;
    private String numero;
    private String bairro;
    private String cep;
    private Double latitude;
    private Double longitude;
}