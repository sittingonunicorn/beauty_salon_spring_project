package net.ukr.lina_chen.beauty_salon_spring_project.entity;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    MASTER,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
