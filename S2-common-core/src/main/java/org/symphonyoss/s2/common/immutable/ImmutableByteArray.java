/*
 *
 *
 * Copyright 2018 Symphony Communication Services, LLC.
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

package org.symphonyoss.s2.common.immutable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import javax.annotation.concurrent.Immutable;

import com.google.protobuf.ByteString;

/**
 * An immutable container for a byte array.
 * 
 * These classes are unsynchronized but nonetheless are thread safe,
 * they are intended to be used within a single thread, some methods
 * involve creating a wrapper or alternate form of the contents, in
 * cases where the created wrapper is itself immutable it may be stored
 * so that it can be returned to subsequent calls to the same method.
 * 
 * If these methods are called concurrently from multiple threads the
 * creation of the alternate form may happen more than once, resulting
 * in "wasted work" but the results are idempotent and the class is,
 * as far as any external observer is concerned, immutable and thread
 * safe.
 * 
 * @author Bruce Skingle
 *
 */
@Immutable
public abstract class ImmutableByteArray implements Iterable<Byte>
{
  /**
   * An empty (zero length) array of bytes.
   */
  public static final ImmutableByteArray  EMPTY = new ArrayBackedImmutableByteArray(new byte[0]);
  
  /**
   * Return an ImmutableByteArray containing the given data.
   * 
   * This operation involves a defensive copy.
   * 
   * @param bytes The data for the ImmutableByteArray.
   * 
   * @return An ImmutableByteArray containing the given data.
   */
  public static ImmutableByteArray newInstance(byte[] ...bytes)
  {
    return new ArrayBackedImmutableByteArray(bytes);
  }
  
  /**
   * Return an ImmutableByteArray containing the given data.
   * 
   * This operation involves a defensive copy.
   * 
   * @param string The data for the ImmutableByteArray.
   * 
   * @return An ImmutableByteArray containing the given data.
   */
  public static ImmutableByteArray newInstance(String string)
  {
    return new ArrayBackedImmutableByteArray(string);
  }
  
  /**
   * Return an ImmutableByteArray containing the given data.
   * 
   * This operation does not involve a defensive copy.
   * 
   * @param bytes The data for the ImmutableByteArray.
   * 
   * @return An ImmutableByteArray containing the given data.
   */
  public static ImmutableByteArray newInstance(ByteString bytes)
  {
    return new ByteStringImmutableByteArray(bytes);
  }
  
  /**
   * Create a Reader for the contents of this ImmutableByteArray, using the UTF8 character set.
   * 
   * All String data in S2 should be encoded in UTF8.
   * 
   * @return A Reader for the contents of this ImmutableByteArray.
   */
  public Reader getReader()
  {
    return new StringReader(toString());
  }

  /**
   * Create a Reader for the contents of this ImmutableByteArray, using the given character set.
   * 
   * This method creates a new Reader object but this does not involve a copy of the data if that's possible.
   * 
   * @param charset The character set with which the byte data should be interpreted.
   * 
   * @return A Reader for the contents of this ImmutableByteArray.
   */
  public Reader getReader(Charset charset)
  {
    if(charset.equals(StandardCharsets.UTF_8))
      return getReader();
    
    return createReader(charset);
  }
  
  /**
   * Write the contents of this ByteArray to the given OutputStream.
   * 
   * @param out and OutputStream to which the contents of this ByteArray are to be written.
   * 
   * @throws IOException If there is an IO error.
   */
  public abstract void write(OutputStream out) throws IOException;
  
  /**
   * Create a Reader instance with the given character set.
   * 
   * @param charset The character set with which the byte data should be interpreted.
   * 
   * @return A Reader instance with the given character set.
   */
  protected abstract Reader createReader(Charset charset);

  /**
   * Return the contents of the byte array as a UTF8 String.
   * 
   * @return The contents of the byte array as a UTF8 String.
   */
  @Override
  public abstract String toString();

  /**
   * Return the contents of the byte array as a Base64UrlSafe String.
   * 
   * @return The contents of the byte array as a Base64UrlSafe String.
   */
  public abstract String toBase64UrlSafeString();
  
  /**
   * Return the contents of the byte array as a Base64 (standard encoding) String.
   * 
   * @return The contents of the byte array as a Base64 (standard encoding) String.
   */
  public abstract String toBase64String();
  
  /**
   * Return the contents of the byte array as a ByteString.
   * 
   * @return The contents of the byte array as a ByteString.
   */
  public abstract ByteString toByteString();
  
  /**
   * Return a copy of the contents as a byte array.
   * 
   * @return A copy of the contents.
   */
  public abstract byte[] toByteArray();

  /**
   * Create an InputStream for the contents of this ImmutableByteArray.
   * 
   * @return An InputStream for the contents of this ImmutableByteArray.
   */
  public abstract InputStream getInputStream();

  /**
   * Return the number of bytes in this ImmutableByteArray.
   * 
   * @return The number of bytes in this ImmutableByteArray.
   */
  public abstract int length();
  
  /**
   * Return the byte at the given index.
   * 
   * @param index An index.
   * 
   * @return The byte at that index.
   * @throws IndexOutOfBoundsException if the index is negative or greater or equal to length()
   */
  public abstract byte byteAt(int index);
  


  public abstract void arraycopy(int index, byte[] iv, int i, int length);
  


  @Override
  public int hashCode()
  {
    int   h=0;
    
    for(int i=0 ; i<length() ; i++)
    {
      h = 31 * h + byteAt(i);
    }
    return h;
  }

  @Override
  public boolean equals(Object obj)
  {
    if(obj instanceof ImmutableByteArray)
    {
      ImmutableByteArray other = (ImmutableByteArray) obj;
      
      if(other.length() == length())
      {
        for(int i=0 ; i<length() ; i++)
        {
          if(byteAt(i) != other.byteAt(i))
          {
            return false;
          }
        }
        return true;
      }
    }
    return false;
  }
}
