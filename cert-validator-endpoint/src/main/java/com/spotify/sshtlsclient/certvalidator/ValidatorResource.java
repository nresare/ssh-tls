package com.spotify.sshtlsclient.certvalidator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * Created by noa on 04/11/15.
 */
@Path("/")
public class ValidatorResource {

  @Path("/_auth")
  @POST
  public String handleAuth(String body) {
    return "User: noa\n";
  }
}
