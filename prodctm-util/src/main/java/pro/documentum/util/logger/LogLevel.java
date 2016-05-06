package pro.documentum.util.logger;

import java.io.PrintStream;
import java.text.MessageFormat;

import org.apache.log4j.Level;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public enum LogLevel {

    TRACE {

        @Override
        boolean isEnabled(final Object source, final boolean log2Stream) {
            return getLogger(source).isTraceEnabled() || log2Stream;
        }

        @Override
        protected void doLog(final Object source, final String message,
                final Object[] params, final Throwable throwable,
                final boolean log2Stream) {
            getLogger(source).trace(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    DEBUG {

        @Override
        boolean isEnabled(final Object source, final boolean log2Stream) {
            return getLogger(source).isDebugEnabled() || log2Stream;
        }

        @Override
        protected void doLog(final Object source, final String message,
                final Object[] params, final Throwable throwable,
                final boolean log2Stream) {
            getLogger(source).debug(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    INFO {

        @Override
        boolean isEnabled(final Object source, final boolean log2Stream) {
            return getLogger(source).isInfoEnabled() || log2Stream;
        }

        @Override
        protected void doLog(final Object source, final String message,
                final Object[] params, final Throwable throwable,
                final boolean log2Stream) {
            getLogger(source).info(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    WARN {

        @Override
        boolean isEnabled(final Object source, final boolean log2Stream) {
            return getLogger(source).isEnabledFor(Level.WARN) || log2Stream;
        }

        @Override
        protected void doLog(final Object source, final String message,
                final Object[] params, final Throwable throwable,
                final boolean log2Stream) {
            getLogger(source).warn(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Sysout(source, this, message, params, throwable);
            }
        }

    },

    ERROR {

        @Override
        boolean isEnabled(final Object source, final boolean log2Stream) {
            return getLogger(source).isEnabledFor(Level.ERROR) || log2Stream;
        }

        @Override
        protected void doLog(final Object source, final String message,
                final Object[] params, final Throwable throwable,
                final boolean log2Stream) {
            getLogger(source).error(getMessage(message, params), throwable);
            if (log2Stream) {
                log2Syserr(source, this, message, params, throwable);
            }
        }

    };

    abstract boolean isEnabled(final Object source, final boolean log2Stream);

    protected abstract void doLog(final Object source, final String message,
            Object[] params, Throwable throwable, boolean log2Stream);

    void log(final Object source, final String message, final Object[] params,
            final Throwable throwable, final boolean log2Stream) {
        try {
            if (isEnabled(source, log2Stream)) {
                doLog(source, nullToBlank(message), params, throwable,
                        log2Stream);
            }
        } catch (Exception | Error ex) {
            log2Syserr(this, ERROR, ex.getMessage(), null, ex);
        }
    }

    private static void log2Sysout(final Object source, final LogLevel level,
            final String message, final Object[] params,
            final Throwable throwable) {
        log2Stream(source, level, message, params, throwable, System.out);
    }

    private static void log2Syserr(final Object source, final LogLevel level,
            final String message, final Object[] params,
            final Throwable throwable) {
        log2Stream(source, level, message, params, throwable, System.err);
    }

    private static void log2Stream(final Object source, final LogLevel level,
            final String message, final Object[] params,
            final Throwable throwable, final PrintStream stream) {
        String format = "%-60s: ";
        if (source == null) {
            stream.print(String.format(format, "NULL"));
        } else if (source instanceof String) {
            stream.print(String.format(format, (String) source));
        } else if (source instanceof Class) {
            stream.print(String.format(format, ((Class<?>) source).getName()));
        } else {
            stream.print(String.format(format, source.getClass().getName()));
        }
        stream.print(String.format("%5s: ", level));
        stream.println(getMessage(message, params));
        if (throwable != null) {
            throwable.printStackTrace(stream);
        }
    }

    public static String nullToBlank(final String str) {
        if (str != null) {
            return str;
        }
        return "";
    }

    private static org.apache.log4j.Logger getLogger(final Object source) {
        if (source instanceof String) {
            return org.apache.log4j.Logger.getLogger((String) source);
        }
        if (source instanceof Class) {
            return org.apache.log4j.Logger.getLogger((Class) source);
        }
        return org.apache.log4j.Logger.getLogger(source.getClass());
    }

    public static String getMessage(final String message, final Object[] params) {
        if (message != null && params != null) {
            return MessageFormat.format(message, params);
        }
        return message;
    }

}
