package com.spotify.sshtls.validator;

import java.security.PublicKey;


/**
 * Implementations of this class will provide an PublicKey instance given a username.
 */
public interface KeyProvider {
  PublicKey getKey(String username);
}
