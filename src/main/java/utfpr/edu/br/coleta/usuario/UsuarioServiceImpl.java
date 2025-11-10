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
import utfpr.edu.br.coleta.usuario.dto.MoradorLogadoDTO;

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

  /**
   * Cadastra um novo morador no sistema com role ROLE_MORADOR.
   *
   * @param cadastroDTO dados do morador a ser cadastrado
   * @return usuário cadastrado
   * @throws IllegalArgumentException se já existir usuário com o mesmo email ou CPF
   */
  @Transactional
  public Usuario cadastrarMorador(utfpr.edu.br.coleta.usuario.dto.MoradorCadastroDTO cadastroDTO) {
    // Verifica se já existe usuário com o mesmo email
    if (usuarioRepository.findByEmail(cadastroDTO.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
    }

    // Verifica se já existe usuário com o mesmo CPF
    if (usuarioRepository.findByCpf(cadastroDTO.getCpf()).isPresent()) {
      throw new IllegalArgumentException("Já existe um usuário cadastrado com este CPF.");
    }

    // Cria novo usuário com role MORADOR
    Usuario usuario = new Usuario();
    usuario.setNome(cadastroDTO.getNome());
    usuario.setEmail(cadastroDTO.getEmail());
    usuario.setCpf(cadastroDTO.getCpf());
    usuario.setTelefone(cadastroDTO.getTelefone());
    usuario.setEndereco(cadastroDTO.getEndereco());
    usuario.setNumero(cadastroDTO.getNumero());
    usuario.setBairro(cadastroDTO.getBairro());
    usuario.setCep(cadastroDTO.getCep());
    usuario.setLatitude(cadastroDTO.getLatitude());
    usuario.setLongitude(cadastroDTO.getLongitude());
    usuario.setAtivo(true);

    // Define role MORADOR
    java.util.Set<utfpr.edu.br.coleta.usuario.role.Role> roles = new java.util.HashSet<>();
    roles.add(utfpr.edu.br.coleta.usuario.role.Role.ROLE_MORADOR);
    usuario.setRoles(roles);

    // Salva no banco
    return usuarioRepository.save(usuario);
  }

  @Override
  public MoradorLogadoDTO obterMoradorLogadoCompleto() {
    Usuario u = obterUsuarioLogado();

    return MoradorLogadoDTO.builder()
            .id(u.getId())
            .nome(u.getNome())
            .cpf(u.getCpf())
            .email(u.getEmail())
            .telefone(u.getTelefone())
            .ativo(u.getAtivo())
            .endereco(u.getEndereco())
            .numero(u.getNumero())
            .bairro(u.getBairro())
            .cep(u.getCep())
            .latitude(u.getLatitude() == null ? null : u.getLatitude().doubleValue())
            .longitude(u.getLongitude() == null ? null : u.getLongitude().doubleValue())
            .build();
  }
}
