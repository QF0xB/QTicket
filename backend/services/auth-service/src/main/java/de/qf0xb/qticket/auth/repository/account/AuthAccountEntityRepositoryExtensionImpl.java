package de.qf0xb.qticket.auth.repository.account;

import de.qf0xb.qticket.auth.model.account.jpa.AuthAccountEntity;
import de.qf0xb.qticket.auth.service.SearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class AuthAccountEntityRepositoryExtensionImpl implements AuthAccountEntityRepositoryExtension {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<AuthAccountEntity> search(SearchRequest req) {
        Pageable pageable = PageRequest.of(req.page(), req.size(), parseSort(req.sort()));

        CriteriaBuilder cb = em.getCriteriaBuilder();

        // main query
        var cq = cb.createQuery(AuthAccountEntity.class);
        var root = cq.from(AuthAccountEntity.class);

        List<Predicate> predicates = new ArrayList<>();

        if (req.q() != null && !req.q().isBlank()) {
            String like = "%" + req.q().toLowerCase() + "%";
            predicates.add(
                    cb.or(
                            cb.like(cb.lower(root.get("username")), like),
                            cb.like(cb.lower(root.get("email")), like)
                    )
            );
        }

        if (req.username() != null && !req.username().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("username")), "%" + req.username().toLowerCase() + "%"));
        }

        if (req.email() != null && !req.email().isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("email")), "%" + req.email().toLowerCase() + "%"));
        }

        if (req.enabled() != null) {
            predicates.add(
                    cb.equal(root.get("authAccountStatus").get("enabled"), req.enabled())
            );
        }
        if (req.emailVerified() != null) {
            predicates.add(
                    cb.equal(root.get("authAccountStatus").get("emailVerified"), req.emailVerified())
            );
        }
        if (req.locked() != null) {
            predicates.add(
                    cb.equal(root.get("authAccountStatus").get("locked"), req.locked())
            );
        }

        if(req.userId() != null) {
            predicates.add(cb.equal(root.get("userId"), req.userId()));
        }

        cq.where(predicates.toArray(Predicate[]::new));
        cq.orderBy(toOrders(cb, root, pageable.getSort()));

        TypedQuery<AuthAccountEntity> query = em.createQuery(cq);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<AuthAccountEntity> content = query.getResultList();

        // count query
        var countCq = cb.createQuery(Long.class);
        var countRoot = countCq.from(AuthAccountEntity.class);
        countCq.select(cb.count(countRoot));
        countCq.where(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        long total = em.createQuery(countCq).getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

    private Sort parseSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "username");
        }

        String[] parts = sort.split(",");
        String prop = parts[0].trim();
        Sort.Direction dir = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc"))
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        return Sort.by(dir, prop);
    }

    private List<jakarta.persistence.criteria.Order> toOrders(
            jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Root<AuthAccountEntity> root,
            Sort sort
    ) {
        List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
        for (Sort.Order o : sort) {
            if (o.isAscending()) {
                orders.add(cb.asc(root.get(o.getProperty())));
            } else {
                orders.add(cb.desc(root.get(o.getProperty())));
            }
        }
        return orders;
    }
}
