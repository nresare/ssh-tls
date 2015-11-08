package com.spotify.sshtls.validator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests Validator
 */
public class ValidatorTest {
  @Test
  public void testGetUID() {
    assertEquals("stack", Validator.getUID("uid=stack,cn=users,dc=spotify,dc=net"));
  }

  @Test(expected = RuntimeException.class)
  public void testGetUIDNoUID() {
    Validator.getUID("cn=Test,cn=users,dc=spotify,dc=net");
  }

}
