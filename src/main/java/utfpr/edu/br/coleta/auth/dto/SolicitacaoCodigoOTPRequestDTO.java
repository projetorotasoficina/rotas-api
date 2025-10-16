package utfpr.edu.br.coleta.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitacaoCodigoOTPRequestDTO {
  @Email(message = "E-mail deve ser válido")
  @NotEmpty
  private String email;
}
