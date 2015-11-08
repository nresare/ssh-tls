package com.spotify.sshtls.example;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * A KeyManager backed by a single X.509 server cert and private key.
 */
public class SimpleKeyManager extends X509ExtendedKeyManager {

  static final KeyFactory RSA_KEY_FACTORY;
  static {
    try {
      RSA_KEY_FACTORY = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Can't understand RSA keys, your env is broken", e);
    }
  }

  static final Base64.Decoder DECODER = Base64.getMimeDecoder();

  private final PrivateKey privateKey;
  private final X509Certificate[] certificateChain;

  public SimpleKeyManager(File cert, File privateKey) throws Exception {
    EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buildPKCS8Key(privateKey));
    this.privateKey = RSA_KEY_FACTORY.generatePrivate(keySpec);

    final CertificateFactory cf = CertificateFactory.getInstance("X509");
    final X509Certificate c = (X509Certificate)cf.generateCertificate(new FileInputStream(cert));
    this.certificateChain = new X509Certificate[] {c};
  }

  /**
   * Return a byte array of containing an RSA private key in ASN.1 PKCS#8 format as specified in
   * RFC5208 Section 5. If the provided file contains a bare ASN.1 encoded RSA key sequence such
   * as the output of "openssl genrsa", the proper PKCS#8 header is added.
   *
   * @param privateKey a File containing a Base64 encoded private key.
   * @return the binary key ready to be fed to PKCS8EncodedKeySpec
   * @throws IOException
   */
  private static byte[] buildPKCS8Key(File privateKey) throws IOException {
    final String s = new String(Files.readAllBytes(privateKey.toPath()));
    if (s.contains("--BEGIN PRIVATE KEY--")) {
      return DECODER.decode(s.replaceAll("-----\\w+ PRIVATE KEY-----", ""));
    }
    if (!s.contains("--BEGIN RSA PRIVATE KEY--")) {
      throw new RuntimeException("Invalid cert format: "+ s);
    }

    final byte[] innerKey = DECODER.decode(s.replaceAll("-----\\w+ RSA PRIVATE KEY-----", ""));
    final byte[] result = new byte[innerKey.length + 26];
    // the PKCS#8 header is static, you just need to replace two 16 bit offsets.
    System.arraycopy(DECODER.decode("MIIEvAIBADANBgkqhkiG9w0BAQEFAASC"), 0, result, 0, 24);
    System.arraycopy(BigInteger.valueOf(result.length - 4).toByteArray(), 0, result, 2, 2);
    System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, result, 24, 2);
    System.arraycopy(innerKey, 0, result, 26, innerKey.length);
    return result;
  }

  @Override
  public String[] getClientAliases(String keyType, Principal[] issuers) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String[] getServerAliases(String keyType, Principal[] issuers) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
    throw new UnsupportedOperationException("Not implemented");
  }

  @Override
  public X509Certificate[] getCertificateChain(String alias) {
    return certificateChain;
  }

  @Override
  public PrivateKey getPrivateKey(String alias) {
    return privateKey;
  }

  @Override
  public String chooseEngineServerAlias(String keyType, Principal[] issuers, SSLEngine engine) {
    return "AN_ALIAS";
  }
}
