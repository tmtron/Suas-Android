package com.zendesk.suas;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zendesk.suas.monitor.ConnectionHandler;
import com.zendesk.suas.monitor.NetworkSocketServer;
import com.zendesk.suas.monitor.UnixSocketServer;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MonitorMiddleware implements Middleware, ConnectionHandler {

    private NetworkSocketServer network;
    private UnixSocketServer unixSocketServer;

    private final Gson gson;
    private final AtomicBoolean started;
    private final List<StateUpdate> data;
    private StateUpdate lastItem = null;


    public MonitorMiddleware(final Context context) {
        this.gson = new Gson();
        this.data = new ArrayList<>();
        this.started = new AtomicBoolean(false);

        final String name = String.format(Locale.US, "%s - %s", Build.MODEL, context.getPackageName().replace(".", "-"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    network = new NetworkSocketServer(name, "_redux-monitor._tcp.", MonitorMiddleware.this);
                    unixSocketServer = new UnixSocketServer("redux_monitor_" + context.getPackageName().replace(".", "-"), MonitorMiddleware.this);
                    network.start(context);
                    unixSocketServer.start();
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
            data.add(lastItem);
        }
    }

    @Override
    public synchronized void handle(InputStream inputStream, OutputStream outputStream) throws IOException {
        BufferedOutputStream output = new BufferedOutputStream(outputStream);
        if (lastItem != null) {
            writeToStream(lastItem, output);
        }

        while (true) {
            if (!data.isEmpty()) {
                final StateUpdate remove = data.remove(0);
                writeToStream(remove, output);
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

}
