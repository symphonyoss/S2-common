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
import javax.annotation.concurrent.NotThreadSafe;

import org.symphonyoss.s2.common.exception.InvalidValueException;
import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.fault.ProgramFault;

/**
 * A factory for Hash objects.
 * 
 * THIS CLASS IS NOT THREAD SAFE.
 * 
 * @author bruce.skingle
 *
 */
@NotThreadSafe
public class HashFactory
{
  private final int                  typeId_;
  private final AbstractHashFunction hashFunction_;

  public HashFactory()
  {
    typeId_ = HashType.defaultHashTypeId_;
    hashFunction_ = HashType.getDefaultHashType().createHashFunction();
  }
  
  public HashFactory(int typeId) throws InvalidValueException
  {
    typeId_ = typeId;
    hashFunction_ = HashType.getHashType(typeId).createHashFunction();
  }
  
  public int    getHashTypeId()
  {
    return typeId_;
  }
  
  public @Nonnull Hash   getHashOf(byte[] bytes)
  {
    try
    {
      return new Hash(typeId_, hashFunction_.digest(bytes));
    }
    catch (InvalidValueException e)
    {
      throw new ProgramFault("Unexpected hash error", e);
    }
  }
  
  public @Nonnull Hash   getCompositeHashOf(Object ...parts)
  {
 // debug  Logger log_ = LoggerFactory.getLogger(getClass());
 // debug  log_.debug("getCompositeHashOf");
    
    for(Object part : parts)
    {
      if(part instanceof Hash)
      {
// debug        
//        StringBuilder b = new StringBuilder();
//        
//        for(byte bb : ((Hash) part).toBytes())
//        {
//          b.append(String.format("%02X ", 0xFF & bb));
//        }
//        
//        log_.debug("  HASH " + part + " " + b.toString());
        
        if(Hash.NIL_HASH.equals(part))
          throw new CodingFault("NIL_HASH (null value) included as element of composite hash");
        
        hashFunction_.update(((Hash) part).toBytes());
      }
      else if(part instanceof byte[])
      {
        hashFunction_.update(((byte[]) part));
      }
//
//      else if(part instanceof ByteString)
//      {
//        hashFunction_.update(((ByteString) part));
//      }
      else
      {
     // debug  log_.debug("  OBJ  " + part);
        hashFunction_.update(part.toString().getBytes());
      }
    }
    
    try
    {
      byte[] rawDigestBytes = hashFunction_.digest();
      
      return new Hash(typeId_, rawDigestBytes);
    }
    catch (InvalidValueException e)
    {
      throw new ProgramFault("Unexpected hash error", e);
    }
  }
}
