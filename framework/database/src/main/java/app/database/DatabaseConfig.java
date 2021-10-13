package app.database;

/**
 * @author chi
 */
public interface DatabaseConfig {
    <T> DatabaseConfig entity(Class<T> entityClass);
}
