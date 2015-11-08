package com.spotify.sshtls.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

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

    SimpleKeyManager skm = new SimpleKeyManager(
        new File("/Users/noa/slask/cert/localhost.crt"),
        new File("/Users/noa/slask/cert/localhost.key"));

    sslContext.init(new KeyManager[] {skm}, new TrustManager[0], null);
    httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext));

    httpsServer.start();
  }
}
