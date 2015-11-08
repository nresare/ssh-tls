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

import com.google.common.annotations.VisibleForTesting;
import com.spotify.sshtls.validator.KeyProvider;
import com.spotify.sshtls.validator.Validator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * A Jersey resource that implements cert checking backed by ssh public key files on disk
 */
@Path("/")
public class ValidatorResource {

  private final Validator validator;
  private static final CertificateFactory CERTIFICATE_FACTORY;

  static {
    try {
      CERTIFICATE_FACTORY = CertificateFactory.getInstance("X509");
    } catch (CertificateException e) {
      throw new RuntimeException("Your environment is broken, doesn't know about X509 certs");
    }
  }

  public ValidatorResource(KeyProvider keyProvider) {
    this.validator = new Validator(keyProvider);
  }

  @Path("/_auth")
  @POST
  public Response handleAuth(String body) {
    String user = validator.validate(parse(body));
    if (user == null) {
      return Response.status(404).build();
    }
    return Response.ok().header("User", user).build();
  }

  @VisibleForTesting
  static X509Certificate parse(String input) {
    InputStream is = new ByteArrayInputStream(input.getBytes());
    try {
      return (X509Certificate) CERTIFICATE_FACTORY.generateCertificate(is);
    } catch (CertificateException e) {
      throw new RuntimeException(e);
    }
  }

}
