package utfpr.edu.br.coleta.aplicativoandroid.apptoken;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Filtro que valida o token do aplicativo Android em requisições específicas.
 * 
 * Este filtro APENAS processa requisições que contêm o header "X-App-Token".
 * Requisições com "Authorization: Bearer {jwt}" (usuários web) são ignoradas
 * e processadas pelo JwtAuthenticationFilter.
 * 
 * Endpoints permitidos para o app Android:
 * - GET /api/motoristas/** (consultar motoristas)
 * - GET /api/rotas/** (consultar rotas)
 * - GET /api/caminhoes/** (consultar caminhões)
 * - POST /api/trajetos (criar trajeto)
 * - POST /api/incidentes (criar incidente)
 * 
 * @author Luiz Alberto dos Passos
 */
@Component
@RequiredArgsConstructor
public class AppTokenFilter extends OncePerRequestFilter {

    private final IAppTokenService appTokenService;

    // Endpoints que o app Android pode acessar
    private static final List<EndpointPermitido> ENDPOINTS_PERMITIDOS = List.of(
        new EndpointPermitido("GET", "/api/motoristas"),
        new EndpointPermitido("GET", "/api/rota"),
        new EndpointPermitido("GET", "/api/caminhoes"),
        new EndpointPermitido("POST", "/api/trajetos"),
        new EndpointPermitido("PUT",  "/api/trajetos"),   
        new EndpointPermitido("POST", "/api/incidentes"),
        new EndpointPermitido("POST", "/api/pontos-trajeto")     
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();
        String appToken = request.getHeader("X-App-Token");
        String authHeader = request.getHeader("Authorization");

        // ✅ CORREÇÃO: Só processa se tiver X-App-Token E não tiver Authorization
        // Se tem Authorization (JWT), deixa o JwtAuthenticationFilter processar
        if (appToken != null && !appToken.isBlank() && 
            (authHeader == null || !authHeader.startsWith("Bearer "))) {
            
            // Verifica se é um endpoint que o app Android pode acessar
            if (requerValidacaoAppToken(method, path)) {
                
                if (!appTokenService.isValidToken(appToken)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"erro\":\"Token do aplicativo inválido\"}");
                    return;
                }

                // Autenticar no contexto do Spring Security com ROLE_APP_ANDROID
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        "app-android",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_APP_ANDROID"))
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Atualiza último acesso
                appTokenService.updateLastAccess(appToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Verifica se o endpoint requer validação de app token.
     * 
     * @param method método HTTP
     * @param path caminho da requisição
     * @return true se requer validação, false caso contrário
     */
    private boolean requerValidacaoAppToken(String method, String path) {
        return ENDPOINTS_PERMITIDOS.stream()
                .anyMatch(ep -> ep.matches(method, path));
    }

    /**
     * Classe interna para representar um endpoint permitido.
     */
    private record EndpointPermitido(String method, String pathPrefix) {
        boolean matches(String reqMethod, String reqPath) {
            return method.equalsIgnoreCase(reqMethod) && reqPath.startsWith(pathPrefix);
        }
    }
}
