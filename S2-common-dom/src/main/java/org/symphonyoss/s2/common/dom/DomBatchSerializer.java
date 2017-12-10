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

package org.symphonyoss.s2.common.dom;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;

import org.symphonyoss.s2.common.fault.CodingFault;

public class DomBatchSerializer extends DomConsumer<DomBatchSerializer> implements Closeable
{
  private CharArrayWriter out_;
  private DomWriter writer_;

  protected DomBatchSerializer(boolean compactMode, boolean canonicalMode)
  {
    super(compactMode, canonicalMode);
    
    out_ = new CharArrayWriter();
    writer_ = DomWriter.newBuilder(out_, this).build();
  }

  public static class Builder extends DomConsumer.Builder<Builder>
  {
    private Builder()
    {}

    private Builder(IDomConsumerOrBuilder initial)
    {
      super(initial);
    }
    
    public DomBatchSerializer build()
    {
      return new DomBatchSerializer(isCompactMode(), isCanonicalMode());
    }
  }
  
  public static Builder newBuilder()
  {
    return new Builder();
  }
  
  public static Builder newBuilder(IDomConsumerOrBuilder initial)
  {
    return new Builder(initial);
  }
  
  public void serialize(IDomNode node)
  {
    try
    {
      writer_.write(node);
      writer_.flush();
    }
    catch(IOException e)
    {
      throw new CodingFault(e);
    }
  }

  @Override
  public void close() throws IOException
  {
    writer_.close();
    out_.close();
  }

  public char[] toCharArray()
  {
    return out_.toCharArray();
  }

  @Override
  public String toString()
  {
    return out_.toString();
  }
  
  public Reader createReader()
  {
    return new CharArrayReader(out_.toCharArray());
  }
}
