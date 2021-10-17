package app.database.impl;

import app.database.Database;
import app.database.QueryV2;
import app.util.collection.QueryResponse;
import com.google.common.collect.Maps;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chi
 */
public class QueryV2Impl<T> implements QueryV2<T> {
    private final Class<T> entityClass;
    private final Database database;
    private final SQLBuilder sql;
    private final Map<String, Object> params;
    private Integer limit;
    private Integer page;

    public QueryV2Impl(Class<T> entityClass, String entityName, String idFieldName, Database database, String query) {
        this.entityClass = entityClass;
        this.database = database;
        sql = new SQLBuilder(entityName, idFieldName, query);
        this.params = Maps.newHashMap();
    }

    @Override
    public QueryV2<T> append(String condition) {
        sql.append(condition);
        return this;
    }

    @Override
    public QueryV2<T> param(String name, Object value) {
        params.put(name, value);
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
        EntityManager em = em();
        try {
            TypedQuery<T> query = em.createQuery(sql.selectSQL(), entityClass);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                query.setParameter(entry.getKey(), value);
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
        EntityManager em = em();
        try {
            TypedQuery<T> query = em.createQuery(sql.selectSQL(), entityClass);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                query.setParameter(entry.getKey(), value);
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
        EntityManager em = em();
        try {
            TypedQuery<Long> query = em.createQuery(sql.countSQL(), Long.class);
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                query.setParameter(entry.getKey(), value);
            }
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public QueryV2<T> limit(Integer page, Integer limit) {
        this.page = page;
        this.limit = limit == null ? Integer.valueOf(100) : limit;
        return this;
    }

    @Override
    public QueryV2<T> sort(String field, Boolean desc) {
        sql.sort(field, desc);
        return this;
    }

    private EntityManager em() {
        return database.em();
    }
}
