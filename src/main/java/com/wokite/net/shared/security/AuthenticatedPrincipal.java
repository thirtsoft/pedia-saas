package com.wokite.net.shared.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public class AuthenticatedPrincipal implements UserDetails {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient AuthenticatedUser user;

    public AuthenticatedPrincipal(AuthenticatedUser user) {
        this.user = user;
    }

    public AuthenticatedUser getUser() {
        return user;
    }

    public UUID getUtilisateurUuid() {
        return user.utilisateurUuid();
    }

    public UUID getEntrepriseUuid() {
        return user.entrepriseUuid();
    }

    public String getRole() {
        return user.role();
    }

    public Set<String> getPermissions() {
        return user.permissions();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.permissions()
                .stream()
                .map(permission ->
                        (GrantedAuthority) () -> permission
                )
                .toList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.utilisateurUuid().toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}