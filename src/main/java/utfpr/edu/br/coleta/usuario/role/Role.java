package utfpr.edu.br.coleta.usuario.role;

import org.springframework.security.core.GrantedAuthority;

/**
 * Roles disponíveis no sistema para controle de permissões.
 */
public enum Role implements GrantedAuthority {
    ROLE_SUPER_ADMIN,
    ROLE_ADMIN_CONSULTA;

    @Override
    public String getAuthority() {
        return name();
    }
}