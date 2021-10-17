package app.database.impl;

import app.database.DatabaseModule;
import app.test.AppExtension;
import app.test.Install;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author chi
 */
@ExtendWith(AppExtension.class)
@Install({DatabaseModule.class, TestModule.class})
class TransactionalInterceptorTest {
    @Inject
    TestService testService;

    @Test
    void transaction() {
        TestEntity t = new TestEntity();
        t.id = UUID.randomUUID().toString();

        testService.create(t);
        TestEntity testEntity = testService.get(t.id);
        assertNotNull(testEntity);
    }

    @Test
    void nestTransaction() {
        TestEntity t = new TestEntity();
        t.id = UUID.randomUUID().toString();
        testService.create(t);

        testService.nestTransaction(t);
        TestEntity testEntity = testService.get(t.id);
        assertEquals("Test2", testEntity.name);
    }

    @Test
    void rollback() {
        TestEntity t = new TestEntity();
        t.id = UUID.randomUUID().toString();
        t.name = "Test";
        testService.create(t);
        assertThrows(WebApplicationException.class, () -> {
            testService.rollback(t);
        });
        TestEntity testEntity = testService.get(t.id);
        assertEquals("Test", testEntity.name);
    }

    @Test
    void notRollback() {
        TestEntity t = new TestEntity();
        t.id = UUID.randomUUID().toString();
        t.name = "Test";
        testService.create(t);
        assertThrows(WebApplicationException.class, () -> {
            testService.notRollback(t);
        });
        TestEntity testEntity = testService.get(t.id);
        assertEquals("Test1", testEntity.name);
    }

    @Test
    void rollbackOn() {
        TestEntity t = new TestEntity();
        t.id = UUID.randomUUID().toString();
        t.name = "Test";
        testService.create(t);
        assertThrows(WebApplicationException.class, () -> {
            testService.rollbackON(t);
        });
        TestEntity testEntity = testService.get(t.id);
        assertEquals("Test", testEntity.name);
    }

}
