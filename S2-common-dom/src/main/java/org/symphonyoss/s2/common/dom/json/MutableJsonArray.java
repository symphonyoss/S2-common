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

import org.apache.commons.codec.binary.Base64;
import org.symphonyoss.s2.common.immutable.ImmutableByteArray;

import com.google.protobuf.ByteString;

public abstract class MutableJsonArray<T extends MutableJsonArray> extends JsonArray<IJsonDomNode> implements IMutableJsonDomNode
{
  public abstract T add(IJsonDomNode child);
  
  public T add(Boolean value)
  {
    return add(new JsonBoolean(value));
  }
  
  public T add(Long value)
  {
    return add(new JsonLong(value));
  }
  
  public T add(Integer value)
  {
    return add(new JsonInteger(value));
  }
  
  public T add(Double value)
  {
    return add(new JsonDouble(value));
  }
  
  public T add(Float value)
  {
    return add(new JsonFloat(value));
  }
  
  public T add(String value)
  {
    return add(new JsonString(value));
  }
  
  public T add(ByteString value)
  {
    return add(new JsonString(Base64.encodeBase64URLSafeString(value.toByteArray())));
  }
  
  public T add(ImmutableByteArray value)
  {
    return add(new JsonString(value.toBase64String()));
  }
}
