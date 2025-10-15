package utfpr.edu.br.coleta.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import utfpr.edu.br.coleta.aplicativoandroid.apptoken.AppTokenFilter;

/**
 * Configuração de segurança para adicionar o AppTokenFilter à cadeia de filtros.
 * 
 * IMPORTANTE: Esta configuração deve ser integrada ao SecurityConfig existente.
 * 
 * No seu SecurityConfig.java, adicione o AppTokenFilter ANTES do JwtAuthenticationFilter:
 * 
 * <pre>
 * {@code
 * @Bean
 * public SecurityFilterChain securityFilterChain(
 *         HttpSecurity http,
 *         JwtAuthenticationFilter jwtAuthenticationFilter,
 *         AppTokenFilter appTokenFilter) throws Exception {
 *     
 *     http
 *         // ... outras configurações ...
 *         .addFilterBefore(appTokenFilter, UsernamePasswordAuthenticationFilter.class)
 *         .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
 *     
 *     return http.build();
 * }
 * }
 * </pre>
 * 
 * E adicione o endpoint de ativação como público:
 * 
 * <pre>
 * {@code
 * .authorizeHttpRequests(auth -> auth
 *     .requestMatchers("/api/app/activate").permitAll()  // Endpoint público
 *     // ... outras regras ...
 * )
 * }
 * </pre>
 * 
 * @author Luiz Alberto dos Passos
 */
@Configuration
public class AppTokenSecurityConfig {
    // Esta classe serve apenas como documentação.
    // A configuração real deve ser feita no SecurityConfig existente.
}

