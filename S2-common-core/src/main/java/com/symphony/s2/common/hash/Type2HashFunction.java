/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.annotation.concurrent.NotThreadSafe;

import com.symphony.s2.common.fault.CodingFault;

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
