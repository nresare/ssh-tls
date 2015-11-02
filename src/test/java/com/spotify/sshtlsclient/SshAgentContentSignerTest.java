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

import com.spotify.sshagentproxy.AgentProxy;
import com.spotify.sshagentproxy.Identity;

import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SshAgentContentSignerTest {

  private static final AgentProxy proxy = mock(AgentProxy.class);
  private static final Identity identity = mock(Identity.class);
  private static final ByteArrayOutputStream stream = mock(ByteArrayOutputStream.class);

  @Test
  public void testAlgorithmIdentifier() throws Exception {
    final SshAgentContentSigner signer = new SshAgentContentSigner(proxy, identity);
    assertThat(signer.getAlgorithmIdentifier(),
               equalTo(new DefaultSignatureAlgorithmIdentifierFinder().find("SHA1withRSA")));
  }

  @Test
  public void testGetSignature() throws Exception {
    final SshAgentContentSigner signer = new SshAgentContentSigner(proxy, identity, stream);
    when(proxy.sign(any(Identity.class), any(byte[].class))).thenReturn(new byte[] {1, 2, 3});
    assertThat(signer.getSignature(), equalTo(new byte[] {1, 2, 3}));
  }
}
