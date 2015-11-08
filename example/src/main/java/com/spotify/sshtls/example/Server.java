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

import com.spotify.sshtls.validator.FileKeyProvider;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * A simple proof of concept HTTPS Server that validates client certificates with SSH
 * public keys.
 */
public class Server {

  public static final File CERT = new File("example/src/test/resources/localhost.crt");
  public static final File CERT_KEY = new File("example/src/test/resources/localhost.key");
  public static final String SSH_KEY_DIR = "validator/src/test/resources/keys";

  public static void main(String[] args) throws Exception {

    // setup the socket address
    InetSocketAddress address = new InetSocketAddress(4247);

    // initialise the HTTPS server
    HttpsServer httpsServer = HttpsServer.create(address, 10);
    httpsServer.createContext("/", new HttpHandler() {
      @Override
      public void handle(HttpExchange httpExchange) throws IOException {
        final byte[] hello = "Hello, world\n".getBytes();
        httpExchange.getResponseHeaders().add("Content-type", "text/plain");
        httpExchange.sendResponseHeaders(200, hello.length);
        httpExchange.getResponseBody().write(hello);
      }
    });

    SSLContext sslContext = SSLContext.getInstance("TLS");

    SimpleKeyManager skm = new SimpleKeyManager(CERT, CERT_KEY);

    ValidatingTrustManager vtm = new ValidatingTrustManager(new FileKeyProvider(SSH_KEY_DIR));

    sslContext.init(new KeyManager[] {skm}, new TrustManager[] {vtm}, null);
    httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
      @Override
      public void configure(HttpsParameters params) {
        SSLParameters sslParameters = getSSLContext().getDefaultSSLParameters();
        sslParameters.setNeedClientAuth(true);
        params.setSSLParameters(sslParameters);
      }
    });

    httpsServer.start();
  }
}
