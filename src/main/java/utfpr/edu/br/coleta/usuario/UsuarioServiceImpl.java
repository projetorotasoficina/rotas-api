package utfpr.edu.br.coleta.usuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import utfpr.edu.br.coleta.generics.CrudServiceImpl;

@Service
public class UsuarioServiceImpl extends CrudServiceImpl<Usuario, Long>
    implements IUsuarioService, UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  /**
   * Cria uma instância do serviço de usuário utilizando o repositório fornecido.
   *
   * @param usuarioRepository repositório de usuários utilizado para operações de persistência
   */
  public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {

    this.usuarioRepository = usuarioRepository;
  }

  /**
   * Fornece o repositório JPA utilizado para operações CRUD da entidade Usuario.
   *
   * @return o JpaRepository específico para Usuario
   */
  @Override
  protected JpaRepository<Usuario, Long> getRepository() {
    return usuarioRepository;
  }

  private static final String ROLE_SERVIDOR = "ROLE_SERVIDOR";

  /**
   * Obtém o usuário atualmente autenticado no contexto de segurança.
   *
   * @return o usuário autenticado
   * @throws IllegalStateException se não houver autenticação ativa ou se o principal não for uma
   *     instância de Usuario
   */
  public Usuario obterUsuarioLogado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) {
      throw new IllegalStateException("Nenhum usuário autenticado!");
    }
    Object principal = auth.getPrincipal();
    if (!(principal instanceof Usuario)) {
      throw new IllegalStateException("Principal não é uma instância de Usuario!");
    }
    return (Usuario) principal;
  }



  /**
   * Carrega os detalhes do usuário baseado no email fornecido.
   *
   * @param email o email do usuário
   * @return UserDetails do usuário encontrado
   * @throws UsernameNotFoundException se o usuário não for encontrado
   */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return usuarioRepository
        .findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
  }

  /**
   * Ativa um usuário após validação bem-sucedida do código OTP.
   *
   * @param email o email do usuário a ser ativado
   */
  @Transactional
  public void ativarUsuario(String email) {
    Usuario usuario =
        usuarioRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));

    usuario.setAtivo(true);
    usuarioRepository.save(usuario);
  }

  @Override
  public Page<Usuario> findAll(Pageable pageable, String search) {
    if (search == null || search.trim().isEmpty()) {
      return findAll(pageable);
    }
    return usuarioRepository.findByNomeContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable);
  }
}
