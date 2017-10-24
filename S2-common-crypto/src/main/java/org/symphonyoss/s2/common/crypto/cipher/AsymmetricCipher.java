/*
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

package org.symphonyoss.s2.common.crypto.cipher;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.symphonyoss.s2.common.fault.CodingFault;
import org.symphonyoss.s2.common.type.IPersistentEnum;
import org.symphonyoss.s2.common.type.InvalidPersistentEnumException;

public enum AsymmetricCipher implements IPersistentEnum
{
  // These values MUST match the enum of the same name in session.proto
  RSA2048(0),
  RSA1024(1),
  ECC521(2);

  private static Map<Integer, AsymmetricCipher> idMap_ = new HashMap<>();
  
  private final int     id_;

  static
  {
    for(AsymmetricCipher v : values())
    {
      if(idMap_.containsKey(v.toInt()))
        throw new CodingFault("Duplicate id " + v.toInt());
      
      idMap_.put(v.toInt(), v);
    }
  }
  
  private AsymmetricCipher(int id)
  {
    id_ = id;
  }

  @Override
  public int toInt()
  {
    return id_;
  }
  
  /**
   * Return the enum instance represented by the given integer value.
   * 
   * @param id  An integer value which could have been returned from toInt();
   * @return    The enum instance represented by the given value.
   * 
   * @throws InvalidPersistentEnumException If the given value is invalid.
   */
  public static @Nonnull AsymmetricCipher newInstance(int id) throws InvalidPersistentEnumException
  {
    AsymmetricCipher v = idMap_.get(id);
    
    if(v == null)
      throw new InvalidPersistentEnumException("Invalid AsymmetricCipher ID " + id);
    
    return v;
  }
}