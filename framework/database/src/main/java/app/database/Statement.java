package app.database;

/**
 * @author chi
 */
public interface Statement {
    Statement param(String name, Object value);

    Statement append(String sql);

    int execute();
}
