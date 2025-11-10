package utfpr.edu.br.coleta.usuario.dto;

import jakarta.validation.constraints.Pattern;
import lombok.*;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoradorUpdateDTO {
  private String nome;
  private String telefone;
  private String cpf;

  private String endereco;
  private String numero;
  private String bairro;
  private String cep;

  private Double latitude;
  private Double longitude;

  // Email só se quiser permitir atualizar; senão remova
  private String email;
}