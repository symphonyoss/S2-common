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

package org.symphonyoss.s2.common.type;

import java.net.MalformedURLException;
import java.net.URL;
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
    if(byteString.size() != 12)
      throw new TransactionFault("Instant must be 12 bytes");
    
    ByteBuffer  bytes = ByteBuffer.allocate(12);
    
    byteString.copyTo(bytes);
    bytes.flip();

    return Instant.ofEpochSecond(getLong(byteString, 0), getInt(byteString, 8));
  }
  
  public static ByteString convertToByteString(Instant instant)
  {
    return convertToInstantByteString(instant.getEpochSecond(), instant.getNano());
  }
  
  public static ByteString convertToInstantByteString(long secs, int nanos)
  {
    byte[]  bytes = new byte[12];
    put(secs, bytes, 0);
    put(nanos, bytes, 8);
    
    return ByteString.copyFrom(bytes);
  }

  public static Instant newInstant(String s)
  {
    String[] parts = s.split("\\.");
    
    if(parts.length != 2)
      throw new TransactionFault("Instant values are SSS.NNN where SSS is seconds and NNN is ns");
    
    try
    {
      return Instant.ofEpochSecond(Long.parseLong(parts[0]), Integer.parseInt(parts[0]));
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

  public static URL newURL(String url)
  {
    try
    {
      return new URL(url);
    }
    catch (MalformedURLException e)
    {
      throw new TransactionFault("Invalid URL value \"" + url + "\"", e);
    }
  }
  
  /**
   * Return the given value as a sequence of bytes, MSB first.
   * 
   * @param value   A long value.
   * @return        A newly allocated byte array containing the given value.
   */
  public static byte[] convertToByteArray(long value)
  {
    byte[] buf = new byte[8];
    
    put(value, buf, 0);
    
    return buf;
  }
  
  /**
   * Put the given value as a sequence of bytes, MSB first to the given buffer at the given offset.
   * Note that the caller must ensure that there is enough space in the buffer, otherwise an
   * ArrayIndexOutOfBounds exception will result.
   * 
   * @param value   A long value.   
   * @param buf     A byte array buffer.
   * @param offset  The starting offset within buf where the value should be written.
   */
  public static void put(long value, byte[] buf, int offset)
  {
    for (int i = offset+7; i >= offset; i--)
    {
      buf[i] = (byte) (value & 0xFF);
      value >>= 8;
    }
  }

  /**
   * Read a long value from the given buffer starting at the given offset.
   * The data is expected to be encoded MSB first.
   * @param buf     A byte array containing the required value
   * @param offset  The offset from within the buffer of the first byte of the data
   * @return        The required value.
   */
  public static long getLong(byte[] buf, int offset)
  {
    long result = 0;
    for (int i = offset; i < offset+8; i++)
    {
      result <<= 8;
      result |= (buf[i] & 0xFF);
    }
    return result;
  }
  
  /**
   * Read a long value from the given buffer starting at the given offset.
   * The data is expected to be encoded MSB first.
   * @param buf     A ByteString containing the required value
   * @param offset  The offset from within the buffer of the first byte of the data
   * @return        The required value.
   */
  public static long getLong(ByteString buf, int offset)
  {
    long result = 0;
    for (int i = offset; i < offset+8; i++)
    {
      result <<= 8;
      result |= (buf.byteAt(i) & 0xFF);
    }
    return result;
  }
  
  /**
   * Return the given value as a sequence of bytes, MSB first.
   * 
   * @param value   A long value.
   * @return        A newly allocated byte array containing the given value.
   */
  public static byte[] convertToByteArray(int value)
  {
    byte[] buf = new byte[4];
    
    put(value, buf, 0);
    
    return buf;
  }
  
  /**
   * Put the given value as a sequence of bytes, MSB first to the given buffer at the given offset.
   * Note that the caller must ensure that there is enough space in the buffer, otherwise an
   * ArrayIndexOutOfBounds exception will result.
   * 
   * @param value   A long value.   
   * @param buf     A byte array buffer.
   * @param offset  The starting offset within buf where the value should be written.
   */
  public static void put(int value, byte[] buf, int offset)
  {
    for (int i = offset+3; i >= offset; i--)
    {
      buf[i] = (byte) (value & 0xFF);
      value >>= 8;
    }
  }

  /**
   * Read an int value from the given buffer starting at the given offset.
   * The data is expected to be encoded MSB first.
   * @param buf     A byte array containing the required value
   * @param offset  The offset from within the buffer of the first byte of the data
   * @return        The required value.
   */
  public static int getInt(byte[] buf, int offset)
  {
    int result = 0;
    for (int i = offset; i < offset+4; i++)
    {
      result <<= 8;
      result |= (buf[i] & 0xFF);
    }
    return result;
  }
  
  /**
   * Read an int value from the given buffer starting at the given offset.
   * The data is expected to be encoded MSB first.
   * @param buf     A ByteString containing the required value
   * @param offset  The offset from within the buffer of the first byte of the data
   * @return        The required value.
   */
  public static int getInt(ByteString buf, int offset)
  {
    int result = 0;
    for (int i = offset; i < offset+4; i++)
    {
      result <<= 8;
      result |= (buf.byteAt(i) & 0xFF);
    }
    return result;
  }
}
