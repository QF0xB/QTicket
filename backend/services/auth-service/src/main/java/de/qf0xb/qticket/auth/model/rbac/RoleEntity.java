package de.qf0xb.qticket.auth.model.rbac;

import de.qf0xb.qticket.security.rbac.AppPermission;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@NullMarked
@Getter
@Setter
@ToString(exclude = {"appPermission", "parent"})
@Entity
@Table(name = "role_entity")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private @Nullable String description;

    // Allow null for root roles. Hierarchy
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private @Nullable RoleEntity parent;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @Column(name = "app_permission")
    @CollectionTable(name = "role_entity_app_permission", joinColumns = @JoinColumn(name = "owner_id"))
    @Builder.Default
    private Set<AppPermission> appPermission = new LinkedHashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        RoleEntity that = (RoleEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}