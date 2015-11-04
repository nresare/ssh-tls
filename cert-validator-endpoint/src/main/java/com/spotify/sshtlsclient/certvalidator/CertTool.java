package com.spotify.sshtlsclient.certvalidator;

import com.google.common.base.Charsets;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

/**
 * Tools for X.509 certificates.
 */
public class CertTool {

  static final KeyFactory rsaKeyFactory;
  static {
    try {
      rsaKeyFactory = KeyFactory.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Can't understand RSA keys, your env is broken", e);
    }
  }
  public static X509Certificate parse(String input)
      throws CertificateException {
    byte[] bytes = input.getBytes(Charsets.UTF_8);
    return X509Certificate.getInstance(bytes);
  }

  public static boolean compare(X509Certificate cert, RSAPublicKeySpec pubKey) {
    try {
      return cert.getPublicKey().equals(rsaKeyFactory.generatePublic(pubKey));
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException("Failed to create RSA public key", e);
    }
  }

}
