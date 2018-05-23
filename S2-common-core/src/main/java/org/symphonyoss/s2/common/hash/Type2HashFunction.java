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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.concurrent.NotThreadSafe;

import org.symphonyoss.s2.common.fault.CodingFault;

@NotThreadSafe
/* package */ class Type2HashFunction extends AbstractHashFunction
{
  /* package */ static final int LENGTH = 23;
  
  private final MessageDigest sha256Digest_;
  private final MessageDigest sha1Digest_;
  
  /* package */ Type2HashFunction()
  {
    try
    {
      sha1Digest_ =  MessageDigest.getInstance("SHA1");
      sha256Digest_ =  MessageDigest.getInstance("SHA-256");
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new CodingFault(e);
    }
  }

  @Override
  /* package */ byte[] digest(byte[] bytes)
  {
    update(bytes);
    return digest();
  }

  @Override
  /* package */ void update(byte[] bytes)
  {
    sha256Digest_.update(bytes);
  }

  @Override
  /* package */ void update(byte b)
  {
    sha256Digest_.update(b);
  }

  @Override
  /* package */ byte[] digest()
  {
    byte[] sha256Hash = sha256Digest_.digest();
    byte[] sha1Hash = sha1Digest_.digest(sha256Hash);
    byte[] type1Hash = new byte[LENGTH];
    int    i=0;
    
    while(i<sha1Hash.length)
    {
      type1Hash[i] = sha1Hash[i];
      i++;
    }
    
    /* Fill out the extra space with some bytes from the sha256 hash
     * Note that we are relying on the sha256 hash being long enough, which it is.
     */
    while(i<LENGTH)
    {
      type1Hash[i] = sha256Hash[i];
      i++;
    }
    
    return type1Hash;
  }
}
