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
public class JsonValue<T,N extends JsonValue<T,N>> implements IImmutableJsonDomNode
{
  private final @Nonnull T                  value_;
  private final @Nonnull String             quotedValue_;
  private final @Nonnull String             asString_;
  private final @Nonnull ImmutableByteArray asBytes_;
  
  public JsonValue(@Nonnull T value, @Nonnull String quotedValue)
  {
    value_ = value;
    quotedValue_ = quotedValue;
    asString_ =value_.toString();
    asBytes_ = ImmutableByteArray.newInstance(asString_);
  }

  @Override
  public IImmutableJsonDomNode immutify()
  {
    return this;
  }

  @Override
  public JsonValue<T, N> newMutableCopy()
  {
    return this;
  }

  public @Nonnull T getValue()
  {
    return value_;
  }

  public @Nonnull String getQuotedValue()
  {
    return quotedValue_;
  }

  @SuppressWarnings("unchecked")
  @Override
  public N writeTo(DomWriter writer, @Nullable String terminator) throws IOException
  {
    writer.writeItem(quotedValue_, terminator);
    return (N) this;
  }
  
  @Override
  public @Nonnull ImmutableByteArray serialize()
  {
    return asBytes_;
  }
  
  @Override
  public @Nonnull String toString()
  {
    return asString_;
  }
  
  @Override
  public int hashCode()
  {
    return value_.hashCode();
  }

  @Override
  public boolean equals(Object other)
  {
    return other instanceof JsonValue && value_.equals(((JsonValue)other).value_);
  }

  @Override
  public int compareTo(IImmutableJsonDomNode other)
  {
    return value_.toString().compareTo(other.toString());
  }
}
