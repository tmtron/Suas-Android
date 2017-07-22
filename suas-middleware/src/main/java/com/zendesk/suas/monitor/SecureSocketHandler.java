package com.zendesk.suas.monitor;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Credentials;
import android.net.LocalSocket;

import java.io.IOException;

public abstract class SecureSocketHandler implements SocketHandler {
  private final Context mContext;

  public SecureSocketHandler(Context context) {
    mContext = context;
  }

  @Override
  public final void onAccepted(LocalSocket socket) throws IOException {
    try {
      enforcePermission(mContext, socket);
      onSecured(socket);
    } catch (PeerAuthorizationException e) {
    }
  }

  protected abstract void onSecured(LocalSocket socket) throws IOException;

  private static void enforcePermission(Context context, LocalSocket peer)
      throws IOException, PeerAuthorizationException {
    Credentials credentials = peer.getPeerCredentials();

    int uid = credentials.getUid();
    int pid = credentials.getPid();

    String requiredPermission = Manifest.permission.DUMP;
    int checkResult = context.checkPermission(requiredPermission, pid, uid);
    if (checkResult != PackageManager.PERMISSION_GRANTED) {
      throw new PeerAuthorizationException(
          "Peer pid=" + pid + ", uid=" + uid + " does not have " + requiredPermission);
    }
  }
}
