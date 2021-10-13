package app.database;


import app.util.collection.QueryResponse;

import java.util.List;
import java.util.Optional;

/**
 * @author chi
 */
public interface Query<T> {
    Query<T> append(String condition, Object... args);

    QueryResponse<T> findAll();

    List<T> find();

    Optional<T> findOne();

    long count();

    Query<T> limit(Integer page, Integer limit);

    Query<T> sort(String field, Boolean desc);

    default Query<T> sort(String field) {
        return sort(field, false);
    }
}
