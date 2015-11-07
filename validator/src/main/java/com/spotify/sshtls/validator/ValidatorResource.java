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
import com.google.common.base.Charsets;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.security.PublicKey;

/**
 * A Jersey resource that implements cert checking backed by ssh public key files on disk
 */
@Path("/")
public class ValidatorResource {

  private final KeyProvider keyProvider;

  public ValidatorResource(KeyProvider keyProvider) {
    this.keyProvider = keyProvider;
  }

  @Path("/_auth")
  @POST
  public Response handleAuth(String body) {
    X509Certificate cert;
    cert = parse(body);
    final String username = getUID(cert.getSubjectDN().toString());
    PublicKey key = keyProvider.getKey(username);
    if (key == null || !cert.getPublicKey().equals(key)) {
      return Response.status(404).build();
    }
    return Response.ok().header("User", username).build();
  }

  @VisibleForTesting
  static X509Certificate parse(String input) {
    byte[] bytes = input.getBytes(Charsets.UTF_8);
    try {
      return X509Certificate.getInstance(bytes);
    } catch (CertificateException e) {
      throw new RuntimeException(e);
    }
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
