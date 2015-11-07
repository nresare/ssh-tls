/*
 * Copyright (c) 2015 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.spotify.sshtls.example;

import com.google.common.io.ByteStreams;
import com.spotify.sshagentproxy.AgentProxies;
import com.spotify.sshagentproxy.AgentProxy;
import com.spotify.sshagentproxy.Identity;
import com.spotify.sshtls.client.SshAgentSSLSocketFactory;

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

  private static final String REMOTE_URL = "https://some.service";

  private Client() throws IOException {
    AgentProxy agentProxy = AgentProxies.newInstance();
    List<Identity> identities = agentProxy.list();
    if (identities.size() > 1) {
      throw new RuntimeException("You have no keys in the ssh-agent");
    }
    Identity firstIdentity = identities.get(0);
    socketFactory = new SshAgentSSLSocketFactory(
        agentProxy, firstIdentity, System.getProperty("user.name"));
  }

  void run() throws Exception {
    HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);

    URL url = new URL(REMOTE_URL);
    URLConnection conn = url.openConnection();

    System.out.println(new String(ByteStreams.toByteArray(conn.getInputStream())));
  }

  public static void main(String[] args) throws Exception {
    Client client = new Client();
    client.run();
  }
}
