package app.database;

import java.util.List;

/**
 * @author chi
 */
public interface Repository<T> {
    @Deprecated
    Query<T> query(String query, Object... args);

    QueryV2<T> queryV2(String query);

    <K> QueryV2<K> queryV2(String query, Class<K> returnType);

    T get(Object id);

    List<T> batchGet(List ids);

    T insert(T entity);

    List<T> batchInsert(List<T> entities);

    T update(Object id, T entity);

    boolean delete(Object id);

    int batchDelete(List ids);

    @Deprecated
    int execute(String query, Object... args);

    Statement statement(String query);
}
