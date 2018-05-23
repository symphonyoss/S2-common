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

package org.symphonyoss.s2.common.dom.json;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.symphonyoss.s2.common.dom.DomWriter;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

@Immutable
public class JsonNull implements IImmutableJsonDomNode
{
  public static final JsonNull            INSTANCE  = new JsonNull();
  
  private static final String             AS_STRING = "null";
  private static final ImmutableByteArray AS_BYTES  = ImmutableByteArray.newInstance(AS_STRING);
  
  private JsonNull()
  {
  }

  @Override
  public IImmutableJsonDomNode immutify()
  {
    return this;
  }

  @Override
  public JsonNull newMutableCopy()
  {
    return this;
  }

  @Override
  public JsonNull writeTo(DomWriter writer, @Nullable String terminator) throws IOException
  {
    writer.writeItem("null", terminator);
    return this;
  }
  
  @Override
  public ImmutableByteArray serialize()
  {
    return AS_BYTES;
  }

  @Override
  public @Nonnull String toString()
  {
    return AS_STRING;
  }
  
  @Override
  public int hashCode()
  {
    return 0;
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof JsonNull;
  }

  @Override
  public int compareTo(IImmutableJsonDomNode other)
  {
    return other instanceof JsonNull ? 0 : -1;
  }
}
