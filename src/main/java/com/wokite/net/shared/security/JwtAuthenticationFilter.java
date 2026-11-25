package com.wokite.net.shared.security;


import com.wokite.net.shared.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(7);

        try {
            AuthenticatedUser user = jwtService.parseUser(token);

            AuthenticatedPrincipal principal = new AuthenticatedPrincipal(user);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            if (user.entrepriseUuid() != null) {
                TenantContext.setTenantUuid(user.entrepriseUuid());
            }

            filterChain.doFilter(request, response);

        } catch (Exception ignored) {

            SecurityContextHolder.clearContext();

            filterChain.doFilter(request, response);

        } finally {

            TenantContext.clear();
        }
    }
}