package com.spotify.sshtls.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import sun.security.x509.X509Key;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * A simple proof of concept HTTPS Server.
 * The needed localhost.ks is generated with
 *    keytool -genkey -keyalg RSA -alias selfsigned -keystore localhost.ks -validity 360 -keysize 2048
 */
public class Server {

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

    // initialise the keystore
    char[] password = "simulator".toCharArray();
    KeyStore ks = KeyStore.getInstance("JKS");
    FileInputStream fis = new FileInputStream("example/localhost.ks");
    ks.load(fis, password);

    // setup the key manager factory
    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
    kmf.init(ks, password);

    // setup the HTTPS context and parameters
    KeyManager[] kms = kmf.getKeyManagers();
    kms = new KeyManager[] {kms[0]};
    sslContext.init(kms, new TrustManager[0], null);
    httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));

    httpsServer.start();
  }
}
