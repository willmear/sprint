package com.willmear.sprint.support.test;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public final class ScopedLogLevel implements AutoCloseable {

    private final Logger logger;
    private final Level previousLevel;

    private ScopedLogLevel(Class<?> loggerType, Level level) {
        this.logger = (Logger) LoggerFactory.getLogger(loggerType);
        this.previousLevel = logger.getLevel();
        logger.setLevel(level);
    }

    public static ScopedLogLevel off(Class<?> loggerType) {
        return new ScopedLogLevel(loggerType, Level.OFF);
    }

    @Override
    public void close() {
        logger.setLevel(previousLevel);
    }
}
