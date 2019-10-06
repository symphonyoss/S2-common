/*
 *
 *
 * Copyright 2017-2018 Symphony Communication Services, LLC.
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
import javax.annotation.concurrent.NotThreadSafe;

import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

/**
 * A factory for Hash objects.
 * 
 * THIS CLASS IS NOT THREAD SAFE.
 * 
 * @author Bruce Skingle
 *
 */
@NotThreadSafe
public class HashFactory
{
  private final int                  typeId_;
  private final AbstractHashFunction hashFunction_;

  /**
   * Construct a HashFactory with the current default hash type.
   */
  public HashFactory()
  {
    typeId_ = HashType.defaultHashTypeId_;
    hashFunction_ = HashType.getDefaultHashType().createHashFunction();
  }
  
  /**
   * Construct a HashFactory for the given hash type.
   *  
   * @param typeId The hash type of the required factory
   * @throws IllegalArgumentException If the given hash type is invalid.
   */
  public HashFactory(int typeId)
  {
    typeId_ = typeId;
    hashFunction_ = HashType.getHashType(typeId).createHashFunction();
  }
  
  /**
   * Return the hash type ID of the factory.
   * 
   * @return the hash type ID of the factory.
   */
  public int    getHashTypeId()
  {
    return typeId_;
  }
  
  /**
   * Return the hash of the given value.
   * 
   * @param bytes A value to be hashed.
   * @return The hash of the given value.
   */
  public @Nonnull Hash getHashOf(byte[] bytes)
  {
    return new Hash(typeId_, hashFunction_.digest(bytes));
  }
  
  /**
   * Return the hash of the given value.
   * 
   * @param bytes A value to be hashed.
   * @return The hash of the given value.
   */
  public @Nonnull Hash   getHashOf(ImmutableByteArray bytes)
  {
    for(byte b : bytes)
      hashFunction_.update(b);
    
    return new Hash(typeId_, hashFunction_.digest());
  }
  
  /**
   * Return the hash of the given values.
   * 
   * The order of the provided values is significant.
   * 
   * @param parts One or more objects the values of which will be concatenated and hashed.
   * 
   * @return The hash of the given value.
   */
  public @Nonnull Hash   getCompositeHashOf(Object ...parts)
  {
    for(Object part : parts)
    {
      if(part instanceof Hash)
      {
        if(Hash.NIL_HASH.equals(part))
          throw new CodingFault("NIL_HASH (null value) included as element of composite hash");
        
        for(byte b : ((Hash) part).toImmutableByteArray())
          hashFunction_.update(b);
      }
      else if(part instanceof byte[])
      {
        hashFunction_.update(((byte[]) part));
      }
      else if(part instanceof ImmutableByteArray)
      {
        for(byte b : (ImmutableByteArray) part)
          hashFunction_.update(b);
      }
      else
      {
        hashFunction_.update(part.toString().getBytes());
      }
    }
    
    byte[] rawDigestBytes = hashFunction_.digest();
    
    return new Hash(typeId_, rawDigestBytes);
  }
}
