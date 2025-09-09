package utfpr.edu.br.coleta.usuario;


import org.springframework.security.core.userdetails.UserDetailsService;
import utfpr.edu.br.coleta.generics.ICrudService;

public interface IUsuarioService extends ICrudService<Usuario, Long>, UserDetailsService {
  /**
   * Retorna o usuário atualmente autenticado no sistema.
   *
   * @return o usuário autenticado ou {@code null} se não houver usuário autenticado
   */
  Usuario obterUsuarioLogado();

  /**
   * Ativa um usuário identificado pelo e-mail.
   *
   * @param email e-mail do usuário a ser ativado
   */
  void ativarUsuario(String email);
}
