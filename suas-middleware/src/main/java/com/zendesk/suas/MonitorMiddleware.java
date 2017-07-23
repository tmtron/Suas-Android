package com.zendesk.suas;

import android.content.Context;
import android.net.LocalSocket;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.zendesk.suas.monitor.LocalSocketServer;
import com.zendesk.suas.monitor.ProcessUtil;
import com.zendesk.suas.monitor.SocketHandler;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class MonitorMiddleware implements Middleware, SocketHandler {

    private final LocalSocketServer localServerSocket;
    private final Gson gson = new Gson();

    private final List<StateUpdate> data = new ArrayList<>();
    private StateUpdate lastItem = null;

    public MonitorMiddleware(Context context) {
        localServerSocket = new LocalSocketServer("main", "redux_monitor_" + ProcessUtil.getProcessName(), this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    localServerSocket.run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        try {
            initBonjour(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBonjour(Context context) throws IOException {
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName(ProcessUtil.getProcessName());
        serviceInfo.setServiceType("_redux-monitor._tcp.");

        ServerSocket mServerSocket = new ServerSocket(0);
        serviceInfo.setPort(mServerSocket.getLocalPort());

            // Store the chosen port.
        final NsdManager systemService = (NsdManager)context.getSystemService(Context.NSD_SERVICE);
        systemService.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {

            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {

            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

            }
        });



    }

    @Override
    public void onAction(@NonNull Action<?> action, @NonNull GetState state, @NonNull Dispatcher dispatcher, @NonNull Continuation continuation) {
        continuation.next(action);
        final State newState = state.getState();

        lastItem = new StateUpdate(action.getActionType(), action.getData(), newState);
        data.add(lastItem);
    }

    @Override
    public void onAccepted(LocalSocket socket) throws IOException {
        BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
        if(lastItem != null) {
            writeToStream(lastItem, output);
        }

        while(true) {
            if(!data.isEmpty()) {
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
