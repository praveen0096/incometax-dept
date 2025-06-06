package io.mosip.credential.logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PrintLogger {

    private PrintLogger() {
    }

    /**
     * Gets the logger.
     *
     * @param clazz the clazz
     * @return the logger
     */
    public static Logger getLogger(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }
}
