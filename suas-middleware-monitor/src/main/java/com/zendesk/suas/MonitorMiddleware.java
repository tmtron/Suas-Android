package com.zendesk.suas;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MonitorMiddleware implements Middleware, ConnectionHandler {

    private NetworkSocketServer network;
    private UnixSocketServer unixSocketServer;

    private final Gson gson;
    private final AtomicBoolean started;
    private final BlockingQueue<StateUpdate> data;
    private StateUpdate lastItem = null;

    public MonitorMiddleware(final Context context) {
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
                        network = new NetworkSocketServer(name, "_redux-monitor._tcp.", MonitorMiddleware.this);
                        network.start(builder.context);
                    }

                    if(builder.enableAdb) {
                        unixSocketServer = new UnixSocketServer("redux_monitor_" + packageName, MonitorMiddleware.this);
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
        final State newState = state.getState();

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
        private final State state;

        private StateUpdate(String action, Object actionData, State state) {
            this.action = action;
            this.actionData = actionData;
            this.state = state;
        }
    }

    public static class Builder {

        private final Context context;
        private boolean enableAdb;
        private boolean enableBonjour;

        public Builder(Context context) {
            this.context = context;
            this.enableAdb = true;
            this.enableBonjour = true;
        }

        public Builder setEnableAdb(boolean enableAdb) {
            this.enableAdb = enableAdb;
            return this;
        }

        public Builder setEnableBonjour(boolean enableBonjour) {
            this.enableBonjour = enableBonjour;
            return this;
        }

        public Middleware build() {
            return new MonitorMiddleware(this);
        }
    }

}
