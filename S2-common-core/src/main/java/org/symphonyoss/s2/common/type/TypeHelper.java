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

package org.symphonyoss.s2.common.type;

import java.nio.ByteBuffer;
import java.time.Instant;

import org.symphonyoss.s2.common.fault.TransactionFault;
import org.symphonyoss.s2.common.hash.Hash;
import org.symphonyoss.s2.common.i18n.Language;

import com.google.protobuf.ByteString;

public class TypeHelper
{
  public static Hash newHash(ByteString byteString)
  {
    return Hash.newInstance(byteString);
  }

  public static Hash newHash(String string)
  {
    return Hash.newInstance(string);
  }
  
  public static ByteString convertToByteString(Hash hash)
  {
    return hash.toByteString();
  }

  public static Language newLanguage(String languageTag)
  {
    return Language.newInstance(languageTag);
  }

  public static String convertToString(Language language)
  {
    return language.toString();
  }
  
  public static Instant newInstant(ByteString byteString)
  {
    if(byteString.size() != 16)
      throw new TransactionFault("Instant must be 16 bytes");
    
    ByteBuffer  bytes = ByteBuffer.allocate(16);
    
    byteString.copyTo(bytes);
    bytes.flip();

    return Instant.ofEpochSecond(bytes.getLong(), bytes.getLong());
  }
  
  public static ByteString convertToByteString(Instant instant)
  {
    return convertToInstantByteString(instant.getEpochSecond(), instant.getNano());
  }
  
  public static ByteString convertToInstantByteString(long secs, long nanos)
  {
    ByteBuffer  bytes = ByteBuffer.allocate(16);
    bytes.putLong(secs);
    bytes.putLong(nanos);
    bytes.flip();
    
    return ByteString.copyFrom(bytes);
  }

  public static Instant newInstant(String s)
  {
    String[] parts = s.split("\\.");
    
    if(parts.length != 2)
      throw new TransactionFault("Instant values are SSS.NNN where SSS is seconds and NNN is ns");
    
    try
    {
      return Instant.ofEpochSecond(Long.parseLong(parts[0]), Long.parseLong(parts[0]));
    }
    catch(NumberFormatException e)
    {
      throw new TransactionFault("Invalid Instant value \"" + s + "\"", e);
    }
  }
  
  public static String convertToString(Instant instant)
  {
    return convertToInstantString(instant.getEpochSecond(), instant.getNano());
  }
  
  public static String convertToInstantString(long secs, long nanos)
  {
    return String.format("%d.%d", secs, nanos);
  }
}
