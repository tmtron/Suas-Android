package zendesk.suas;

import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/**
 * Middleware for logging {@link State} changes.
 * <br>
 * <p>
 * Create an instance using the default constructor:
 * <br>
 * <pre>
 * Middleware logger new LoggerMiddleware();
 * </pre>
 *
 * or using the builder for more configuration options:
 * <br>
 * <pre>
 * Middleware logger = new LoggerMiddleware.Builder()
 *      .withTitleFormatter(...)
 *      .withSerialization(...)
 *      ...
 *      .build()
 * </pre>
 *
 * Make sure the the logger is the last middleware in the list:
 * <br>
 * <pre>
 * Middleware logger = new LoggerMiddleware()
 *
 * Store store = Suas.createStore(...)
 *       .widthMiddleware(middleware1, middleware2, ... middlewareN, logger)
 *       .builder().
 * </pre>
 *
 */
public class LoggerMiddleware implements Middleware {

    private static final String LOG_TAG = "Suas-Logger";

    private final LogAppender logger;
    private final Predicate predicate;
    private final TitleFormatter titleFormatter;
    private final Transformer<Action<?>> actionTransformer;
    private final Transformer<State> stateTransformer;
    private final int lineLength;

    private LoggerMiddleware(Builder builder) {
        this.logger = builder.logAppender;
        this.predicate = builder.predicate;
        this.titleFormatter = builder.titleFormatter;
        this.actionTransformer = builder.actionTransformer;
        this.stateTransformer = builder.stateTransformer;
        this.lineLength = builder.lineLength;
    }

    /**
     * Create a LoggerMiddleware with sane default parameters.
     */
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

