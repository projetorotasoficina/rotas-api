package utfpr.edu.br.coleta.auth.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SolicitacaoCodigoOTPResponseDTO {
  private String mensagem;
}
