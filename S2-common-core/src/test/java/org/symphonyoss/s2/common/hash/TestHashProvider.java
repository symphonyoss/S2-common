/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The SSF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.s2.common.hash;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.symphonyoss.s2.common.exception.InvalidValueException;
import org.symphonyoss.s2.common.hash.Hash;
import org.symphonyoss.s2.common.hash.HashProvider;
import org.junit.Assert;

public class TestHashProvider
{
  @Test
  public void testType2Hash() throws InvalidValueException
  {
    String  plainText = "Hello World!";
    
    Hash hash = HashProvider.getHashOf(2, plainText.getBytes(StandardCharsets.UTF_8));
    
    Assert.assertEquals("Type2 Hash should be 25 bytes", hash.toImmutableByteArray().toByteArray().length, 25);
    
    System.out.println("Type2 Hashes:");
    
    System.out.println("1234567890123456789012345678901234567890123456");
    System.out.println("SHA1------------------------------------SHA256");
    System.out.println(hash);
    
    for(int i=0 ; i<10 ; i++)
      System.out.println(HashProvider.getHashOf(2, (plainText + i).getBytes(StandardCharsets.UTF_8)));
    
  }
  
  @Test
  public void testCompositeHash() throws InvalidValueException
  {
    testCompositeHash("EE54B1846CF852E62EFD3CA1725F17372A39F2C9B04ADA70105F90EE046B650E11",
        "Principal",
        Hash.ofHexString("DC4F2272D2CF6CFB626266499E31E82A6DEF150826551E3EDBDFCB2E526DDE5F11"),
        "SecurityContext",
        Hash.ofHexString("7EF0A530C1F499F91BC20270225788D1E1505E7354276ADAE1855FE7E2523E2711"));

    
    testCompositeHash("EE54B1846CF852E62EFD3CA1725F17372A39F2C9B04ADA70105F90EE046B650E11",
        "Principal",
        Hash.ofHexString("DC4F2272D2CF6CFB626266499E31E82A6DEF150826551E3EDBDFCB2E526DDE5F11"),
        "SecurityContext",
        Hash.ofHexString("7EF0A530C1F499F91BC20270225788D1E1505E7354276ADAE1855FE7E2523E2711"));   
  }

  private void testCompositeHash(String expect, Object ...parts)
  {
    String hash = HashProvider.getCompositeHashOf(parts).toStringHex();
    String msg = "Expected \"" + expect + "\"\ngot      \"" + hash + "\"";
    
    System.out.println(msg);

    Assert.assertEquals(msg, expect, hash);
  }
}
