package app.scheduler;

import app.AbstractModule;
import app.Binder;
import app.Configurable;
import app.scheduler.impl.SchedulerConfigImpl;
import app.scheduler.impl.SchedulerService;

/**
 * @author chi
 */
public class SchedulerModule extends AbstractModule implements Configurable<SchedulerConfig> {
    private final SchedulerService schedulerService = SchedulerService.INSTANCE;

    @Override
    protected void configure() {
        onReady(schedulerService::start);
        onShutdown(schedulerService::stop);
    }

    @Override
    public SchedulerConfig configurator(AbstractModule module, Binder binder) {
        return new SchedulerConfigImpl(schedulerService);
    }
}
