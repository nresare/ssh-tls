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

import com.google.common.annotations.VisibleForTesting;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

/**
 * A Validator validates a X509Certificate instances against a set of ssh public keys.
 */
public class Validator {

  private final KeyProvider keyProvider;

  /**
   * Constructs a new Validator with the given KeyProvider for ssh public key lookups.
   * @param keyProvider the KeyProvider to relay key lookup requests to.
   */
  public Validator(KeyProvider keyProvider) {
    this.keyProvider = keyProvider;
  }

  /**
   * Validate the  X509Certificate instance. A certificate is considered valid if the public
   * key that the KeyProvider in the instance for this username matches the public key of the
   * certificate.
   * The username is extracted from the UID field of the Subject Distinguished Name field of the
   * certificate.
   *
   * @param certificate the X509Certificate instance to validate
   * @return the username of the validated user if validation succeeded, else null
   */
  public String validate(X509Certificate certificate) {
    final String username = getUID(certificate.getSubjectDN().toString());
    PublicKey key = keyProvider.getKey(username);
    if (key == null) {
      return null;
    }
    if (certificate.getPublicKey().equals(key)) {
      return username;
    }
    return null;
  }

  @VisibleForTesting
  static String getUID(String dn) {
    try {
      LdapName ln = new LdapName(dn);
      for (Rdn rdn : ln.getRdns()) {
        if (rdn.getType().equalsIgnoreCase("UID")) {
          return rdn.getValue().toString();
        }
      }
      throw new RuntimeException("DN doesn't contain an UID field: " + dn);
    } catch (InvalidNameException e) {
      throw new RuntimeException("failed to parse DN: " + dn, e);
    }
  }
}
