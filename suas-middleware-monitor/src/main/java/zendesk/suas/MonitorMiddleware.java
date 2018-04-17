package zendesk.suas;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Middleware for logging {@link State} changes to <a href="https://github.com/zendesk/Suas-Monitor">Suas Monitor</a>
 * <br>
 * <p>
 * Create an instance using the default constructor:
 * <br>
 * <pre>
 * Middleware monitor = new MonitorMiddleware(context);
 * </pre>
 *
 * or using the builder for more configuration options:
 * <br>
 * <pre>
 * Middleware logger = new MonitorMiddleware.Builder(context)
 *      .withEnableBonjour(true)
 *      .withEnableAdb(false)
 *      .build()
 * </pre>
 *
 * Make sure the the monitor is the last middleware in the list:
 * <br>
 * <pre>
 * Middleware monitor = new MonitorMiddleware(context)
 *
 * Store store = Suas.createStore(...)
 *       .withMiddleware(middleware1, middleware2, ... middlewareN, monitor)
 *       .builder().
 * </pre>
 */
public class MonitorMiddleware implements Middleware, ConnectionHandler {

    private NetworkSocketServer network;
    private UnixSocketServer unixSocketServer;

    private final Gson gson;
    private final AtomicBoolean started;
    private final BlockingQueue<StateUpdate> data;
    private StateUpdate lastItem = null;

    /**
     * * Create a LoggerMiddleware with default parameters.
     *
     * <p>
     *     ADB and Bonjour are enabled in the default configuration.
     * </p>
     *
     * @param context a context
     */
    public MonitorMiddleware(@NonNull Context context) {
        this(new Builder(context));
    }

    private MonitorMiddleware(final Builder builder) {
        this.gson = new Gson();
        this.data = new LinkedBlockingQueue<>();
        this.started = new AtomicBoolean(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String packageName = builder.context.getPackageName().replace(".", "-");
                    final String name = String.format(Locale.US, "%s - %s", Build.MODEL, packageName);

                    if(builder.enableBonjour) {
                        network = new NetworkSocketServer(name, "_suas-monitor._tcp.", MonitorMiddleware.this);
                        network.start(builder.context);
                    }

                    if(builder.enableAdb) {
                        unixSocketServer = new UnixSocketServer("suas_monitor_" + packageName, MonitorMiddleware.this);
                        unixSocketServer.start();
                    }

                    started.set(true);
                } catch (IOException e) {
                    started.set(false);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onAction(@NonNull Action<?> action, @NonNull GetState state, @NonNull Dispatcher dispatcher, @NonNull Continuation continuation) {
        continuation.next(action);
        final Map<String, Object> newState = state.getState().getState();

        lastItem = new StateUpdate(action.getActionType(), action.getData(), newState);
        if(started.get()) {
            data.add(new StateUpdate(action.getActionType(), action.getData(), newState));
        }
    }

    @Override
    public synchronized void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedOutputStream output = new BufferedOutputStream(outputStream);
        if (lastItem != null) {
            writeToStream(lastItem, output);
        }

        while (true) {
            try {
                final StateUpdate stateUpdate = data.take();
                writeToStream(stateUpdate, output);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void writeToStream(StateUpdate data, BufferedOutputStream outputStream) throws IOException {
        final String dump = gson.toJson(data) + "&&__&&__&&";
        outputStream.write(dump.getBytes("ASCII"));
        outputStream.flush();
    }

    private static class StateUpdate {

        private final String action;
        private final Object actionData;
        private final Map<String, Object> state;

        private StateUpdate(String action, Object actionData, Map<String, Object> state) {
            this.action = action;
            this.actionData = actionData;
            this.state = state;
        }
    }

    /**
     * A fluent API for configuring a {@link MonitorMiddleware}
     */
    public static class Builder {

        private final Context context;
        private boolean enableAdb;
        private boolean enableBonjour;

        public Builder(@NonNull Context context) {
            this.context = context;
            this.enableAdb = true;
            this.enableBonjour = true;
        }

        /**
         * Enable/disable debugging over ADB.
         * @param enableAdb true for enabling ADB, false otherwise.
         */
        @NonNull
        public Builder withEnableAdb(boolean enableAdb) {
            this.enableAdb = enableAdb;
            return this;
        }

        /**
         * Enable/disable debugging over the network/bonjour.
         * @param enableBonjour true for enabling Bonjour, false otherwise.
         */
        @NonNull
        public Builder withEnableBonjour(boolean enableBonjour) {
            this.enableBonjour = enableBonjour;
            return this;
        }

        /**
         * Create an instance of {@link MonitorMiddleware} with all the provided
         * config options.
         */
        @NonNull
        public Middleware build() {
            return new MonitorMiddleware(this);
        }
    }

}
