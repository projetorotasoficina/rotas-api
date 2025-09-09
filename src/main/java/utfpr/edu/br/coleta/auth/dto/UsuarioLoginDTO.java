package utfpr.edu.br.coleta.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@Schema(
    name = "UsuarioLoginDTO",
    description = "Objeto de informação do usuário para ser carregado no contexto do frontend")
public class UsuarioLoginDTO {
  private String email;
  private String nome;
  private Set<String> authorities;
}
