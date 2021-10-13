package app.database;

import javax.persistence.EntityManager;
import java.sql.SQLException;

/**
 * @author chi
 */
public interface Database {
    <T> Query<T> query(String sql, Class<T> viewClass, Object... params);

    int execute(String sql, Object... params) throws SQLException;

    EntityManager em();
}
