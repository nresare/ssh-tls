package com.spotify.sshtls.validator;

import org.junit.Test;

import javax.security.cert.X509Certificate;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Tests CertTool
 */
public class CertToolTest {
  @Test
  public void testParse() throws Exception {
    String certString = new String(
        Files.readAllBytes(Paths.get("src/test/resources/test_noa.crt")));
    X509Certificate cert = CertTool.parse(certString);
    assertEquals("UID=noa", cert.getSubjectDN().toString());
  }
}
