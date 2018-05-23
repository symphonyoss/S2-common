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

import org.symphonyoss.s2.common.exception.InvalidValueException;
import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

/**
 * A thread safe static alternative to creating a HashFactory.
 * 
 * Note that the methods on this class are synchronized, it would be more
 * performant to create a HashFactory (which is not thread-safe) as
 * opposed to using this class.
 * 
 * @author bruce.skingle
 *
 */
public class HashProvider
{
  private static HashFactory[] factories_;
  
  static
  {
    factories_= new HashFactory[HashType.hashTypes_.length];
    
    try
    {
      for(int i=1 ; i<factories_.length ; i++)
      {
        factories_[i] = new HashFactory(i);
      }
    }
    catch (InvalidValueException e)
    {
      // "Can't happen"
      throw new CodingFault(e);
    }
  }
  
  /**
   * Return the hash of the given value.
   * 
   * @param bytes A value to be hashed.
   * @return The hash of the given value.
   */
  public static @Nonnull Hash getHashOf(byte[] bytes)
  {
    HashFactory factory = factories_[HashType.defaultHashTypeId_];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  /**
   * Return the hash of the given value.
   * 
   * @param bytes A value to be hashed.
   * @return The hash of the given value.
   */
  public static @Nonnull Hash getHashOf(ImmutableByteArray bytes)
  {
    HashFactory factory = factories_[HashType.defaultHashTypeId_];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  /**
   * Return the hash of the given type of the given value.
   * 
   * @param hashType  The type of the required hash 
   * @param bytes     A value to be hashed.
   * @return The hash of the given value.
   * @throws InvalidValueException If the requested hash type is not valid.
   */
  public static @Nonnull Hash getHashOf(int hashType, byte[] bytes) throws InvalidValueException
  {
    if(hashType < 1 || hashType >= factories_.length)
      throw new InvalidValueException("Invalid hash type ID " + hashType);
    
    HashFactory factory = factories_[hashType];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  /**
   * Return the hash of the given type of the given value.
   * 
   * @param hashType  The type of the required hash 
   * @param bytes     A value to be hashed.
   * 
   * @return The hash of the given value.
   * 
   * @throws InvalidValueException If the requested hash type is not valid.
   */
  public static @Nonnull Hash getHashOf(int hashType, ImmutableByteArray bytes) throws InvalidValueException
  {
    if(hashType < 1 || hashType >= factories_.length)
      throw new InvalidValueException("Invalid hash type ID " + hashType);
    
    HashFactory factory = factories_[hashType];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  /**
   * Return the hash of the given type of the given values.
   * 
   * The order of the provided values is significant.
   * 
   * @param hashType  The type of the required hash 
   * @param parts One or more objects the values of which will be concatenated and hashed.
   * 
   * @return The hash of the given value.
   * 
   * @throws InvalidValueException If the requested hash type is not valid.
   */
  public static @Nonnull Hash getCompositeHashOf(int hashType, Object ...parts) throws InvalidValueException
  {
    if(hashType < 1 || hashType >= factories_.length)
      throw new InvalidValueException("Invalid hash type ID " + hashType);
    
    HashFactory factory = factories_[hashType];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
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
  public static @Nonnull Hash getCompositeHashOf(Object ...parts)
  {
    HashFactory factory = factories_[HashType.defaultHashTypeId_];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
  
  /**
   * Return the type 1 hash of the given values.
   * 
   * The order of the provided values is significant.
   * 
   * @param parts One or more objects the values of which will be concatenated and hashed.
   * 
   * @return The hash of the given value.
   */
  public static @Nonnull Hash getType1CompositeHashOf(Object ...parts)
  {
    HashFactory factory = factories_[1];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
}
