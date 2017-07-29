package zendesk.suas;

import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LoggerMiddleware implements Middleware {

    private static final String LOG_TAG = "Suas-Logger";

    private final LogAppender logger;
    private final Priority logLevel;
    private final Predicate predicate;
    private final TitleFormatter titleFormatter;
    private final Transformer<Action<?>> actionTransformer;
    private final Transformer<State> stateTransformer;
    private final int lineLength;

    private LoggerMiddleware(Builder builder) {
        this.logger = builder.logAppender;
        this.logLevel = builder.logLevel;
        this.predicate = builder.predicate;
        this.titleFormatter = builder.titleFormatter;
        this.actionTransformer = builder.actionTransformer;
        this.stateTransformer = builder.stateTransformer;
        this.lineLength = builder.lineLength;
    }

    public LoggerMiddleware() {
        this(new Builder());
    }

    @Override
    public void onAction(@NonNull Action<?> action, @NonNull GetState state, @NonNull Dispatcher dispatcher, @NonNull Continuation continuation) {
        if(predicate.predicate(state, action)) {
            final Date timestamp = new Date();
            final long start = System.nanoTime();

            final State oldState = state.getState();
            continuation.next(action);
            final State newState = state.getState();

            final float durationInMs = (System.nanoTime() - start) / 1000000f;
            final String title = String.format(Locale.US, "┎───→ %s", titleFormatter.getTitle(action, timestamp, durationInMs));
            final String oldStateString = getSection("┠─ prev state\t► ", "┃\t", stateTransformer.transform(oldState), lineLength);
            final String actionString = getSection("┠─ action\t\t► ", "┃\t\t", actionTransformer.transform(action), lineLength);
            final String newStateString = getSection("┠─ next state\t► ", "┃\t", stateTransformer.transform(newState), lineLength);
            final String lastLine = String.format(Locale.US, "┖─%s", new String(new char[title.length() - 2]).replace("\0", "─"));

            logger.log(logLevel, LOG_TAG, title, null);
            logger.log(logLevel, LOG_TAG, oldStateString, null);
            logger.log(logLevel, LOG_TAG, actionString, null);
            logger.log(logLevel, LOG_TAG, newStateString, null);
            logger.log(logLevel, LOG_TAG, lastLine, null);
        }
    }


    private String getSection(String firstLineStart, String subsequentLineStart, String content, int maxLineLength) {
        final String section;

        if(maxLineLength > 0 && (firstLineStart.length() + content.length()) > maxLineLength) {

            final String firstLine = firstLineStart + content.substring(0, maxLineLength - firstLineStart.length());
            final String restContent = content.substring(maxLineLength - firstLineStart.length());
            final List<String> remainingLines = LoggerHelper.splitLogMessage(restContent, maxLineLength - firstLineStart.length());
            final StringBuilder stringBuilder = new StringBuilder(firstLine);

            for(String s : remainingLines) {
                final int whitespace = firstLineStart.length() - subsequentLineStart.length();
                final String w = new String(new char[whitespace]).replace("\0", " ");
                stringBuilder.append("\n");
                stringBuilder.append(String.format(Locale.US, "%s%s%s", subsequentLineStart, w, s));
            }

            section = stringBuilder.toString();
        } else {
            section = String.format(Locale.US, "%s%s", firstLineStart, content);
        }

        return section;
    }

    public interface Predicate {
        boolean predicate(GetState getState, Action<?> action);
    }

    public interface Transformer<E> {
        String transform(E e);
    }

    public interface TitleFormatter {
        String getTitle(Action<?> action, Date timestamp, float duration);
    }

    public static class Builder {

        private LogAppender logAppender = getPlatformLogger();
        private Predicate predicate = new DefaultPredicate(true);
        private boolean showDuration = true;
        private boolean showTimestamp = true;
        private TitleFormatter titleFormatter = new DefaultTitleFormatter(showTimestamp, showDuration);
        private Priority logLevel = Priority.DEBUG;

        private Serialization serialization = Serialization.GSON;
        private Transformer<Action<?>> actionTransformer = new DefaultTransformer<>(serialization);
        private Transformer<State> stateTransformer = new DefaultTransformer<>(serialization);

        private int lineLength = -1;

        public Builder setLogAppender(LogAppender logAppender) {
            this.logAppender = logAppender;
            return this;
        }

        public Builder setPredicate(Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        public Builder setShowDuration(boolean showDuration) {
            this.showDuration = showDuration;
            this.titleFormatter = new DefaultTitleFormatter(showTimestamp, showDuration);
            return this;
        }

        public Builder setShowTimestamp(boolean showTimestamp) {
            this.showTimestamp = showTimestamp;
            this.titleFormatter = new DefaultTitleFormatter(showTimestamp, showDuration);
            return this;
        }

        public Builder setSerialization(Serialization serialization) {
            this.serialization = serialization;
            this.actionTransformer = new DefaultTransformer<>(serialization);
            this.stateTransformer = new DefaultTransformer<>(serialization);
            return this;
        }

        public Builder setActionTransformer(Transformer<Action<?>> actionTransformer) {
            this.actionTransformer = actionTransformer;
            return this;
        }

        public Builder setStateTransformer(Transformer<State> stateTransformer) {
            this.stateTransformer = stateTransformer;
            return this;
        }

        public Builder setTitleFormatter(TitleFormatter titleFormatter) {
            this.titleFormatter = titleFormatter;
            return this;
        }

        public Builder setLogLevel(Priority logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public Builder setLineLength(int lineLength) {
            this.lineLength = lineLength;
            return this;
        }

        public Middleware build() {
            return new LoggerMiddleware(this);
        }

        private static LogAppender getPlatformLogger() {
            LogAppender logger = null;
            try {
                Class.forName("android.os.Build");
                if (Build.VERSION.SDK_INT != 0) {
                    logger = new Android();
                }

            } catch (Exception ignored) {
                // Intentionally empty
            } finally {
                if (logger == null) {
                    logger = new Java();
                }
            }
            return logger;
        }

    }

    private static class DefaultTransformer<E> implements Transformer<E> {

        private final Serialization serialization;
        private final Gson gson = new Gson();

        private DefaultTransformer(Serialization serialization) {
            this.serialization = serialization;
        }

        @Override
        public String transform(E data) {
            String actionString;
            try {
                if(serialization == Serialization.GSON) {
                    actionString = gson.toJson(data);
                } else {
                    actionString = data.toString();
                }
            } catch (Exception e) {
                actionString = "<Unable to serialize>";
            }
            return actionString;
        }
    }

    private static class DefaultPredicate implements Predicate {

        private final boolean predicate;

        DefaultPredicate(boolean predicate) {
            this.predicate = predicate;
        }

        @Override
        public boolean predicate(GetState getState, Action<?> action) {
            return predicate;
        }
    }

    private static class DefaultTitleFormatter implements TitleFormatter {

        private static final SimpleDateFormat TIME_FORMAT =
                new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

        private final boolean showTimeStamp;
        private final boolean showDuration;

        DefaultTitleFormatter(boolean showTimeStamp, boolean showDuration) {
            this.showTimeStamp = showTimeStamp;
            this.showDuration = showDuration;
        }

        @Override
        public String getTitle(Action<?> action, Date timestamp, float duration) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Action: '");
            stringBuilder.append(action.getActionType());
            stringBuilder.append("' ");

            if(showTimeStamp) {
                stringBuilder.append("@");
                stringBuilder.append(TIME_FORMAT.format(timestamp));
                stringBuilder.append(" ");
            }

            if(showDuration) {
                stringBuilder.append("(in ");
                stringBuilder.append(String.format(Locale.US, "%.02f", duration));
                stringBuilder.append(" ms)");
            }

            return stringBuilder.toString();
        }
    }


    public interface LogAppender {
        void log(Priority priority, String tag, String message, Throwable throwable);
    }

    public enum Serialization {
        GSON,
        TO_STRING
    }

    public enum Priority {
        VERBOSE(2),
        DEBUG(3),
        INFO(4),
        WARN(5),
        ERROR(6);

        private final int priority;

        Priority(int priority) {
            this.priority = priority;
        }
    }

    private static class Android implements LogAppender {

        private static final int MAX_LINE_LENGTH = 4000;

        @Override
        public void log(Priority priority, String tag, String message, Throwable throwable) {
            String androidTag = LoggerHelper.getAndroidTag(tag);

            if (throwable != null) {
                message = message + "\n" + Log.getStackTraceString(throwable);
            }

            final List<String> buffer = LoggerHelper.splitLogMessage(message, MAX_LINE_LENGTH);

            for(String line : buffer){
                Log.println(
                        priority == null ? Priority.INFO.priority : priority.priority,
                        androidTag, line
                );
            }
        }
    }

    private static class Java implements LogAppender {

        private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

        @Override
        public void log(Priority priority, String tag, String message, Throwable throwable) {
            /**
             * Suppressing this warning because it looks unreadable when it is a single string.
             */
            @SuppressWarnings("StringBufferReplaceableByString")
            StringBuilder logBuilder = new StringBuilder(100);

            logBuilder
                    .append("[")
                    .append(new SimpleDateFormat(ISO_8601_FORMAT, Locale.US).format(new Date()))
                    .append("]")
                    .append(" ")
                    .append(priority == null
                            ? LoggerHelper.getLevelFromPriority(Priority.INFO.priority)
                            : LoggerHelper.getLevelFromPriority(priority.priority))
                    .append("/")
                    .append(tag != null && tag.trim().length() > 0 ? tag : "UNKNOWN")
                    .append(": ")
                    .append(message);

            System.out.println(logBuilder.toString());

            if (throwable != null) {
                throwable.printStackTrace(System.out);
            }
        }
    }
}
