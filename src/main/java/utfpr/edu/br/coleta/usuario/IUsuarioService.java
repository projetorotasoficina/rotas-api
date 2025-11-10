package utfpr.edu.br.coleta.usuario;


import org.springframework.security.core.userdetails.UserDetailsService;
import utfpr.edu.br.coleta.generics.ICrudService;
import utfpr.edu.br.coleta.usuario.dto.MoradorLogadoDTO;

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

  /**
   * Cadastra um novo morador no sistema.
   *
   * @param cadastroDTO dados do morador a ser cadastrado
   * @return usuário cadastrado com role ROLE_MORADOR
   */
  Usuario cadastrarMorador(utfpr.edu.br.coleta.usuario.dto.MoradorCadastroDTO cadastroDTO);
  MoradorLogadoDTO obterMoradorLogadoCompleto(); // novo

}
