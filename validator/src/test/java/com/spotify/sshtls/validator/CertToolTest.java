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

import org.junit.Test;

import javax.security.cert.X509Certificate;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Tests CertTool
 */
public class CertToolTest {
  @Test
  public void testParse() throws Exception {
    String certString = new String(
        Files.readAllBytes(Paths.get("src/test/resources/test_noa.crt")));
    X509Certificate cert = CertTool.parse(certString);
    assertEquals("UID=noa", cert.getSubjectDN().toString());
  }
}
