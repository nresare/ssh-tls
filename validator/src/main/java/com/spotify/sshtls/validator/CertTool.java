package com.spotify.sshtls.validator;

import com.google.common.base.Charsets;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

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
}
