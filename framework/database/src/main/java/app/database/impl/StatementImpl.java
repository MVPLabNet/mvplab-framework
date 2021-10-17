package app.database.impl;

import app.database.Database;
import app.database.Statement;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author chi
 */
public class StatementImpl implements Statement {
    private final Logger logger = LoggerFactory.getLogger(StatementImpl.class);
    private final Class<?> entityClass;
    private final Database database;
    private final SQLBuilder sql;
    private final Map<String, Object> params;

    public StatementImpl(Class<?> entityClass, String entityName, String idFieldName, Database database, String query) {
        this.entityClass = entityClass;
        this.database = database;
        sql = new SQLBuilder(entityName, idFieldName, query);
        this.params = Maps.newHashMap();
    }

    @Override
    public Statement append(String condition) {
        sql.append(condition);
        return this;
    }

    @Override
    public int execute() {
        Stopwatch watch = Stopwatch.createStarted();
        EntityManager em = em();
        try {
            jakarta.persistence.Query query = em.createQuery(sql.sql());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                Object value = entry.getValue();
                query.setParameter(entry.getKey(), value);
            }
            return query.executeUpdate();
        } finally {
            logger.debug("execute, sql={}, entityClass={}, elapsedTime={}", sql, entityClass.getName(), watch.elapsed(TimeUnit.MILLISECONDS));
            em.close();
        }
    }

    @Override
    public Statement param(String name, Object value) {
        params.put(name, value);
        return this;
    }

    private EntityManager em() {
        return database.em();
    }
}
