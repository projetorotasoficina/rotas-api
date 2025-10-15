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
 * Este filtro intercepta requisições para endpoints que o app Android pode acessar
 * e valida se o header "X-App-Token" contém um token válido.
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
        new EndpointPermitido("GET", "/api/rotas"),
        new EndpointPermitido("GET", "/api/caminhoes"),
        new EndpointPermitido("POST", "/api/trajetos"),
        new EndpointPermitido("POST", "/api/incidentes")
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        // Verifica se é um endpoint que requer validação de app token
        if (requerValidacaoAppToken(method, path)) {
            String appToken = request.getHeader("X-App-Token");

            if (appToken == null || appToken.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"erro\":\"Token do aplicativo ausente\"}");
                return;
            }

            if (!appTokenService.isValidToken(appToken)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"erro\":\"Token do aplicativo inválido\"}");
                return;
            }

            // ✅ NOVO: Autenticar no contexto do Spring Security
            // Cria uma autenticação com a role ROLE_APP_ANDROID
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken(
                    "app-android", // principal (identificador)
                    null, // credentials (não precisa de senha)
                    List.of(new SimpleGrantedAuthority("ROLE_APP_ANDROID")) // authorities
                );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Atualiza último acesso
            appTokenService.updateLastAccess(appToken);
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

