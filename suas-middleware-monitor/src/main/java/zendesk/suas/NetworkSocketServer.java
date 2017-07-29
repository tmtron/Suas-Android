package zendesk.suas;


import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;


class NetworkSocketServer implements NsdManager.RegistrationListener{

    private static final String LOG_TAG = "Suas-Bonjour";

    private final NsdServiceInfo serviceInfo;
    private final ServerSocket serverSocket;
    private final ConnectionHandler socketHandler;

    NetworkSocketServer(String name, String type, ConnectionHandler socketHandler) throws IOException {
        this.serverSocket = getServerSocket();
        this.serviceInfo = getServiceInfo(name, type, serverSocket.getLocalPort());
        this.socketHandler = socketHandler;
    }

    private NsdServiceInfo getServiceInfo(String name, String type, int localPort) {
        final NsdServiceInfo serviceInfo = new NsdServiceInfo();
        serviceInfo.setServiceName(name);
        serviceInfo.setServiceType(type);
        serviceInfo.setPort(localPort);
        return serviceInfo;
    }

    private ServerSocket getServerSocket() throws IOException {
        return new ServerSocket(0);
    }

    void start(Context context) {
        final NsdManager manager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
        if(manager != null) {
            manager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, this);
        } else {
            Log.e(LOG_TAG, "NsdManager is null");
        }
    }

    @Override
    public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(LOG_TAG, "Unable to register network service");
    }

    @Override
    public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
        Log.e(LOG_TAG, "Unable to unregister network service");
    }

    @Override
    public void onServiceRegistered(NsdServiceInfo serviceInfo) {
        Log.d(LOG_TAG, "Service registered. Start listening for connections.");
        final ServerThread serverThread = new ServerThread(serverSocket, socketHandler);
        serverThread.setName("Suas NW Server");
        serverThread.start();
    }

    @Override
    public void onServiceUnregistered(NsdServiceInfo serviceInfo) {

    }

    private static class ServerThread extends Thread {

        private final ServerSocket serverSocket;
        private final ConnectionHandler socketHandler;
        private final AtomicInteger threadId;

        private ServerThread(ServerSocket serverSocket, ConnectionHandler socketHandler) {
            this.serverSocket = serverSocket;
            this.socketHandler = socketHandler;
            this.threadId = new AtomicInteger(0);
        }

        @Override
        public void run() {
            while(true) {
                try {
                    final Socket accept = serverSocket.accept();

                    final WorkerThread workerThread = new WorkerThread(accept, socketHandler);
                    workerThread.setName("SuasNwSocket " + threadId.incrementAndGet());
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

        private final Socket socket;
        private final ConnectionHandler socketHandler;

        WorkerThread(Socket socket, ConnectionHandler socketHandler) {
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
