package it.sephiroth.android.library.simplelogger;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;

/**
 * Utility class for logging
 *
 * @author alessandro crugnola (alessandro.crugnola@gmail.com)
 */
public final class LoggerFactory {
    private LoggerFactory() { }

    /**
     * turning LOG_ENABLED to false will prevent any debug event with the
     * exception of error logs
     */
    public static final boolean LOG_ENABLED = true;
    private static final NullLogger NULL_LOGGER = new NullLogger("null");

    public enum LoggerType {
        Console,
        Null
    }

    public interface Logger {

        /**
         * Set the minimum log level (ie: Log.VERBOSE)
         *
         * @param level from 2 to 7
         */
        void setLevel(int level);

        /**
         * Returns the current log level
         *
         * @return int
         */
        int getLevel();

        /**
         * gets the current log tag
         */
        String getTag();

        /**
         * change the log tag
         *
         * @param tag tag
         */
        void setTag(@NonNull final String tag);

        void debug(String message);

        void info(String message);

        void warn(String message);

        void error(String message);

        void verbose(String message);

        void log(Exception e);

        /**
         * Log using java format specifications. For more informations on how
         * to format messages, see <a href='http://developer.android.com/reference/java/util/Formatter.html'>Formatter</a>
         *
         * @see Formatter
         */
        void debug(String format, Object... args);

        void info(String format, Object... args);

        void warn(String format, Object... args);

        void error(String format, Object... args);

        void verbose(String s, Object... args);
    }

    abstract static class BaseLogger implements Logger {
        String tag;
        int level;

        BaseLogger(String basetag) {
            tag = basetag;
            level = Log.VERBOSE;
        }

        @Override
        public void setLevel(final int level) {
            this.level = level;
        }

        @Override
        public int getLevel() {
            return level;
        }

        public String getTag() {
            return tag;
        }

        @Override
        public void setTag(final String tag) {
            this.tag = tag;
        }

        protected StringBuilder formatArguments(Object... args) {
            StringBuilder b = new StringBuilder();
            for (Object obj : args) {
                b.append(obj + ", ");
            }
            return b;
        }
    }

    static class NullLogger extends BaseLogger {
        NullLogger(String basetag) {
            super(basetag);
        }

        @Override
        public void debug(String message) { /* do nothing */ }

        @Override
        public void info(String message) { /* do nothing */ }

        @Override
        public void warn(String message) { /* do nothing */ }

        @Override
        public void error(String message) { /* do nothing */ }

        public void verbose(String message) { /* do nothing */ }

        @Override
        public void log(final Exception e) { /* do nothing */ }

        @Override
        public void verbose(String format, Object... args) { /* do nothing */ }

        @Override
        public void debug(String format, Object... args) { /* do nothing */ }

        @Override
        public void info(String format, Object... args) { /* do nothing */ }

        @Override
        public void warn(String format, Object... args) { /* do nothing */ }

        @Override
        public void error(String format, Object... args) { /* do nothing */ }
    }

    public static final class FileLogger extends BaseLogger {

        private final String filePath;
        private FileWriter logWriter;

        private FileLogger(final String fileName) {
            super("");
            filePath = Environment.getExternalStorageDirectory() + "/" + fileName;

            FileWriter writer;
            try {
                writer = new FileWriter(filePath, true);
            } catch (IOException e) {
                writer = null;
            }
            logWriter = writer;
        }

