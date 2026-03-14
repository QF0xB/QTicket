package de.qf0xb.qticket.auth.model.account;

import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@ToString(exclude = {"authTwoFaMethodEntities", "roleEntities"})
@Table(
        name = "auth_account_entity"
)
public class AuthAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    // Login identifiers
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // Password
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @OneToMany(mappedBy = "authAccount", orphanRemoval = true)
    private Set<AuthTwoFaMethodEntity> authTwoFaMethodEntities = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "auth_account_roles",
            joinColumns = @JoinColumn(name = "auth_account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roleEntities = new LinkedHashSet<>();

    @Embedded
    private AuthAccountStatus authAccountStatus;

    public boolean isTwoFaRequired() {
        return authTwoFaMethodEntities != null && !authTwoFaMethodEntities.isEmpty();
    }

    public Set<TwoFaType> getTwoFaTypes() {
        return authTwoFaMethodEntities.stream().map(AuthTwoFaMethodEntity::getTwoFaType).collect(Collectors.toSet());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        AuthAccountEntity that = (AuthAccountEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}