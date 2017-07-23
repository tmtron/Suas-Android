package com.zendesk.suas;


import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

class UnixSocketServer {

    private static final String LOG_TAG = "Suas-Adb";

    private final LocalServerSocket localServerSocket;
    private final ConnectionHandler connectionHandler;

    UnixSocketServer(String address, ConnectionHandler connectionHandler) throws IOException {
        this.localServerSocket = getSocket(address);
        this.connectionHandler = connectionHandler;
    }

    void start() {
        final ServerThread serverThread = new ServerThread(localServerSocket, connectionHandler);
        serverThread.setName("Suas ADB Server");
        serverThread.start();
    }

    private LocalServerSocket getSocket(String address) throws IOException {
        return new LocalServerSocket(address);
    }

    private static class ServerThread extends Thread {

        private final LocalServerSocket localServerSocket;
        private final ConnectionHandler socketHandler;
        private final AtomicInteger threadId;

        private ServerThread(LocalServerSocket localServerSocket, ConnectionHandler socketHandler) {
            this.localServerSocket = localServerSocket;
            this.socketHandler = socketHandler;
            this.threadId = new AtomicInteger(0);
        }

        @Override
        public void run() {
            while(true) {
                try {
                    final LocalSocket socket = localServerSocket.accept();

                    final WorkerThread workerThread = new WorkerThread(socket, socketHandler);
                    workerThread.setName("SuasUnixSocket " + threadId.incrementAndGet());
                    workerThread.setDaemon(true);
                    workerThread.start();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Server thread error", e);
                    e.printStackTrace();
                    break;
                }
            }
        }
    }


    private static class WorkerThread extends Thread {

        private final LocalSocket socket;
        private final ConnectionHandler socketHandler;

        WorkerThread(LocalSocket socket, ConnectionHandler socketHandler) {
            this.socket = socket;
            this.socketHandler = socketHandler;
        }

        @Override
        public void run() {
            try {
                socketHandler.handle(socket.getInputStream(), socket.getOutputStream());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Worker thread error", e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.w(LOG_TAG, "Unable to close socket");
                }
            }
        }
    }

}
