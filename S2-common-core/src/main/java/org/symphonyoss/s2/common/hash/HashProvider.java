/*
 *
 *
 * Copyright 2017 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

import org.symphonyoss.s2.common.exception.BadFormatException;
import org.symphonyoss.s2.common.fault.CodingFault;

/**
 * A thread safe static alternative to creating a ThreadFactory.
 * 
 * Note that the methods on this class are synchronized, it would be more
 * performant to create a ThreadFactory (which is not thread-safe) as
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
    catch (BadFormatException e)
    {
      // "Can't happen"
      throw new CodingFault(e);
    }
  }
  
  public static @Nonnull Hash getHashOf(byte[] bytes)
  {
    HashFactory factory = factories_[HashType.defaultHashTypeId_];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  public static @Nonnull Hash getHashOf(int hashType, byte[] bytes) throws BadFormatException
  {
    if(hashType < 1 || hashType >= factories_.length)
      throw new BadFormatException("Invalid hash type ID " + hashType);
    
    HashFactory factory = factories_[hashType];
    
    synchronized (factory)
    {
      return factory.getHashOf(bytes);
    }
  }
  
  public static @Nonnull Hash getCompositeHashOf(int hashType, Object ...parts) throws BadFormatException
  {
    if(hashType < 1 || hashType >= factories_.length)
      throw new BadFormatException("Invalid hash type ID " + hashType);
    
    HashFactory factory = factories_[hashType];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
  
  public static @Nonnull Hash getCompositeHashOf(Object ...parts)
  {
    HashFactory factory = factories_[HashType.defaultHashTypeId_];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
  
  public static @Nonnull Hash getType1CompositeHashOf(Object ...parts)
  {
    HashFactory factory = factories_[1];
    
    synchronized (factory)
    {
      return factory.getCompositeHashOf(parts);
    }
  }
}
