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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import javax.annotation.concurrent.Immutable;

import org.apache.commons.codec.binary.Base64;
import org.symphonyoss.s2.common.reader.ByteStringInputStream;
import org.symphonyoss.s2.common.reader.ByteStringReader;

import com.google.protobuf.ByteString;

@Immutable
class ByteStringImmutableByteArray extends ImmutableByteArray
{
  private final ByteString bytes_;
  private String           stringValue_;
  private String           base64UrlSafeValue_;
  private String           base64Value_;

  ByteStringImmutableByteArray(ByteString bytes)
  {
    bytes_ = bytes;
  }

  @Override
  public Reader createReader(Charset charset)
  {
    return new ByteStringReader(bytes_, charset);
  }

  @Override
  public InputStream getInputStream()
  {
    return new ByteStringInputStream(bytes_);
  }

  @Override
  public void write(OutputStream out) throws IOException
  {
    bytes_.writeTo(out);
  }

  @Override
  public String toString()
  {
    if(stringValue_ == null)
      stringValue_ = new String(bytes_.toByteArray(), StandardCharsets.UTF_8); // involves 2 copies, sigh.
    
    return stringValue_;
  }
  
  @Override
  public String toBase64UrlSafeString()
  {
    if(base64UrlSafeValue_ == null)
      base64UrlSafeValue_ = Base64.encodeBase64URLSafeString(toString().getBytes()); // double copy is unavoidable, may as well set string value in the process
    
    return base64UrlSafeValue_;
  }
  
  @Override
  public String toBase64String()
  {
    if(base64Value_ == null)
      base64Value_ = Base64.encodeBase64String(toString().getBytes()); // double copy is unavoidable, may as well set string value in the process
    
    return base64Value_;
  }

  @Override
  public Iterator<Byte> iterator()
  {
    return bytes_.iterator();
  }

  @Override
  public byte[] toByteArray()
  {
    return bytes_.toByteArray();
  }

  @Override
  public ByteString toByteString()
  {
    return bytes_;
  }

  @Override
  public int length()
  {
    return bytes_.size();
  }

  @Override
  public byte byteAt(int index)
  {
    return bytes_.byteAt(index);
  }

  @Override
  public void arraycopy(int index, byte[] dest, int destPos, int length)
  {
    for(int i=0 ; i<length ; i++)
      dest[destPos++] = bytes_.byteAt(index++);
  }
}
