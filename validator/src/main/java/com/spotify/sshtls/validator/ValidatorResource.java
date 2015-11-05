package com.spotify.sshtls.validator;

import com.google.common.annotations.VisibleForTesting;

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
    try {
      cert = CertTool.parse(body);
    } catch (CertificateException e) {
      throw new RuntimeException("Failure to parse cert from POST body", e);
    }
    final String username = getUID(cert.getSubjectDN().toString());
    PublicKey key = keyProvider.getKey(username);
    if (key == null || !cert.getPublicKey().equals(key)) {
      return Response.status(404).build();
    }
    return Response.ok().header("User", username).build();
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
