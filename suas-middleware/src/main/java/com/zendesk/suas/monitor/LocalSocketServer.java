package com.zendesk.suas.monitor;

import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.BindException;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

public class LocalSocketServer {
  private static final String WORKER_THREAD_NAME_PREFIX = "StethoWorker";
  private static final int MAX_BIND_RETRIES = 2;
  private static final int TIME_BETWEEN_BIND_RETRIES_MS = 1000;

  private final String mFriendlyName;
  private final String mAddress;
  private final SocketHandler mSocketHandler;
  private final AtomicInteger mThreadId = new AtomicInteger();

  private Thread mListenerThread;
  private boolean mStopped;
  private LocalServerSocket mServerSocket;

  /**
   * @param friendlyName identifier to help debug this server, used for naming threads and such.
   * @param address the local socket address to listen on.
   * @param socketHandler functional handler once a socket is accepted.
   */
  public LocalSocketServer(
      String friendlyName,
      String address,
      SocketHandler socketHandler) {
    mFriendlyName = friendlyName;
    mAddress = address;
    mSocketHandler = socketHandler;
  }

  public String getName() {
    return mFriendlyName;
  }

  /**
   * Binds to the address and listens for connections.
   * <p/>
   * If successful, this thread blocks forever or until {@link #stop} is called, whichever
   * happens first.
   *
   * @throws IOException Thrown on failure to bind the socket.
   */
  public void run() throws IOException {
    synchronized (this) {
      if (mStopped) {
        return;
      }
      mListenerThread = Thread.currentThread();
    }

    listenOnAddress(mAddress);
  }

  private void listenOnAddress(String address) throws IOException {
    mServerSocket = bindToSocket(address);
    Log.i("SERVER", "Listening on @" + address);

    while (!Thread.interrupted()) {
      try {
        // Use previously accepted socket the first time around, otherwise wait to
        // accept another.
        LocalSocket socket = mServerSocket.accept();

        // Start worker thread
        Thread t = new WorkerThread(socket, mSocketHandler);
        t.setName(
            WORKER_THREAD_NAME_PREFIX +
            "-" + mFriendlyName +
            "-" + mThreadId.incrementAndGet());
        t.setDaemon(true);
        t.start();
      } catch (SocketException se) {
        // ignore exception if interrupting the thread
        if (Thread.interrupted()) {
          break;
        }
        se.printStackTrace();
      } catch (InterruptedIOException ex) {
        ex.printStackTrace();
        break;
      } catch (IOException e) {
        e.printStackTrace();
        //LogUtil.w(e, "I/O error initialising connection thread");
        break;
      }
    }

    //LogUtil.i("Server shutdown on @" + address);
  }

  /**
   * Stops the listener thread and unbinds the address.
   */
  public void stop() {
    synchronized (this) {
      mStopped = true;
      if (mListenerThread == null) {
        return;
      }
    }

    mListenerThread.interrupt();
    try {
      if (mServerSocket != null) {
        mServerSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      // Don't care...
    }
  }

  private static LocalServerSocket bindToSocket(String address) throws IOException {
    int retries = MAX_BIND_RETRIES;
    IOException firstException = null;
    do {
      try {
        return new LocalServerSocket(address);
      } catch (BindException be) {
        if (firstException == null) {
          firstException = be;
        }
        sleepUninterruptibly(TIME_BETWEEN_BIND_RETRIES_MS);
      }
    } while (retries-- > 0);

    throw firstException;
  }

  public static void sleepUninterruptibly(long millis) {
    long remaining = millis;
    long startTime = System.currentTimeMillis();
    do {
      try {
        Thread.sleep(remaining);
        return;
      } catch (InterruptedException e) {
        long sleptFor = System.currentTimeMillis() - startTime;
        remaining -= sleptFor;
      }
    } while (remaining > 0);
  }

  private static class WorkerThread extends Thread {
    private final LocalSocket mSocket;
    private final SocketHandler mSocketHandler;

    public WorkerThread(LocalSocket socket, SocketHandler socketHandler) {
      mSocket = socket;
      mSocketHandler = socketHandler;
    }

    @Override
    public void run() {
      try {
        mSocketHandler.onAccepted(mSocket);
      } catch (IOException ex) {

      } finally {
        try {
          mSocket.close();
        } catch (IOException ignore) {
          ignore.printStackTrace();
        }
      }
    }
  }
}
