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

import com.google.common.base.Charsets;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PublicKey;

/**
 * A KeyProvider implementation backed by files in a directory.
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
