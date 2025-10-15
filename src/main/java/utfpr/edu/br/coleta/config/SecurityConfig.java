package utfpr.edu.br.coleta.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import utfpr.edu.br.coleta.auth.jwt.JwtAuthenticationFilter;
import utfpr.edu.br.coleta.auth.otp.EmailOtpAuthenticationProvider;
import utfpr.edu.br.coleta.aplicativoandroid.apptoken.AppTokenFilter;

import java.util.Arrays;
import java.util.List;

/** Classe de configuração do Spring Security */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private final Environment environment;
  private final EmailOtpAuthenticationProvider emailOtpAuthenticationProvider;

  /** Injeta a chave app.client.origins e os valores existentes separados por vírgula configurado no yml */
  @Value("#{'${app.client.origins}'.split(',')}")
  private List<String> allowedOrigins;

  @Value("${app.swagger.enabled}")
  private boolean isSwaggerEnabled;

  /** Construtor com injeção de dependências */
  public SecurityConfig(
          Environment environment,
          EmailOtpAuthenticationProvider emailOtpAuthenticationProvider) {
    this.environment = environment;
    this.emailOtpAuthenticationProvider = emailOtpAuthenticationProvider;
  }

  /** Configura a cadeia de filtros de segurança HTTP da aplicação */
  @Bean
  public SecurityFilterChain securityFilterChain(
          HttpSecurity http,
          JwtAuthenticationFilter jwtAuthenticationFilter,
          AppTokenFilter appTokenFilter) throws Exception {

    http.cors(c -> c.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**", "/h2-console/**"))
            .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
            .authorizeHttpRequests(authorize ->
                    authorize
                            // ✅ Health Checks (Fly.io / Actuator)
                            .requestMatchers("/actuator/health", "/actuator/info").permitAll()

                            // Rotas de autenticação - únicas acessíveis sem login
                            .requestMatchers("/api/auth/**").permitAll()
                            .requestMatchers("/api/email/**").permitAll()

                            // ✅ Endpoint público de ativação Android
                            .requestMatchers("/api/app/activate").permitAll()

                            // Console H2 apenas em ambiente de teste
                            .requestMatchers("/h2-console/**").access(isTestProfile())

                            // Swagger apenas se habilitado
                            .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html/**")
                            .access(isSwaggerEnabled())

                            // CORS preflight
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                            // Página de erro
                            .requestMatchers("/error").permitAll()

                            // ✅ Endpoints de gerenciamento de códigos e tokens - apenas SUPER_ADMIN
                            .requestMatchers("/api/codigosativacao/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers("/api/apptokens/**").hasAuthority("ROLE_SUPER_ADMIN")

                            // Operações de criação (POST) - apenas SUPER_ADMIN
                            .requestMatchers(HttpMethod.POST, "/api/usuarios/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/motoristas/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/caminhoes/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/tipocoleta/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.POST, "/api/tiporesiduo/**").hasAuthority("ROLE_SUPER_ADMIN")

                            // ✅ MODIFICADO: POST de trajetos e incidentes - SUPER_ADMIN ou APP_ANDROID
                            .requestMatchers(HttpMethod.POST, "/api/trajetos/**")
                            .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_APP_ANDROID")
                            .requestMatchers(HttpMethod.POST, "/api/incidentes/**")
                            .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_APP_ANDROID")

                            // Operações de exclusão (DELETE) - apenas SUPER_ADMIN
                            .requestMatchers(HttpMethod.DELETE, "/api/usuarios/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/motoristas/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/caminhoes/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/tipocoleta/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.DELETE, "/api/tiporesiduo/**").hasAuthority("ROLE_SUPER_ADMIN")

                            // Operações de atualização (PUT) - apenas SUPER_ADMIN
                            .requestMatchers(HttpMethod.PUT, "/api/usuarios/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/motoristas/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/caminhoes/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/tipocoleta/**").hasAuthority("ROLE_SUPER_ADMIN")
                            .requestMatchers(HttpMethod.PUT, "/api/tiporesiduo/**").hasAuthority("ROLE_SUPER_ADMIN")

                            .requestMatchers(HttpMethod.GET, "/api/motoristas/**")
                            .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN_CONSULTA", "ROLE_APP_ANDROID")

                            // Operações de consulta (GET) - SUPER_ADMIN, ADMIN_CONSULTA ou APP_ANDROID
                            .requestMatchers(HttpMethod.GET, "/api/usuarios/meu-perfil").authenticated()
                            .requestMatchers(HttpMethod.GET, "/api/**")
                            .hasAnyAuthority("ROLE_SUPER_ADMIN", "ROLE_ADMIN_CONSULTA", "ROLE_APP_ANDROID")

                            // Qualquer outra requisição requer autenticação
                            .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(emailOtpAuthenticationProvider)
            // ✅ Adicionar AppTokenFilter ANTES do JwtAuthenticationFilter
            .addFilterBefore(appTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  /** Retorna um AuthorizationManager que concede acesso somente quando o perfil ativo inclui "test". */
  private AuthorizationManager<RequestAuthorizationContext> isTestProfile() {
    return (authentication, context) ->
            Arrays.asList(environment.getActiveProfiles()).contains("test")
                    ? new AuthorizationDecision(true)
                    : new AuthorizationDecision(false);
  }

  /** Retorna um AuthorizationManager que permite acesso apenas se o Swagger estiver habilitado. */
  private AuthorizationManager<RequestAuthorizationContext> isSwaggerEnabled() {
    return (authentication, context) ->
            isSwaggerEnabled ? new AuthorizationDecision(true) : new AuthorizationDecision(false);
  }

  /** Cria e retorna a configuração de CORS para a aplicação. */
  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOriginPatterns(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    // ✅ Adicionar X-App-Token aos headers permitidos
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "X-App-Token"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  /** Fornece o bean AuthenticationManager configurado pelo Spring Security. */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
          throws Exception {
    return config.getAuthenticationManager();
  }
}

