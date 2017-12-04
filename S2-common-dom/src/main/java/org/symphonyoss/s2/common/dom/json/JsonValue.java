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

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

import org.symphonyoss.s2.common.dom.DomWriter;

@Immutable
public class JsonValue<T,N extends JsonValue<T,N>> implements IImmutableJsonDomNode
{
  private final T      value_;
  private final String quotedValue_;
  
  public JsonValue(T value, String quotedValue)
  {
    value_ = value;
    quotedValue_ = quotedValue;
  }

  public T getValue()
  {
    return value_;
  }

  public String getQuotedValue()
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
}
