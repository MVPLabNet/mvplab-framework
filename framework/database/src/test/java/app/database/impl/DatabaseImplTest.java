package app.database.impl;

import app.database.DatabaseOptions;
import app.test.TempDirectoryExtension;
import org.hibernate.dialect.HSQLDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author chi
 */
@ExtendWith(TempDirectoryExtension.class)
class DatabaseImplTest {
    DatabaseImpl database;

    @BeforeEach
    void setup() {
        database = database();
        database.addEntityClassName(TestEntity.class);
        database.addEntityClassName(TestView.class);
        database.start();

        EntityManager em = database.em();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        try {
            TestEntity entity = new TestEntity();
            entity.id = UUID.randomUUID().toString();
            em.persist(entity);
        } finally {
            transaction.commit();
            em.close();
        }
    }

    @Test
    void query() {
        Optional<TestEntity> entity = database.query("select t from TestEntity t", TestEntity.class).findOne();
        assertTrue(entity.isPresent());
    }

    @Test
    void queryView() {
        Optional<TestView> entity = database.query("select new app.database.impl.TestView(t.id, t.name) from TestEntity t", TestView.class).findOne();
        assertTrue(entity.isPresent());
    }

    private DatabaseImpl database() {
        DatabaseOptions options = new DatabaseOptions();
        options.url = "jdbc:hsqldb:mem:db;sql.syntax_mys=true";
        options.createTableEnabled = true;
        options.dialect = HSQLDialect.class.getName();
        options.driver = "org.hsqldb.jdbc.JDBCDriver";

        return new DatabaseImpl(options);
    }
}
