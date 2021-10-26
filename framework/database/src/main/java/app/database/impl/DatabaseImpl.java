package app.database.impl;


import app.database.Database;
import app.database.DatabaseOptions;
import app.database.Query;
import app.util.exception.Errors;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.SharedCacheMode;
import jakarta.persistence.ValidationMode;
import jakarta.persistence.spi.ClassTransformer;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.PersistenceUnitTransactionType;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import javax.sql.DataSource;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author chi
 */
public class DatabaseImpl implements Database {
    private final ThreadLocal<Transaction> currentTransaction = new ThreadLocal<>();
    private final Set<String> entityClassNames = Sets.newHashSet();
    private final DatabaseOptions options;
    private EntityManagerFactory emf;
    private Path dir;
    private DataSource dataSource;

    public DatabaseImpl(DatabaseOptions options) {
        this.options = options;
    }

    public void setDir(Path dir) {
        this.dir = dir;
    }

    public void start() {
        emf = createEMF();
    }

    public void addEntityClassName(Class<?> entityClass) {
        entityClassNames.add(entityClass.getName());
    }

    private EntityManagerFactory createEMF() {
        PersistenceUnitInfo persistenceUnitInfo = persistenceUnitInfo();
        Map<String, Object> configuration = new HashMap<>();
        return new EntityManagerFactoryBuilderImpl(new PersistenceUnitInfoDescriptor(persistenceUnitInfo), configuration).build();
    }

    private PersistenceUnitInfoImpl persistenceUnitInfo() {
        return new PersistenceUnitInfoImpl("database", ImmutableList.copyOf(entityClassNames), properties());
    }

    Transaction transaction() {
        Transaction transaction = currentTransaction.get();
        if (transaction == null) {
            transaction = new Transaction(this);
            currentTransaction.set(transaction);
        }
        return transaction;
    }

    void resetTransaction() {
        currentTransaction.remove();
    }

    private Properties properties() {
        Properties properties = new Properties();
        if (options.createTableEnabled) {
            properties.put("hibernate.hbm2ddl.auto", "update");
        }
        properties.put("hibernate.connection.datasource", dataSource());
        properties.put("javax.persistence.validation.mode", "NONE");
        properties.put("hibernate.show_sql", options.showSQLEnabled);
        return properties;
    }

    public DataSource dataSource() {
        if (this.dataSource == null) {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(resetBaseDir(options.url));
            basicDataSource.setUsername(options.username);
            basicDataSource.setPassword(options.password);
            basicDataSource.setMaxTotal(options.pool.max);
            basicDataSource.setMinIdle(options.pool.min);
            this.dataSource = basicDataSource;
        }
        return this.dataSource;
    }

    private String resetBaseDir(String jdbcURL) {
        if (jdbcURL != null && dir != null) {
            return jdbcURL.replace(":./", ':' + dir.resolve("database").toString() + '/');
        }
        return jdbcURL;
    }

    @Override
    public <T> Query<T> query(String sql, Class<T> viewClass, Object... params) {
        return new RepositoryImpl<>(viewClass, this).query(sql, params);
    }

    @Override
    public int execute(String sql, Object... params) {
        EntityManager em = em();
        try {
            jakarta.persistence.Query query = em.createQuery(sql);
            for (int i = 0; i < params.length; i++) {
                query.setParameter(i, params[i]);
            }
            return query.executeUpdate();
        } catch (Exception e) {
            throw Errors.internalError("failed to execute sql, sql={}, params={}", sql, params, e);
        } finally {
            em.close();
        }
    }

    @Override
    public EntityManager em() {
        Transaction transaction = currentTransaction.get();
        if (transaction == null) {
            return emf.createEntityManager();
        } else {
            return transaction.em();
        }
    }


    static class PersistenceUnitInfoImpl implements PersistenceUnitInfo {
        private final String name;
        private final List<String> entityClassNames;
        private final Properties properties;

        PersistenceUnitInfoImpl(String name, List<String> entityClassNames, Properties properties) {
            this.name = name;
            this.entityClassNames = entityClassNames;
            this.properties = properties;
        }

        @Override
        public String getPersistenceUnitName() {
            return name;
        }

        @Override
        public String getPersistenceProviderClassName() {
            return null;
        }

        @Override
        public PersistenceUnitTransactionType getTransactionType() {
            return PersistenceUnitTransactionType.RESOURCE_LOCAL;
        }

        @Override
        public DataSource getJtaDataSource() {
            return null;
        }

        @Override
        public DataSource getNonJtaDataSource() {
            return null;
        }

        @Override
        public List<String> getMappingFileNames() {
            return null;
        }

        @Override
        public List<URL> getJarFileUrls() {
            return null;
        }

        @Override
        public URL getPersistenceUnitRootUrl() {
            return null;
        }

        @Override
        public List<String> getManagedClassNames() {
            return entityClassNames;
        }

        @Override
        public boolean excludeUnlistedClasses() {
            return false;
        }

        @Override
        public SharedCacheMode getSharedCacheMode() {
            return null;
        }

        @Override
        public ValidationMode getValidationMode() {
            return ValidationMode.AUTO;
        }

        @Override
        public Properties getProperties() {
            return properties;
        }

        @Override
        public String getPersistenceXMLSchemaVersion() {
            return null;
        }

        @Override
        public ClassLoader getClassLoader() {
            return null;
        }

        @Override
        public void addTransformer(ClassTransformer transformer) {
        }

        @Override
        public ClassLoader getNewTempClassLoader() {
            return null;
        }
    }
}
