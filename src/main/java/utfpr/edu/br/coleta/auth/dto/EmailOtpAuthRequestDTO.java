package utfpr.edu.br.coleta.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailOtpAuthRequestDTO {
  @NotBlank @Email private String email;

  @NotBlank private String code;
}
