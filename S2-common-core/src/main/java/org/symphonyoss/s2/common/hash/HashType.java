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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.symphonyoss.s2.common.exception.InvalidValueException;
import org.symphonyoss.s2.common.fault.CodingFault;

/* package */ class HashType extends AbstractHashType
{
  /* package */ static final int        defaultHashTypeId_ = 1;
  /* package */ static final HashType[] hashTypes_ = new HashType[]
  {
      new HashType(0, null,      0,  new byte[] {},  "0"),
      new DigestHashType(1, "SHA-256", 32, new byte[] {1}, "11"),
      new HashType(2, new AbstractHashFunctionFactory()
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
      
  /* package */ final int           hashTypeId_;
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
  /* package */ HashType(int hashTypeId, @Nullable AbstractHashFunctionFactory hashFunctionFactory, int byteLen,
      byte[] typeIdAsBytes, String typeIdAndLengthAsString)
  {
    super(hashFunctionFactory);
    
    if(typeIdAsBytes.length > 15)
      throw new CodingFault("Hash typeId may not exceed 15 bytes length, (1 digit of Hex)");
    
    hashTypeId_ = hashTypeId;
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
  
  /* package */ static @Nonnull HashType  getHashType(int typeId) throws InvalidValueException
  {
    if(typeId < 0 || typeId > hashTypes_.length - 1)
      throw new InvalidValueException("Invalid hash type ID " + typeId);
    
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

  /* package */ int getHashTypeId()
  {
    return hashTypeId_;
  }
}
