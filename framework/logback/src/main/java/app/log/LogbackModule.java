package app.log;

import app.AbstractModule;
import app.util.exception.Errors;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.rolling.DefaultTimeBasedFileNamingAndTriggeringPolicy;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author chi
 */

public class LogbackModule extends AbstractModule {
    static {
        System.setProperty("org.jboss.logging.provider", "slf4j");

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern("%d [%thread] %-5level %logger{5} - %msg %n");
        ple.setContext(lc);
        ple.start();

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(new ContextBase());
        appender.setEncoder(ple);
        appender.start();

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.detachAndStopAllAppenders();
        root.setLevel(Level.INFO);
        root.addAppender(appender);
    }

    @Override
    protected void configure() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.detachAndStopAllAppenders();

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        LogbackOptions options = options("log", LogbackOptions.class);
        if (options.dir == null) {
            root.addAppender(createConsoleAppender(lc, options));
            root.setLevel(Level.valueOf(options.level));
        } else {
            root.addAppender(createFileAppender(app().dataDir().resolve(options.dir), lc, options));
            root.setLevel(Level.valueOf(options.level));
        }

        for (String packageName : options.excludePackages) {
            Logger logger = (Logger) LoggerFactory.getLogger(packageName);
            logger.setLevel(Level.WARN);
        }
    }

    private Appender<ILoggingEvent> createConsoleAppender(LoggerContext lc, LogbackOptions options) {
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern(options.pattern);
        ple.setContext(lc);
        ple.start();

        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        appender.setContext(new ContextBase());
        appender.setEncoder(ple);
        appender.start();
        return appender;
    }

    private Appender<ILoggingEvent> createFileAppender(Path dir, LoggerContext lc, LogbackOptions options) {
        PatternLayoutEncoder ple = new PatternLayoutEncoder();
        ple.setPattern(options.pattern);
        ple.setContext(lc);
        ple.start();

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        appender.setEncoder(ple);
        appender.setContext(lc);

        Path logFile = dir.resolve("jweb.log");
        try {
            Files.createDirectories(logFile.getParent());
        } catch (IOException e) {
            throw Errors.internalError(e);
        }
        appender.setFile(logFile.toFile().getAbsolutePath());

        if (options.rollingPolicy == LogbackOptions.RollingPolicy.DAILY) {
            TimeBasedRollingPolicy<ILoggingEvent> policy = policy(dir, lc, 7, "jweb.%d{yyyy-MM-dd}.log");
            policy.setParent(appender);
            appender.setRollingPolicy(policy);
            appender.setTriggeringPolicy(policy);
            policy.start();
        } else if (options.rollingPolicy == LogbackOptions.RollingPolicy.HOURLY) {
            TimeBasedRollingPolicy<ILoggingEvent> policy = policy(dir, lc, 24 * 7, "jweb.%d{yyyy-MM-dd-hh}.log");
            policy.setParent(appender);
            appender.setRollingPolicy(policy);
            appender.setTriggeringPolicy(policy);
            policy.start();
        }

        appender.start();
        return appender;
    }

    private TimeBasedRollingPolicy<ILoggingEvent> policy(Path dir, LoggerContext lc, int i, String s) {
        TimeBasedRollingPolicy<ILoggingEvent> policy = new TimeBasedRollingPolicy<>();
        policy.setMaxHistory(i);
        policy.setContext(lc);
        policy.setTotalSizeCap(FileSize.valueOf("1GB"));
        policy.setFileNamePattern(dir.resolve(s).toString());

        DefaultTimeBasedFileNamingAndTriggeringPolicy<ILoggingEvent> timeBasedTriggering = new DefaultTimeBasedFileNamingAndTriggeringPolicy<>();
        timeBasedTriggering.setContext(lc);
        timeBasedTriggering.setTimeBasedRollingPolicy(policy);
        timeBasedTriggering.start();

        policy.setTimeBasedFileNamingAndTriggeringPolicy(timeBasedTriggering);
        return policy;
    }
}
