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

package com.spotify.sshtls.validator;

import com.spotify.daemon.MainLoop;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * A simple standalone main class for the validator jersey endpoint.
 */
public class SimpleMain {
  private static final int PORT = 62911;


  public static void main(String[] args) throws IOException {
    ResourceConfig rc = new ResourceConfig();
    KeyProvider keyProvider = new FileKeyProvider("test/resources/keys");
    ValidatorResource validatorResource = new ValidatorResource(keyProvider);
    rc.registerInstances(validatorResource);
    URI baseURI = URI.create("http://0.0.0.0:" + PORT + "/");
    HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseURI, rc);

    // for now just a loop that runs until it gets the TERM signal.
    final MainLoop mainLoop = MainLoop.newInstance();
    mainLoop.run();

    server.shutdownNow();
  }
}