        public synchronized void clear() {
            if (!LOG_ENABLED) {
                return;
            }
            if (TextUtils.isEmpty(filePath)) {
                return;
            }
            FileChannel outChan = null;
            try {
                outChan = new FileOutputStream(filePath, true).getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                outChan.truncate(0);
                outChan.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public synchronized void close() {
            if (logWriter != null) {
                try {
                    logWriter.close();
                } catch (IOException e) {
                    // Doesn't matter
                }
                logWriter = null;
            }
        }

        private synchronized void writeLog(int level, String logLevel, String str) {
            if (LOG_ENABLED && getLevel() <= level && null != logWriter) {
                writeLogSync(logLevel, str);
            }
        }

        void writeLogSync(String logLevel, String str) {
            Date d = new Date();
            int hr = d.getHours();
            int min = d.getMinutes();
            int sec = d.getSeconds();
            StringBuffer sb = new StringBuffer(256);
            sb.append('[');
            sb.append(hr);
            sb.append(':');
            if (min < 10) {
                sb.append('0');
            }
            sb.append(min);
            sb.append(':');
            if (sec < 10) {
                sb.append('0');
            }
            sb.append(sec);
            sb.append("|");
            sb.append(logLevel);
            sb.append("] ");
            sb.append(str);
            sb.append("\r\n");
            String s = sb.toString();
            if (logWriter != null) {
                try {
                    logWriter.write(s);
                    logWriter.flush();
                } catch (IOException e) {
                    if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                        if (logWriter != null) {
                            try {
                                writeLogSync(logLevel, "Exception writing log; recreating...");
                                writeLogSync(logLevel, str);
                            } catch (Exception e1) {
                                // Nothing to do at this point
                            }
                        }
                    }
                }
            }
        }

        public synchronized void log(Exception e) {
            if (logWriter != null) {
                writeLog(Log.ERROR, "EXCEPTION", "Stack trace follows...");
                PrintWriter pw = new PrintWriter(logWriter);
                e.printStackTrace(pw);
                pw.flush();
            }
        }

        @Override
        public void debug(final String message) {
            writeLog(Log.DEBUG, "DEBUG", message);
        }

        @Override
        public void info(final String message) {
            writeLog(Log.INFO, "INFO", message);
        }

        @Override
        public void warn(final String message) {
            writeLog(Log.WARN, "WARN", message);
        }

        @Override
        public void error(final String message) {
            writeLog(Log.ERROR, "ERROR", message);
        }

        @Override
        public void verbose(final String message) {
            writeLog(Log.VERBOSE, "VERBOSE", message);
        }

        @Override
        public void debug(final String format, final Object... args) {
            debug(String.format(format, args));
        }

        @Override
        public void info(final String format, final Object... args) {
            info(String.format(format, args));
        }

        @Override
        public void warn(final String format, final Object... args) {
            warn(String.format(format, args));
        }

        @Override
        public void error(final String format, final Object... args) {
            error(String.format(format, args));
        }

        @Override
        public void verbose(final String s, final Object... args) {
            verbose(String.format(s, args));
        }
    }

    static class ConsoleLogger extends BaseLogger {
        ConsoleLogger(String basetag) {
            super(basetag);
        }

        private void printMessage(int level, String message) {
            if (getLevel() <= level) {
                switch (level) {
                    case Log.VERBOSE:
                        Log.v(tag, message);
                        break;
                    case Log.DEBUG:
                        Log.d(tag, message);
                        break;
                    case Log.INFO:
                        Log.i(tag, message);
                        break;
                    case Log.WARN:
                        Log.w(tag, message);
                        break;
                    case Log.ERROR:
                        Log.e(tag, message);
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void debug(String message) {
            printMessage(Log.DEBUG, message);
        }

        @Override
        public void info(String message) {
            printMessage(Log.INFO, message);
        }

        @Override
        public void warn(String message) {
            printMessage(Log.WARN, message);
        }

        @Override
        public void error(String message) {
            printMessage(Log.ERROR, message);
        }

        @Override
        public void verbose(String message) {
            printMessage(Log.VERBOSE, message);
        }

        @Override
        public void log(final Exception e) {
            printMessage(Log.ERROR, Log.getStackTraceString(e));
        }

        @Override
        public void verbose(String format, Object... args) {
            verbose(String.format(format, args));
        }

        @Override
        public void debug(String format, Object... args) {
            debug(String.format(format, args));
        }

        @Override
        public void info(String format, Object... args) {
            info(String.format(format, args));
        }

        @Override
        public void warn(String format, Object... args) {
            warn(String.format(format, args));
        }

        @Override
        public void error(String format, Object... args) {
            error(String.format(format, args));
        }
    }

    /**
     * @param basetag tag prefix
     * @param type    logger type. One of {@link LoggerType}
     * @return
     */
    public static Logger getLogger(String basetag, LoggerType type) {
        if (!LOG_ENABLED) {
            return NULL_LOGGER;
        }

        switch (type) {
            case Console:
                return new ConsoleLogger(basetag);
            case Null:
            default:
                return NULL_LOGGER;
        }
    }

    /**
     * Returns the default Console Logger
     *
     * @param basetag log tag
     * @return new logger
     */
    public static Logger getLogger(String basetag) {
        return getLogger(basetag, LoggerType.Console);
    }

    static final HashMap<String, WeakReference<FileLogger>> FILE_LOGGER_MAP = new HashMap<>();

    public static synchronized FileLogger getFileLogger(@NonNull String fileName) {
        final WeakReference<FileLogger> logger = FILE_LOGGER_MAP.get(fileName);
        if (null != logger) {
            FileLogger fileLogger = logger.get();
            if (null != fileLogger) {
                return fileLogger;
            }
        }
        FileLogger fileLogger = new FileLogger(fileName);
        FILE_LOGGER_MAP.put(fileName, new WeakReference<>(fileLogger));
        return fileLogger;
    }
}
