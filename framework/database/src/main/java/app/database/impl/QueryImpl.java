package app.database.impl;

import app.database.Database;
import app.util.collection.QueryResponse;
import com.google.common.collect.Lists;
import app.database.Query;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public class QueryImpl<T> implements Query<T> {
    private final Class<T> entityClass;
    private final Database database;
    private final SQLBuilder sql;
    private final List<Object> params;

    private Integer limit;
    private Integer page;

    public QueryImpl(Class<T> entityClass, String entityName, String idFieldName, Database database, String query, Object... params) {
        this.entityClass = entityClass;
        this.database = database;
        sql = new SQLBuilder(entityName, idFieldName, query);
        this.params = Lists.newArrayList(Arrays.asList(params));
    }

    @Override
    public Query<T> append(String condition, Object... args) {
        sql.append(condition);
        params.addAll(Arrays.asList(args));
        return this;
    }

    @Override
    public QueryResponse<T> findAll() {
        QueryResponse<T> queryResponse = new QueryResponse<>();
        queryResponse.page = page;
        queryResponse.limit = limit;
        queryResponse.items = find();
        queryResponse.total = count();
        return queryResponse;
    }

    @Override
    public List<T> find() {
        EntityManager em = database.em();
        try {
            TypedQuery<T> query = em.createQuery(sql.selectSQL(), entityClass);
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i, params.get(i));
            }
            if (page != null) {
                query.setFirstResult((page - 1) * limit);
                query.setMaxResults(limit);
            }
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<T> findOne() {
        EntityManager em = database.em();
        try {
            TypedQuery<T> query = em.createQuery(sql.selectSQL(), entityClass);
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i, params.get(i));
            }
            query.setFirstResult(0);
            query.setMaxResults(1);
            List<T> results = query.getResultList();
            if (results.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    @Override
    public long count() {
        EntityManager em = database.em();
        try {
            TypedQuery<Long> query = em.createQuery(sql.countSQL(), Long.class);
            for (int i = 0; i < params.size(); i++) {
                query.setParameter(i, params.get(i));
            }
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Query<T> limit(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit == null ? Integer.valueOf(100) : limit;
        return this;
    }

    @Override
    public Query<T> sort(String field, Boolean desc) {
        sql.sort(field, desc);
        return this;
    }
}
