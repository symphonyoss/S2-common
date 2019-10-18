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
import org.symphonyoss.s2.common.type.provider.IValueProvider;

/**
 * An immutable JsonDomNode for a simple value.
 * 
 * @author Bruce Skingle
 *
 * @param <T> The type of the value
 * @param <N> The concrete type of the node so that fluent methods are possible.
 */
@Immutable
public class JsonValue<T,N extends JsonValue<T,N>> implements IImmutableJsonDomNode, IValueProvider
{
  private final @Nonnull T                  value_;
  private final @Nonnull String             quotedValue_;
  private String             asString_;
  private ImmutableByteArray asBytes_;
  
  /**
   * Constructor.
   * 
   * @param value       The typed value.
   * @param quotedValue The string value as it appears in JSON, with quotes if necessary.
   */
  public JsonValue(@Nonnull T value, @Nonnull String quotedValue)
  {
    value_ = value;
    quotedValue_ = quotedValue;
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

  /**
   * 
   * @return the typed value.
   */
  public @Nonnull T getValue()
  {
    return value_;
  }

  /**
   * 
   * @return the JSON string value with quotes if necessary.
   */
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
  public synchronized @Nonnull ImmutableByteArray serialize()
  {
    if(asBytes_ == null)
      asBytes_ = ImmutableByteArray.newInstance(toString());
    
    return asBytes_;
  }
  
  @Override
  public synchronized @Nonnull String toString()
  {
    if(asString_ == null)
      asString_ =value_.toString();

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
    return other instanceof JsonValue && value_.equals(((JsonValue<?,?>)other).value_);
  }

  @Override
  public int compareTo(IImmutableJsonDomNode other)
  {
    return value_.toString().compareTo(other.toString());
  }
}
