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
/* package */ class DigestHashFunction extends AbstractHashFunction
{
  private final MessageDigest digest_;
  
  /* package */ DigestHashFunction(String digestId)
  {
    try
    {
      digest_ =  MessageDigest.getInstance(digestId);
    }
    catch (NoSuchAlgorithmException e)
    {
      throw new CodingFault(e);
    }
  }

  @Override
  /* package */ byte[] digest(byte[] bytes)
  {
    return digest_.digest(bytes);
  }

  @Override
  /* package */ void update(byte[] bytes)
  {
    digest_.update(bytes);
  }

  @Override
  /* package */ byte[] digest()
  {
    return digest_.digest();
  }
}
