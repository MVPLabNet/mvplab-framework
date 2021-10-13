package app.database;


import app.util.collection.QueryResponse;

import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public interface QueryV2<T> {
    QueryV2<T> append(String condition);

    QueryV2<T> param(String name, Object value);

    QueryResponse<T> findAll();

    List<T> find();

    Optional<T> findOne();

    long count();

    QueryV2<T> limit(Integer page, Integer limit);

    QueryV2<T> sort(String field, Boolean desc);

    default QueryV2<T> sort(String field) {
        return sort(field, false);
    }
}
