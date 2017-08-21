/*
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * All Rights Reserved
 */

package com.symphony.s2.common.hash;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.symphony.s2.common.exception.BadFormatException;
import com.symphony.s2.common.fault.CodingFault;

/* package */ class HashType extends AbstractHashType
{
  /* package */ static final int        defaultHashTypeId_ = 1;
  /* package */ static final HashType[] hashTypes_ = new HashType[]
  {
      new HashType(null,      0,  new byte[] {},  "0"),
      new DigestHashType("SHA-256", 32, new byte[] {1}, "11"),
      new HashType(new AbstractHashFunctionFactory()
      {
        @Override
        AbstractHashFunction createHashFunction()
        {
          return new Type2HashFunction();
        }
      }, Type2HashFunction.LENGTH, new byte[] {2}, "21"),
      /* Append new hash types here.
       * 
       * Ensure that the byte encoded typeId matches the position of the
       * DigestSpec in the array and that all forms of typeId match.
       * 
       * DO NOT REMOVE OR CHANGE THE ORDER OF EXISTING TYPES
       */
  };
      
  /* package */ final int           byteLen_;
  /* package */ final byte[]        typeIdAsBytes_;
  /* package */ final String        typeIdAndLengthAsString_;
  
  /**
   * Create a new DigestType.
   * 
   * For the avoidance of doubt, bad things will happen if the three
   * forms of typeId do not match. Add new types with CAUTION.
   * 
   * @param digestId                ID of digest implementation
   * @param byteLen                 Length of encoded digest in bytes.
   * @param typeId                  TypeId as an int.
   * @param typeIdAsBytes           TypeId in encoded byte form.
   * @param typeIdAndLengthAsString TypeId in encoded Hex String form.
   */
  /* package */ HashType(@Nullable AbstractHashFunctionFactory hashFunctionFactory, int byteLen,
      byte[] typeIdAsBytes, String typeIdAndLengthAsString)
  {
    super(hashFunctionFactory);
    
    if(typeIdAsBytes.length > 15)
      throw new CodingFault("Hash typeId may not exceed 15 bytes length, (1 digit of Hex)");
    
    byteLen_ = byteLen;
    typeIdAsBytes_ = typeIdAsBytes;
    typeIdAndLengthAsString_ = typeIdAndLengthAsString;
  }
  
  /* package */ static @Nonnull HashType  getNilHashType()
  {
    return hashTypes_[0];
  }
  
  /* package */ static @Nonnull HashType  getHashType1()
  {
    return hashTypes_[1];
  }

  /* package */ static @Nonnull HashType  getDefaultHashType()
  {
    return hashTypes_[defaultHashTypeId_];
  }
  
  /* package */ static @Nonnull HashType  getHashType(int typeId) throws BadFormatException
  {
    if(typeId < 0 || typeId > hashTypes_.length - 1)
      throw new BadFormatException("Invalid hash type ID " + typeId);
    
    return hashTypes_[typeId];
  }

  /* package */ @Nonnull byte[] encode(int typeId, byte[] rawDigest)
  {
    byte[] hashBytes = new byte[rawDigest.length + typeIdAsBytes_.length + 1];

    int out = 0;
    
    for(int in=0 ; in<rawDigest.length ; in++)
      hashBytes[out++] = rawDigest[in];
    
    for(int in=0 ; in<typeIdAsBytes_.length ; in++)
      hashBytes[out++] = typeIdAsBytes_[in];
    
    hashBytes[out] = (byte) typeIdAsBytes_.length;
    
    return hashBytes;
  }
}
