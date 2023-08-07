package com.lucasjosino.hawapi.repositories.base.impl;

import com.lucasjosino.hawapi.models.base.BaseModel;
import com.lucasjosino.hawapi.repositories.base.BaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.UUID;

public class BaseRepositoryImpl<M extends BaseModel, ID> extends SimpleJpaRepository<M, ID> implements BaseRepository<M, ID> {

    private final JpaEntityInformation<M, ID> jpaEntityInformation;

    private final EntityManager entityManager;

    @Autowired
    public BaseRepositoryImpl(
            JpaEntityInformation<M, ID> jpaEntityInformation,
            EntityManager entityManager
    ) {
        super(jpaEntityInformation, entityManager);
        this.jpaEntityInformation = jpaEntityInformation;
        this.entityManager = entityManager;
    }

    @Override
    public Page<UUID> findAllUUIDs(Specification<M> specification, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();

        CriteriaQuery<UUID> query = builder.createQuery(UUID.class);
        Root<M> root = query.from(jpaEntityInformation.getJavaType());

        query.select(root.get("uuid"));
        query.where(specification.toPredicate(root, query, builder));
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        TypedQuery<UUID> futureRes = entityManager.createQuery(query);
        futureRes.setFirstResult((int) pageable.getOffset());
        futureRes.setMaxResults(pageable.getPageSize());

        long count = this.count(specification);
        return PageableExecutionUtils.getPage(futureRes.getResultList(), pageable, () -> count);
    }
}
