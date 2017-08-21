/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.hash;

public class DigestHashType extends HashType
{

  DigestHashType(final String digestId, int byteLen, byte[] typeIdAsBytes,
      String typeIdAndLengthAsString)
  {
    super(new AbstractHashFunctionFactory()
    {
      @Override
      AbstractHashFunction createHashFunction()
      {
        return new DigestHashFunction(digestId);
      }
    }, byteLen, typeIdAsBytes, typeIdAndLengthAsString);
  }
}
