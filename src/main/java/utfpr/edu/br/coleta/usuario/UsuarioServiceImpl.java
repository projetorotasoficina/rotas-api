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
import utfpr.edu.br.coleta.usuario.dto.MoradorUpdateDTO;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioServiceImpl extends CrudServiceImpl<Usuario, Long>
        implements IUsuarioService, UserDetailsService {

  private final UsuarioRepository usuarioRepository;

  public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  protected JpaRepository<Usuario, Long> getRepository() {
    return usuarioRepository;
  }

  private static final String ROLE_SERVIDOR = "ROLE_SERVIDOR";

  /** Usuário autenticado */
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

  /** Spring Security: carrega por e-mail */
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return usuarioRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
  }

  /** Ativa usuário após OTP */
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

  /** Cadastro público de morador */
  @Transactional
  public Usuario cadastrarMorador(utfpr.edu.br.coleta.usuario.dto.MoradorCadastroDTO cadastroDTO) {
    // e-mail único
    if (usuarioRepository.findByEmail(cadastroDTO.getEmail()).isPresent()) {
      throw new IllegalArgumentException("Já existe um usuário cadastrado com este e-mail.");
    }
    // cpf único (se informado)
    if (cadastroDTO.getCpf() != null && !cadastroDTO.getCpf().isBlank()) {
      if (usuarioRepository.findByCpf(cadastroDTO.getCpf()).isPresent()) {
        throw new IllegalArgumentException("Já existe um usuário cadastrado com este CPF.");
      }
    }

    Usuario usuario = new Usuario();
    usuario.setNome(cadastroDTO.getNome());
    usuario.setEmail(cadastroDTO.getEmail());
    usuario.setCpf(cadastroDTO.getCpf());
    usuario.setTelefone(cadastroDTO.getTelefone());
    usuario.setEndereco(cadastroDTO.getEndereco());
    usuario.setNumero(cadastroDTO.getNumero());
    usuario.setBairro(cadastroDTO.getBairro());
    usuario.setCep(normalizaCEP(cadastroDTO.getCep()));
    usuario.setLatitude(cadastroDTO.getLatitude());
    usuario.setLongitude(cadastroDTO.getLongitude());
    usuario.setAtivo(true);

    Set<utfpr.edu.br.coleta.usuario.role.Role> roles = new HashSet<>();
    roles.add(utfpr.edu.br.coleta.usuario.role.Role.ROLE_MORADOR);
    usuario.setRoles(roles);

    return usuarioRepository.save(usuario);
  }

  /** DTO completo do morador logado (leitura) */
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

  /** Atualiza dados do morador logado */
  @Override
  @Transactional
  public MoradorLogadoDTO atualizarMoradorLogado(MoradorUpdateDTO dto) {
    Usuario u = obterUsuarioLogado();

    // Nome
    if (dto.getNome() != null && !dto.getNome().isBlank()) {
      u.setNome(dto.getNome().trim());
    }
    // Telefone
    if (dto.getTelefone() != null) {
      u.setTelefone(dto.getTelefone().trim());
    }
    // CPF (se mudou, valida unicidade)
    if (dto.getCpf() != null && !dto.getCpf().isBlank()) {
      String novoCpf = apenasDigitos(dto.getCpf());
      if (!novoCpf.equals(u.getCpf())) {
        Optional<Usuario> outro = usuarioRepository.findByCpf(novoCpf);
        if (outro.isPresent() && !outro.get().getId().equals(u.getId())) {
          throw new IllegalArgumentException("Já existe um usuário com este CPF.");
        }
        u.setCpf(novoCpf);
      }
    }
    // E-mail (não alterar por padrão; se quiser permitir, descomente a validação abaixo)
    /*
    if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
      String novoEmail = dto.getEmail().trim().toLowerCase();
      if (!novoEmail.equalsIgnoreCase(u.getEmail())) {
        Optional<Usuario> outroEmail = usuarioRepository.findByEmail(novoEmail);
        if (outroEmail.isPresent() && !outroEmail.get().getId().equals(u.getId())) {
          throw new IllegalArgumentException("Já existe um usuário com este e-mail.");
        }
        u.setEmail(novoEmail);
      }
    }
    */

    // Endereço
    if (dto.getEndereco() != null) u.setEndereco(vazioParaNull(dto.getEndereco()));
    if (dto.getNumero() != null)   u.setNumero(vazioParaNull(dto.getNumero()));
    if (dto.getBairro() != null)   u.setBairro(vazioParaNull(dto.getBairro()));
    if (dto.getCep() != null)      u.setCep(normalizaCEP(dto.getCep()));

    // Coordenadas
    if (dto.getLatitude() != null)  u.setLatitude(dto.getLatitude());
    if (dto.getLongitude() != null) u.setLongitude(dto.getLongitude());

    usuarioRepository.save(u);
    return obterMoradorLogadoCompleto();
  }

  /** Exclui (hard delete) a conta do usuário logado */
  @Override
  @Transactional
  public void excluirContaLogado() {
    Usuario u = obterUsuarioLogado();
    usuarioRepository.deleteById(u.getId());
  }

  /* ===================== utilitários ===================== */

  private static String normalizaCEP(String cep) {
    if (cep == null) return null;
    String dig = apenasDigitos(cep);
    return dig.length() == 8 ? dig : dig; // mantém só dígitos; regra extra se quiser.
  }

  private static String apenasDigitos(String s) {
    return s == null ? null : s.replaceAll("\\D", "");
  }

  private static String vazioParaNull(String s) {
    return (s == null || s.isBlank()) ? null : s.trim();
  }
}