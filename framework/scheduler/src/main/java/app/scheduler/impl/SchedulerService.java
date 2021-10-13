package app.scheduler.impl;

import app.util.exception.Errors;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author chi
 */
public class SchedulerService {
    private final Logger logger = LoggerFactory.getLogger(SchedulerService.class);

    public static final SchedulerService INSTANCE = new SchedulerService();
    private Scheduler scheduler;
    private final Map<String, Task> tasks = Maps.newConcurrentMap();

    public void schedule(String cron, Runnable task) {
        String id = UUID.randomUUID().toString();
        tasks.put(id, new Task(id, cron, task));
    }

    public void start() {
        try {
            SchedulerFactory sf = new StdSchedulerFactory();
            scheduler = sf.getScheduler();
            scheduler.start();

            for (Map.Entry<String, Task> entry : tasks.entrySet()) {
                Task task = entry.getValue();
                JobDetail job = newJob(SchedulerJob.class)
                    .withIdentity(task.id)
                    .build();

                CronTrigger trigger = newTrigger()
                    .withIdentity(task.id)
                    .withSchedule(cronSchedule(task.cron))
                    .build();
                scheduler.scheduleJob(job, trigger);
            }
        } catch (SchedulerException e) {
            throw Errors.internalError(e);
        }
    }

    public void stop() {
        try {
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            throw Errors.internalError(e);
        }
    }

    private void trigger(String taskId) {
        Task task = tasks.get(taskId);
        Stopwatch w = Stopwatch.createStarted();
        try {
            task.task.run();
            logger.info("task finished, task={}, in {}ms", task.task.getClass().getCanonicalName(), w.elapsed(TimeUnit.MILLISECONDS));
        } catch (Exception e) {
            logger.error("failed to execute task, cron={}, task={}", task.cron, task.task.getClass().getCanonicalName(), e);
        }
    }

    public static class SchedulerJob implements Job {
        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            String taskId = context.getJobDetail().getKey().getName();
            INSTANCE.trigger(taskId);
        }
    }

    public static class Task {
        public final String id;
        public final String cron;
        public final Runnable task;

        public Task(String id, String cron, Runnable task) {
            this.id = id;
            this.cron = cron;
            this.task = task;
        }
    }
}
