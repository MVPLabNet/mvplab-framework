package app.test;

import app.test.impl.Test2Service;
import app.test.impl.TestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author chi
 */
@ExtendWith(AppExtension.class)
@Install(Test2Module.class)
class AppExtensionTest {
    @Inject
    TestService testService;
    @Inject
    Test2Service test2Service;

    @Test
    void configure() {
        assertNotNull(testService);
        assertNotNull(test2Service);
    }
}