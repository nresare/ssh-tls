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
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Tests ValidatorResource
 */

public class ValidatorResourceTest {

  @Test
  public void testHandleAuthNotFound() throws IOException {
    ValidatorResource vr = new ValidatorResource(
        new FileKeyProvider("../validator/src/test/resources/keys"));
    String crt = new String(Files.readAllBytes(Paths.get("src/test/resources/test_noa.crt")));
    Response r = vr.handleAuth(crt);
    assertEquals(404, r.getStatus());
  }

  @Test
  public void testHandleAuth() throws IOException {
    ValidatorResource vr = new ValidatorResource(
        new FileKeyProvider("../validator/src/test/resources/keys"));
    String crt = new String(Files.readAllBytes(Paths.get("src/test/resources/test_alice.crt")));
    Response r = vr.handleAuth(crt);
    assertEquals(200, r.getStatus());
    assertEquals("alice", r.getHeaderString("User"));
  }

}
