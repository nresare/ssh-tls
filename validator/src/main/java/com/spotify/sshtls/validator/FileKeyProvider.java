package com.spotify.sshtls.validator;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PublicKey;

/**
 * Created by noa on 05/11/15.
 */
public class FileKeyProvider implements KeyProvider {
  private final String keyDir;

  public FileKeyProvider(String keyDir) {
    this.keyDir = keyDir;
  }

  @Override
  public PublicKey getKey(String username) {
    if (username.contains("/")) {
      throw new RuntimeException("We don't support usernames with / in them.");
    }
    Path p = Paths.get(keyDir, String.format("id_rsa_%s.pub", username));
    try {
      final String keyString = new String(Files.readAllBytes(p), Charsets.UTF_8);
      return SSHKeyParser.parseOpenSSHPubKey(keyString);
    } catch (IOException | InvalidKeyException e) {
      return null;
    }
  }
}
