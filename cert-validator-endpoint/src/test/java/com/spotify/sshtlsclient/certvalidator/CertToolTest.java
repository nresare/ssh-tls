package com.spotify.sshtlsclient.certvalidator;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.RSAPublicKeySpec;
import javax.security.cert.X509Certificate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests CertTool
 */
public class CertToolTest {
  @Test
  public void testParse() throws Exception {
    String certString = new String(
        Files.readAllBytes(Paths.get("src/test/resources/test.crt")));
    X509Certificate cert = CertTool.parse(certString);
    assertEquals("UID=noa", cert.getSubjectDN().toString());
  }

  @Test
  public void testCompare() throws Exception {
    final Path keyPath = Paths.get("src/test/resources/id_rsa.pub");
    String pubKeyString = new String(Files.readAllBytes(keyPath));
    RSAPublicKeySpec pubKey = SSHKeyParser.parseOpenSSHPubKey(pubKeyString);

    String certString = new String(
        Files.readAllBytes(Paths.get("src/test/resources/test.crt")));
    X509Certificate cert = CertTool.parse(certString);

    assertTrue(CertTool.compare(cert, pubKey));
  }


}
