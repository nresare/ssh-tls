package com.spotify.sshtlsclient;

import com.google.common.io.ByteStreams;
import com.spotify.sshagentproxy.AgentProxies;
import com.spotify.sshagentproxy.AgentProxy;
import com.spotify.sshagentproxy.Identity;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * example client that connects using an ssh backed client cert.
 */
public class Client {
  final SshAgentSSLSocketFactory socketFactory;

  private Client() throws IOException {
    AgentProxy agentProxy = AgentProxies.newInstance();
    List<Identity> identities = agentProxy.list();
    if (identities.size() > 1) {
      throw new RuntimeException("You have no keys in the ssh-agent");
    }
    Identity firstIdentity = identities.get(0);
    socketFactory = new SshAgentSSLSocketFactory(agentProxy, firstIdentity, "noa");
  }

  void run() throws Exception {
    HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);

    URL url = new URL("https://x-deleteme-a1.noa.cloud.spotify.net");
    URLConnection conn = url.openConnection();

    System.out.println(new String(ByteStreams.toByteArray(conn.getInputStream())));
  }

  public static void main(String[] args) throws Exception {
    Client client = new Client();
    client.run();
  }
}
