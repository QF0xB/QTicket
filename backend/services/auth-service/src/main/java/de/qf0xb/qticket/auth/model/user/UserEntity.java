package de.qf0xb.qticket.auth.model.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@ToString
@Table(name = "user_entity")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String passwordHash;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Embedded
    private UserEntityAuditInfo userAuditInfo;

    @Embedded
    private UserEntityStatusInfo userStatusInfo;

    public UserEntity() {
        this.userAuditInfo = new UserEntityAuditInfo();
        this.userStatusInfo = new UserEntityStatusInfo();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return userStatusInfo.getExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return !userStatusInfo.getLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userStatusInfo.getExpired();
    }

    @Override
    public boolean isEnabled() {
        return userStatusInfo.getEnabled() && userStatusInfo.getEmailVerified();
    }
}