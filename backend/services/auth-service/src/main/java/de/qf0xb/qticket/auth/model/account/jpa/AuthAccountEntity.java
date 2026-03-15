package de.qf0xb.qticket.auth.model.account.jpa;

import de.qf0xb.qticket.auth.model.account.TwoFaType;
import de.qf0xb.qticket.auth.model.rbac.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.NullMarked;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@NullMarked
@Getter
@Setter
@Entity
@ToString(exclude = {"authTwoFaMethodEntities", "roleEntities"})
@Table(
        name = "auth_account_entity"
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthAccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
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
    @Builder.Default
    private Set<AuthTwoFaMethodEntity> authTwoFaMethodEntities = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "auth_account_roles",
            joinColumns = @JoinColumn(name = "auth_account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<RoleEntity> roleEntities = new LinkedHashSet<>();

    @Embedded
    @Builder.Default
    private AuthAccountStatus authAccountStatus = new AuthAccountStatus();

    public boolean isTwoFaRequired() {
        return !authTwoFaMethodEntities.isEmpty();
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