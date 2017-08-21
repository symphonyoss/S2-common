/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.hash;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.symphony.s2.common.exception.BadFormatException;
import com.symphony.s2.common.hash.Hash;
import com.symphony.s2.common.hash.HashProvider;

import org.junit.Assert;

public class TestHashProvider
{
  @Test
  public void testType2Hash() throws BadFormatException
  {
    String  plainText = "Hello World!";
    
    Hash hash = HashProvider.getHashOf(2, plainText.getBytes(StandardCharsets.UTF_8));
    
    Assert.assertEquals("Type2 Hash should be 25 bytes", hash.toBytes().length, 25);
    
    System.out.println("Type2 Hashes:");
    
    System.out.println("1234567890123456789012345678901234567890123456");
    System.out.println("SHA1------------------------------------SHA256");
    System.out.println(hash);
    
    for(int i=0 ; i<10 ; i++)
      System.out.println(HashProvider.getHashOf(2, (plainText + i).getBytes(StandardCharsets.UTF_8)));
    
  }
  
  @Test
  public void testCompositeHash() throws BadFormatException
  {
    testCompositeHash("EE54B1846CF852E62EFD3CA1725F17372A39F2C9B04ADA70105F90EE046B650E11",
        "Principal",
        new Hash("DC4F2272D2CF6CFB626266499E31E82A6DEF150826551E3EDBDFCB2E526DDE5F11"),
        "SecurityContext",
        new Hash("7EF0A530C1F499F91BC20270225788D1E1505E7354276ADAE1855FE7E2523E2711"));

    
    testCompositeHash("EE54B1846CF852E62EFD3CA1725F17372A39F2C9B04ADA70105F90EE046B650E11",
        "Principal",
        new Hash("DC4F2272D2CF6CFB626266499E31E82A6DEF150826551E3EDBDFCB2E526DDE5F11"),
        "SecurityContext",
        new Hash("7EF0A530C1F499F91BC20270225788D1E1505E7354276ADAE1855FE7E2523E2711"));   
  }

  private void testCompositeHash(String expect, Object ...parts)
  {
    String hash = HashProvider.getCompositeHashOf(parts).toString();
    String msg = "Expected \"" + expect + "\"\ngot      \"" + hash + "\"";
    
    System.out.println(msg);

    Assert.assertEquals(msg, expect, hash);
  }
}
