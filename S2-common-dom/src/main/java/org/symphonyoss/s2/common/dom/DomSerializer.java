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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.symphonyoss.s2.common.fault.CodingFault;

public class DomSerializer extends DomConsumer<DomSerializer>
{
  protected DomSerializer(boolean compactMode, boolean canonicalMode)
  {
    super(compactMode, canonicalMode);
  }

  public static class Builder extends DomConsumer.Builder<Builder>
  {
    private Builder()
    {}

    private Builder(IDomConsumerOrBuilder initial)
    {
      super(initial);
    }
    
    public DomSerializer build()
    {
      return new DomSerializer(isCompactMode(), isCanonicalMode());
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
  
  public String serialize(DomNode node)
  {
    try
    {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      DomWriter writer = DomWriter.newBuilder(bout, this).build();
      writer.write(node);
      writer.close();
      
      return bout.toString();
    }
    catch(IOException e)
    {
      throw new CodingFault(e);
    }
  }
}
