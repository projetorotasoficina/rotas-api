package utfpr.edu.br.coleta.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@Schema(name = "RespostaLoginDTO", description = "Resposta de login")
public class RespostaLoginDTO {
  private String token;
  private long expiresIn;

  @Schema(name = "user", description = "Informações do usuário autenticado")
  private UsuarioLoginDTO user;
}
