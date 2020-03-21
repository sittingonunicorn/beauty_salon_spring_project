package net.ukr.lina_chen.beauty_salon_spring_project.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username", "user_id"})})
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "user_id", nullable = false)
    private Long id;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "password", nullable = false)
    @Size(min = 8, max = 32, message = "Password should be from 8 to 32 symbols.")
    private String password;
    @Column(name = "username", nullable = false)
    @Size(min = 8, max = 32, message = "Username should be from 8 to 32 symbols.")
    private String username;
    @Column(name = "name", nullable = false)
    @Size(min = 2, max = 32, message = "Username should be from 2 to 32 symbols.")
    private String name;
//    @OneToMany(mappedBy = "user", cascade = CascadeType.MERGE)
//    private Set<Comment> comments;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> result = new HashSet<>();
        result.add(this.getRole());
        return result;
    }

    public boolean isAdmin() {
        return role.equals(Role.ADMIN);
    }

    public boolean isMaster() {
        return role.equals(Role.MASTER);
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