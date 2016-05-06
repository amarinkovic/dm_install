package pro.documentum.util.logger;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public final class Logger {

    public static final String LOG2STREAM_PROPERTY = Logger.class.getName()
            + ".log2sysout";

    private static final Boolean LOG_2_STREAM_VALUE;

    private static final String CLASS = Logger.class.getName();

    static {
        String param = System.getenv(LOG2STREAM_PROPERTY);
        if (param == null) {
            param = System.getProperty(LOG2STREAM_PROPERTY);
        }
        LOG_2_STREAM_VALUE = param != null
                && "true".equalsIgnoreCase(param.trim());
    }

    private Logger() {
        super();
    }

    public static boolean isEnabled(final LogLevel level, final Object source) {
        return level.isEnabled(source, LOG_2_STREAM_VALUE);
    }

    public static boolean isDebugEnabled() {
        return LogLevel.DEBUG.isEnabled(getSource(), LOG_2_STREAM_VALUE);
    }

    public static boolean isDebugEnabled(final Object source) {
        return LogLevel.DEBUG.isEnabled(source, LOG_2_STREAM_VALUE);
    }

    public static void log(final LogLevel level, final Object source,
            final String message) {
        level.log(source, message, null, null, LOG_2_STREAM_VALUE);
    }

    public static void log(final LogLevel level, final Object source,
            final String message, final Throwable throwable) {
        level.log(source, message, null, throwable, LOG_2_STREAM_VALUE);
    }

    public static void log(final LogLevel level, final Object source,
            final String message, final Object... params) {
        level.log(source, message, params, null, LOG_2_STREAM_VALUE);
    }

    public static void log(final LogLevel level, final Object source,
            final String message, final Throwable throwable,
            final Object... params) {
        level.log(source, message, params, throwable, LOG_2_STREAM_VALUE);
    }

    public static void error(final String message, final Throwable throwable,
            final Object... params) {
        log(LogLevel.ERROR, getSource(), message, throwable, params);
    }

    public static void error(final String message, final Throwable throwable) {
        log(LogLevel.ERROR, getSource(), message, throwable);
    }

    public static void error(final String message, final Object... params) {
        log(LogLevel.ERROR, getSource(), message, params);
    }

    public static void error(final String message) {
        log(LogLevel.ERROR, getSource(), message);
    }

    public static void error(final Throwable throwable) {
        log(LogLevel.ERROR, getSource(), null, throwable);
    }

    public static void warn(final String message, final Throwable throwable,
            final Object... params) {
        log(LogLevel.WARN, getSource(), message, throwable, params);
    }

    public static void warn(final String message, final Throwable throwable) {
        log(LogLevel.WARN, getSource(), message, throwable);
    }

    public static void warn(final String message, final Object... params) {
        log(LogLevel.WARN, getSource(), message, params);
    }

    public static void warn(final String message) {
        log(LogLevel.WARN, getSource(), message);
    }

    public static void warn(final Throwable throwable) {
        log(LogLevel.WARN, getSource(), null, throwable);
    }

    public static void info(final String message, final Throwable throwable,
            final Object... params) {
        log(LogLevel.INFO, getSource(), message, throwable, params);
    }

    public static void info(final String message, final Throwable throwable) {
        log(LogLevel.INFO, getSource(), message, throwable);
    }

    public static void info(final String message, final Object... params) {
        log(LogLevel.INFO, getSource(), message, params);
    }

    public static void info(final String message) {
        log(LogLevel.INFO, getSource(), message);
    }

    public static void info(final Throwable throwable) {
        log(LogLevel.INFO, getSource(), null, throwable);
    }

    public static void debug(final String message, final Throwable throwable,
            final Object... params) {
        log(LogLevel.DEBUG, getSource(), message, throwable, params);
    }

    public static void debug(final String message, final Throwable throwable) {
        log(LogLevel.DEBUG, getSource(), message, throwable);
    }

    public static void debug(final String message, final Object... params) {
        log(LogLevel.DEBUG, getSource(), message, params);
    }

    public static void debug(final String message) {
        log(LogLevel.DEBUG, getSource(), message);
    }

    public static void debug(final Throwable throwable) {
        log(LogLevel.DEBUG, getSource(), null, throwable);
    }

    public static void trace(final String message, final Throwable throwable,
            final Object... params) {
        log(LogLevel.TRACE, getSource(), message, throwable, params);
    }

    public static void trace(final String message, final Throwable throwable) {
        log(LogLevel.TRACE, getSource(), message, throwable);
    }

    public static void trace(final String message, final Object... params) {
        log(LogLevel.TRACE, getSource(), message, params);
    }

    public static void trace(final String message) {
        log(LogLevel.TRACE, getSource(), message);
    }

    public static void trace(final Throwable throwable) {
        log(LogLevel.TRACE, getSource(), null, throwable);
    }

    private static String getSource() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 1, n = elements.length; i < n; i++) {
            if (CLASS.equals(elements[i].getClassName())) {
                continue;
            }
            return String.format("%s.%s", elements[i].getClassName(),
                    elements[i].getMethodName());
        }
        return null;
    }

}