            logger.log("", title);
            logger.log(LOG_TAG, oldStateString);
            logger.log(LOG_TAG, actionString);
            logger.log(LOG_TAG, newStateString);
            logger.log(LOG_TAG, lastLine);
        }
    }


    private static String getSection(String firstLineStart, String subsequentLineStart, String content, int maxLineLength) {
        final String section;

        if(maxLineLength > 0 && (firstLineStart.length() + content.length()) > maxLineLength) {

            final String firstLine = firstLineStart + content.substring(0, maxLineLength - firstLineStart.length());
            final String restContent = content.substring(maxLineLength - firstLineStart.length());
            final List<String> remainingLines = splitLogMessage(restContent, maxLineLength - firstLineStart.length());
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

    private static List<String> splitLogMessage(String message, int maxLength){
        final List<String> messages = new ArrayList<>();

        if(maxLength < 1){

            if(!(message != null && message.length() > 0)){
                messages.add("");
                return messages;
            }

            messages.add(message);
            return messages;
        }

        if(message == null || message.trim().length() == 0){
            messages.add("");
            return messages;
        }

        if(message.length() < maxLength){
            messages.add(message);
            return messages;
        }

        for(int i = 0, len = message.length(); i < len; i++){

            final int indexOfSeparator = message.indexOf("\n", i);
            final int newLine = (indexOfSeparator != -1) ? indexOfSeparator : len;

            do {
                final int end = Math.min(newLine, i + maxLength);
                messages.add(message.substring(i, end));
                i = end;
            } while(i < newLine);
        }

        return messages;
    }

    /**
     * Callback that decides if the logger should print or not.
     */
    public interface Predicate {
        /**
         * Callback that decides if the logger should print or not.
         *
         * @param getState the current state
         * @param action the current action
         * @return {@code true} print, {@code false} don't print
         */
        boolean predicate(@NonNull GetState getState, @NonNull Action<?> action);
    }

    /**
     * Callback that allows converting an object of type {@code <E>} to a {@link String} before printing
     */
    public interface Transformer<E> {
        /**
         * Callback that allows converting an object of type {@code <E>} to a {@link String} before printing
         *
         * @param e date to transform into a string
         */
        @NonNull
        String transform(@NonNull E e);
    }

    /**
     * Callback for creating the title line
     */
    public interface TitleFormatter {

        /**
         * Generate a title string for the logger
         *
         * @param action the current action
         * @param timestamp the current timestamp
         * @param duration the duration the action needed to process
         * @return the title string
         */
        @NonNull
        String getTitle(@NonNull Action<?> action, @NonNull Date timestamp, @NonNull float duration);
    }

    /**
     * Callback for printing logs
     */
    public interface LogAppender {
        /**
         * Callback for printing a single log message
         *
         * @param tag a log tag
         * @param message a log message
         */
        void log(@NonNull String tag, @NonNull String message);
    }

    /**
     * Serialization Type
     * <p>
     *     Only works in combination with the {@code DefaultTransformer}
     * </p>
     */
    public enum Serialization {
        /**
         * Use {@link Gson} to transform an object to a {@link String}
         */
        GSON,

        /**
         * Use {@link Object#toString()} to transform an object to a {@link String}
         */
        TO_STRING
    }

    /**
     * Fluent API for configuring a logger
     */
    public static class Builder {

        private LogAppender logAppender = new SuasLogAppender();
        private Predicate predicate = new DefaultPredicate(true);
        private boolean showDuration = true;
        private boolean showTimestamp = true;
        private TitleFormatter titleFormatter = new DefaultTitleFormatter(showTimestamp, showDuration);

        private Serialization serialization = Serialization.GSON;
        private Transformer<Action<?>> actionTransformer = new DefaultTransformer<>(serialization);
        private Transformer<State> stateTransformer = new DefaultTransformer<>(serialization);

        private int lineLength = -1;

        /**
         *
         */
        public Builder() {
            // intentionally empty
        }

        /**
         * Define a custom {@link LogAppender}
         * <p>
         *     Default: When running on Android it will use {@code Log} and on java {@code System.out}
         * </p>
         */
        @NonNull
        public Builder withLogAppender(@NonNull LogAppender logAppender) {
            this.logAppender = logAppender;
            return this;
        }

        /**
         * Provide a custom {@link Predicate}
         * <p>
         *     Default: Always print
         * </p>
         */
        @NonNull
        public Builder withPredicate(@NonNull Predicate predicate) {
            this.predicate = predicate;
            return this;
        }

        /**
         * Enable/disable the duration in the title line.
         * <p>
         *     Default: {@code true}
         *     <br>
         *     Only working with the default {@link TitleFormatter}
         * </p>
         */
        @NonNull
        public Builder withShowDuration(boolean showDuration) {
            this.showDuration = showDuration;
            this.titleFormatter = new DefaultTitleFormatter(showTimestamp, showDuration);
            return this;
        }

        /**
         * Enable/disable the timestamp in the title line.
         * <p>
         *     Default: {@code true}
         *     <br>
         *     Only working with the default {@link TitleFormatter}
         * </p>
         */
        @NonNull
        public Builder withShowTimestamp(boolean showTimestamp) {
            this.showTimestamp = showTimestamp;
            this.titleFormatter = new DefaultTitleFormatter(showTimestamp, showDuration);
            return this;
        }

        /**
         * Choose your preferred serialization method.
         * <p>
         *     Default: {@link Serialization#GSON}
         *     <br>
         *     Only work in combination with the the default transformer
         * </p>
         */
        @NonNull
        public Builder withSerialization(@NonNull Serialization serialization) {
            this.serialization = serialization;
            this.actionTransformer = new DefaultTransformer<>(serialization);
            this.stateTransformer = new DefaultTransformer<>(serialization);
            return this;
        }

        /**
         * Provide a custom {@link Transformer} for {@link Action}.
         */
        @NonNull
        public Builder withActionTransformer(@NonNull Transformer<Action<?>> actionTransformer) {
            this.actionTransformer = actionTransformer;
            return this;
        }

        /**
         * Provide a custom {@link Transformer} for {@link State}.
         */
        @NonNull
        public Builder withStateTransformer(@NonNull Transformer<State> stateTransformer) {
            this.stateTransformer = stateTransformer;
            return this;
        }

        /**
         * Provide a custom {@link TitleFormatter}.
         */
        @NonNull
        public Builder withTitleFormatter(@NonNull TitleFormatter titleFormatter) {
            this.titleFormatter = titleFormatter;
            return this;
        }

        /**
         * Limit the line length.
         * <p>
         *     Default: {@code -1}
         *     <br>
         *     {@code -1} ... no max line length
         * </p>
         */
        @NonNull
        public Builder withLineLength(int lineLength) {
            this.lineLength = lineLength;
            return this;
        }

        /**
         * Create an instance with all provided options.
         */
        @NonNull
        public Middleware build() {
            return new LoggerMiddleware(this);
        }

    }

    private static class DefaultTransformer<E> implements Transformer<E> {

        private final Serialization serialization;
        private final Gson gson = new Gson();

        private DefaultTransformer(Serialization serialization) {
            this.serialization = serialization;
        }

        @NonNull
        @Override
        public String transform(@NonNull E data) {
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
        public boolean predicate(@NonNull GetState getState, @NonNull Action<?> action) {
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

        @NonNull
        @Override
        public String getTitle(@NonNull Action<?> action, @NonNull Date timestamp, @NonNull float duration) {
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


    private static class SuasLogAppender implements LogAppender {

        private static final int MAX_LINE_LENGTH = 4000;
        private static final String LOG_TAG = "Suas-Logger";
        private static final Logger LOGGER = Logger.getLogger(LOG_TAG);

        static {
            try {
                Class.forName("android.os.Build");
            } catch (Exception ignored) {
                final Handler consoleHandler = new StreamHandler(System.out, new Formatter() {
                    @Override
                    public String format(LogRecord logRecord) {
                        return String.format(Locale.US, "[%s] %s\n", logRecord.getLoggerName(), logRecord.getMessage());
                    }
                });
                consoleHandler.setLevel(Level.INFO);
                LOGGER.setUseParentHandlers(false);
                LOGGER.addHandler(consoleHandler);
            }
        }

        @Override
        public void log(@NonNull String tag, @NonNull String message) {

            final List<String> buffer = splitLogMessage(message, MAX_LINE_LENGTH);

            for(String line : buffer){
                LOGGER.log(Level.INFO, line);
            }
        }
    }
}
