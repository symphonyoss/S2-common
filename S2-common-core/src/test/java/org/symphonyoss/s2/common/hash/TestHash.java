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

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;
import org.symphonyoss.s2.common.exception.InvalidValueException;
import org.symphonyoss.s2.common.hash.Hash;
import org.symphonyoss.s2.common.hash.HashFactory;

import junit.framework.AssertionFailedError;

public class TestHash
{
  @Test
  public void testTypeId() throws InvalidValueException
  {
    for(int i=1 ; i<3 ; i++)
    {
      Hash hash = HashProvider.getHashOf(i, "Hello".getBytes());
      
      assertEquals(i, hash.getTypeId());
    }
  }
  
  
  @Test
  public void testCompositeHash() throws InvalidValueException
  {
    String hexStringValue = "84CE13744E13E1DB83F0DFCF8811DB6B7FFFEC51B40A8496936CD0720D37B32811";
    String urlSafeBase64Value = "hM4TdE4T4duD8N_PiBHba3__7FG0CoSWk2zQcg03sygBAQ";
    String base64Value = "hM4TdE4T4duD8N/PiBHba3//7FG0CoSWk2zQcg03sygBAQ==";
    byte[] byteValue = new byte[]
    {
        (byte)0x84, (byte)0xCE, (byte)0x13, (byte)0x74, (byte)0x4E, (byte)0x13, (byte)0xE1, (byte)0xDB,
        (byte)0x83, (byte)0xF0, (byte)0xDF, (byte)0xCF, (byte)0x88, (byte)0x11, (byte)0xDB, (byte)0x6B,
        (byte)0x7F, (byte)0xFF, (byte)0xEC, (byte)0x51, (byte)0xB4, (byte)0x0A, (byte)0x84, (byte)0x96,
        (byte)0x93, (byte)0x6C, (byte)0xD0, (byte)0x72, (byte)0x0D, (byte)0x37, (byte)0xB3, (byte)0x28,
        (byte)0x1, (byte)0x1};
    
    Hash hash = Hash.ofHexString(hexStringValue);
    byte[] result = hash.toImmutableByteArray().toByteArray();
    
//    System.out.println(hash.toString());
//    System.out.println(hash.toStringBase64());

    if(!Arrays.equals(byteValue, result))
        throw new AssertionFailedError("Bytes conversion test failed");
    
    Hash hash2 = Hash.ofHexString("\t  " + hexStringValue + "  \n");

    if(!hash.equals(Hash.ofBase64String(urlSafeBase64Value)))
      throw new AssertionFailedError("equals  urlSafeBase64 test failed");

    if(!hash.equals(Hash.ofBase64String(base64Value)))
      throw new AssertionFailedError("equals  base64 test failed");

    if(hash.hashCode() != hash2.hashCode())
      throw new AssertionFailedError("hashcode test failed");
    
    if(!hash.equals(hash2))
      throw new AssertionFailedError("equals test failed");
    
    if(!hash2.equals(hash))
      throw new AssertionFailedError("reverse equals test failed");
    
    if(!hash.equals(hash))
      throw new AssertionFailedError("reflexive equals test failed");

    
    if(hash.compareTo(hash2) != 0)
      throw new AssertionFailedError("compareTo test failed");
    
    if(hash2.compareTo(hash) != 0)
      throw new AssertionFailedError("reverse compareTo test failed");
    
    if(hash.compareTo(hash) != 0)
      throw new AssertionFailedError("reflexive compareTo test failed");
    
    hash = new Hash(byteValue);
    
    if(!urlSafeBase64Value.equals(hash.toString()))
      throw new AssertionFailedError("String conversion test failed");
    
    if(hash.hashCode() != hash2.hashCode())
      throw new AssertionFailedError("hashcode differential test failed");
    
    if(!hash.equals(hash2))
      throw new AssertionFailedError("differential equals test failed");
    
    if(!hash2.equals(hash))
      throw new AssertionFailedError("reverse differential equals test failed");
    
    HashFactory factory = new HashFactory();
    
    hash = factory.getHashOf(byteValue);
    
    if(hash.hashCode() == hash2.hashCode())
      throw new AssertionFailedError("hashcode not equals test failed");
    
    if(hash.equals(hash2))
      throw new AssertionFailedError("not equals test failed");
    
    if(hash2.equals(hash))
      throw new AssertionFailedError("reverse not equals test failed");
  }
}
