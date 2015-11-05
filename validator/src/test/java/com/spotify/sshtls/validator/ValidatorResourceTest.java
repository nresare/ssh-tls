package com.spotify.sshtls.validator;

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
  public void testGetUID() {
    assertEquals("stack", ValidatorResource.getUID("uid=stack,cn=users,dc=spotify,dc=net"));
  }

  @Test(expected = RuntimeException.class)
  public void testGetUIDNoUID() {
    ValidatorResource.getUID("cn=Test,cn=users,dc=spotify,dc=net");
  }

  @Test
  public void testHandleAuthNotFound() throws IOException {
    ValidatorResource vr = new ValidatorResource(new FileKeyProvider("src/test/resources/keys"));
    String crt = new String(Files.readAllBytes(Paths.get("src/test/resources/test_noa.crt")));
    Response r = vr.handleAuth(crt);
    assertEquals(404, r.getStatus());
  }

  @Test
  public void testHandleAuth() throws IOException {
    ValidatorResource vr = new ValidatorResource(new FileKeyProvider("src/test/resources/keys"));
    String crt = new String(Files.readAllBytes(Paths.get("src/test/resources/test_alice.crt")));
    Response r = vr.handleAuth(crt);
    assertEquals(200, r.getStatus());
    assertEquals("alice", r.getHeaderString("User"));
  }

}
