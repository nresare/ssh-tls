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

package com.spotify.sshtlsclient;

import com.spotify.sshagentproxy.Identity;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class X509CertificateFactoryTest {

  private static final SshAgentContentSigner signer = mock(SshAgentContentSigner.class);
  private static final Identity identity = mock(Identity.class);

  @Test
  public void test() throws Exception {
//    final Certificate certificate = X509CertificateFactory.get(signer, identity, "dxia");
//    System.out.println(certificate);
  }
}
