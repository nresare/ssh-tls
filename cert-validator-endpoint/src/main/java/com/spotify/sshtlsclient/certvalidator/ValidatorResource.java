package com.spotify.sshtlsclient.certvalidator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * A Jersey resource that implements cert checking backed by ssh public key files on disk
 */
@Path("/")
public class ValidatorResource {

  @Path("/_auth")
  @POST
  public String handleAuth(String body) {
    System.out.println("body: " + body);
    return "User: noa\n";
  }
}
