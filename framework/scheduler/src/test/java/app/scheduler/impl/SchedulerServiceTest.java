package app.scheduler.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author chi
 */
class SchedulerServiceTest {
    private boolean triggered = false;

    @Test
    void schedule() throws InterruptedException {
        SchedulerService schedulerService = SchedulerService.INSTANCE;
        schedulerService.schedule("1/5 * * * * ?", () -> triggered = true);
        schedulerService.start();

        Thread.sleep(10000);

        assertTrue(triggered);
        schedulerService.stop();
    }
}